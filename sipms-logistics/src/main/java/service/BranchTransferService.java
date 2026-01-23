package service;

import entity.LowStockAlert;
import entity.StockTransferItem;
import entity.StockTransferRequest;
import enums.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Branch;
import model.Product;
import model.ProductInventory;
import model.StockMovement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BranchTransferService {

    private final LowStockAlertRepository lowStockAlertRepository;
    private final ProductInventoryRepository inventoryRepository;
    private final StockTransferRequestRepository stockTransferRequestRepository;
    private final StockTransferRequestRepository transferRequestRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public LowStockAlert createLowStockAlert(ProductInventory inventory) {

        log.warn("LOW STOCK ALERT: Product {} at Branch {} -Stock: {} Minimum: {}",
                inventory.getProduct().getProductName(),
                inventory.getBranch().getBranchName(),
                inventory.getQuantityAvailable(),
                inventory.getMinimumStockLevel());

        LowStockAlert alert = new LowStockAlert();
        alert.setProduct(inventory.getProduct());
        alert.setBranch(inventory.getBranch());
        alert.setCurrentStock(inventory.getQuantityAvailable());
        alert.setMinimumStockLevel(inventory.getMinimumStockLevel());
        alert.setAlertDate(LocalDateTime.now());
        alert.setShortageQuantity(inventory.getMinimumStockLevel() - inventory.getQuantityAvailable());
        alert.setStatus(AlertStatus.NEW);

        if (inventory.getQuantityAvailable() == 0) {
            alert.setSeverity(AlertSeverity.OUT_OF_STOCK);
        } else if (inventory.getQuantityAvailable() < inventory.getMinimumStockLevel()  && inventory.getQuantityAvailable() > 0) {
            alert.setSeverity(AlertSeverity.CRITICAL);
        } else {
            alert.setSeverity(AlertSeverity.WARNING);
        }

        //Send notification to branch manager(Asynchronous)
        return lowStockAlertRepository.save(alert);
    }

    //Find which branch can supply the needed quantity
    @Transactional
    public Optional<Branch> findSourceBranch(
            Long productId,
            Long destinationBranchId,
            Integer requiredQuantity
    ) {
        if (requiredQuantity == null || requiredQuantity <= 0) {
            log.warn("Invalid requiredQuantity: {}", requiredQuantity);
            return Optional.empty();
        }

        log.info("Finding source branch for product {} (need {} units)",
                productId, requiredQuantity);

        List<ProductInventory> availableStock =
                inventoryRepository.findAllByProductId(productId);

        Optional<Branch> sourceBranch = availableStock.stream()
                .filter(inv -> !Objects.equals(inv.getBranch().getId(), destinationBranchId))
                .filter(inv -> inv.getQuantityAvailable() >= requiredQuantity)
                .filter(inv ->
                        inv.getQuantityAvailable() - requiredQuantity >= inv.getMinimumStockLevel()
                )
                .max(Comparator.comparingInt(ProductInventory::getQuantityAvailable))
                .map(ProductInventory::getBranch);

        sourceBranch.ifPresentOrElse(
                b -> log.info("Found source branch {}", b.getBranchName()),
                () -> log.warn("No suitable branch found for product {}", productId)
        );


        return sourceBranch;

    }


    //    Auto create transfer request
    @Transactional
    public StockTransferRequest autoCreateTransferFromAlert(long alertId) {

        log.info("Auto-creating transfer request for alert {}", alertId);

        LowStockAlert alert = lowStockAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        Optional<Branch> sourceBranch = findSourceBranch(
                alert.getProduct().getId(),
                alert.getBranch().getId(),
                alert.getShortageQuantity()
        );

        if (sourceBranch.isEmpty()) {
            log.error("Cannot create transfer: No source branch available");
            alert.setStatus(AlertStatus.ACKNOWLEDGED);
            lowStockAlertRepository.save(alert);
            return null;
        }

        StockTransferRequest transfer = new StockTransferRequest();
        transfer.setTransferNumber(generateTransferNumber());
        transfer.setSourceBranch(sourceBranch.get());
        transfer.setDestinationBranch(alert.getBranch());
        transfer.setStatus(TransferStatus.PENDING_APPROVAL);
        transfer.setPriority(
                alert.getSeverity() == AlertSeverity.OUT_OF_STOCK ?
                        TransferPriority.URGENT :
                        TransferPriority.HIGH
        );

        transfer.setRequestDate(LocalDate.now());
        transfer.setRequestedBy("SYSTEM");

        StockTransferItem item = new StockTransferItem();
        item.setProduct(alert.getProduct());
        item.setRequestedQuantity(alert.getShortageQuantity());
        transfer.addItem(item);

        StockTransferRequest savedTransfer = stockTransferRequestRepository.save(transfer);

        alert.setStatus(AlertStatus.IN_PROGRESS);
        alert.setTransferRequest(savedTransfer);
        alert.setAcknowledgedDate(LocalDateTime.now());
        alert.setAcknowledgedBy("SYSTEM");
        lowStockAlertRepository.save(alert);

        log.info("Created transfer request {} for alert {}", transfer.getTransferNumber(), alertId);

        return savedTransfer;
    }

    @Transactional
    public StockTransferRequest createManualTransferRequest(
            Long sourceBranchId,
            Long destinationBranchId,
            List<TransferItemRequest> items,
            String requestedBy
    ){
        log.info("Creating manual transfer request from Branch {} to Branch {}",
                sourceBranchId, destinationBranchId);

        Branch sourceBranch = branchRepository.findById(sourceBranchId)
                .orElseThrow(() -> new RuntimeException("Source branch not found"));

        Branch destinationBranch = branchRepository.findById(destinationBranchId)
                .orElseThrow(() -> new RuntimeException("Destination branch not found"));

        StockTransferRequest transfer = new StockTransferRequest();
        transfer.setTransferNumber(generateTransferNumber());
        transfer.setSourceBranch(sourceBranch);
        transfer.setDestinationBranch(destinationBranch);
        transfer.setStatus(TransferStatus.PENDING_APPROVAL);
        transfer.setPriority(TransferPriority.MEDIUM);
        transfer.setRequestDate(LocalDate.now());
        transfer.setRequestedBy(requestedBy);

        for (TransferItemRequest itemReq : items) {

            Product product= productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));
            StockTransferItem item = new StockTransferItem();
            item.setProduct(product);
            item.setRequestedQuantity(itemReq.getQuantity());
            transfer.addItem(item);
        }

        return transferRequestRepository.save(transfer);

    }

     // Approve transfer request
    @Transactional
    public void approveTransfer(Long transferId, String approvedBy) {

        log.info("Approved transfer request {}", transferId);

        StockTransferRequest transfer = transferRequestRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer request not found"));

        if (transfer.getStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Transfer request is not pending approval");
        }

        for (StockTransferItem item : transfer.getTransferItems()) {

            ProductInventory sourceInventory = inventoryRepository
                    .findByProductIdAndBranchId(item.getProduct().getId(), transfer.getSourceBranch().getId())
                    .orElseThrow(() -> new RuntimeException("Source inventory not found for product " + item.getProduct().getId()));

            if (sourceInventory.getQuantityAvailable() < item.getRequestedQuantity()) {
                throw new RuntimeException("Insufficient stock in source branch for product " + item.getProduct().getProductName());
            }

            item.setApprovedQuantity(item.getApprovedQuantity());
            item.setUnitCost(sourceInventory.getAverageCost());
        }
            transfer.setStatus(TransferStatus.APPROVED);
            transfer.setApprovalDate(LocalDate.now());
            transfer.setApprovedBy(approvedBy);

            transferRequestRepository.save(transfer);

            log.info("Transfer request {} approved by {}", transfer.getTransferNumber(), approvedBy);

        }

    //Reject transfer request
    @Transactional
    public void rejectTransfer(Long transferId, String rejectedBy, String reason){

        log.info("Rejected transfer request {}", transferId);

        StockTransferRequest transfer = transferRequestRepository.findById(transferId)
                .orElseThrow(() -> new RuntimeException("Transfer request not found"));

        if (transfer.getStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Transfer request is not pending approval");
        }

        transfer.setStatus(TransferStatus.REJECTED);
        transfer.setApprovalDate(LocalDate.now());
        transfer.setApprovedBy(rejectedBy);

        transferRequestRepository.save(transfer);

        log.info("Transfer request {} rejected by {}. Reason: {}", transfer.getTransferNumber(), rejectedBy, reason);
    }

//    Ship items from the source branch
    @Transactional
    public void shipTransfer(Long transferId, String shippedBy, String carrier, String trackingNumber){

        log.info("Shipping transfer request {}", transferId);

        StockTransferRequest transferRequest= transferRequestRepository.findById(transferId)
                .orElseThrow(()-> new RuntimeException("Transfer not found"));

        if(transferRequest.getStatus() != TransferStatus.APPROVED) {
            throw new RuntimeException("Transfer request is not approved for shipping");
        }
            for (StockTransferItem item : transferRequest.getTransferItems()) {
                ProductInventory sourceInventory = inventoryRepository
                        .findByProductIdAndBranchId(item.getProduct().getId(), transferRequest.getSourceBranch().getId())
                        .orElseThrow(() -> new RuntimeException("Source inventory not found for product " + item.getProduct().getId()));

                sourceInventory.setQuantityOnHand(sourceInventory.getQuantityOnHand() - item.getApprovedQuantity());
                sourceInventory.setQuantityAvailable(sourceInventory.getQuantityAvailable() - item.getApprovedQuantity());

                inventoryRepository.save(sourceInventory);

                createStockMovement(
                        item.getProduct(),
                        transferRequest.getSourceBranch(),
                        -item.getApprovedQuantity(),
                        MovementType.TRANSFER_OUT,
                        transferRequest.getTransferNumber(),
                        item.getUnitCost(),
                        "Transfer to " + transferRequest.getDestinationBranch().getBranchName(),
                        shippedBy
                );

                item.setShippedQuantity(item.getApprovedQuantity());
            }

            transferRequest.setStatus(TransferStatus.APPROVED);
            transferRequest.setShippedBy(shippedBy);
            transferRequest.setShippingCarrier(carrier);
            transferRequest.setTrackingNumber(trackingNumber);

            transferRequestRepository.save(transferRequest);

            log.info("Transfer {} shipped. Tracking: {}",
                    transferRequest.getTransferNumber(), trackingNumber);

    }

     //Receive items at destination branch
     @Transactional
     public void receiveTransfer(
             Long transferId,
             String receivedBy,
             List<ReceiptItem> receivedItems
     ) {
         log.info("Receiving transfer request {}", transferId);

         StockTransferRequest transfer = transferRequestRepository.findById(transferId)
                 .orElseThrow(() -> new RuntimeException("Transfer not found"));

         if (transfer.getStatus() != TransferStatus.IN_TRANSIT) {
             throw new RuntimeException("Transfer is not in transit");
         }

         boolean allReceived = true;

         for (ReceiptItem receipt : receivedItems) {
             StockTransferItem item = transfer.getTransferItems().stream()
                     .filter(i -> i.getId().equals(receipt.getItemId()))
                     .findFirst()
                     .orElseThrow(() -> new RuntimeException("Transfer item not found"));

             // Record received quantity
             item.setReceivedQuantity(receipt.getReceivedQuantity());
             item.setDamagedQuantity(receipt.getDamagedQuantity());

             // Add to destination inventory
             ProductInventory destInv = inventoryRepository
                     .findByProductIdAndBranchId(
                             item.getProduct().getId(),
                             transfer.getDestinationBranch().getId()
                     )
                     .orElseGet(() -> {
                         ProductInventory newInv = new ProductInventory();
                         newInv.setProduct(item.getProduct());
                         newInv.setBranch(transfer.getDestinationBranch());
                         newInv.setQuantityOnHand(0);
                         newInv.setQuantityAvailable(0);
                         return newInv;
                     });

             int goodQuantity = receipt.getReceivedQuantity() - receipt.getDamagedQuantity();
             destInv.setQuantityOnHand(destInv.getQuantityOnHand() + goodQuantity);
             destInv.setQuantityAvailable(destInv.getQuantityAvailable() + goodQuantity);

             // Update average cost
             updateAverageCost(destInv, goodQuantity, item.getUnitCost());

             inventoryRepository.save(destInv);

             createStockMovement(
                     item.getProduct(),
                     transfer.getDestinationBranch(),
                     goodQuantity,
                     MovementType.TRANSFER_IN,
                     transfer.getTransferNumber(),
                     item.getUnitCost(),
                     "Transfer from " + transfer.getSourceBranch().getBranchName(),
                     receivedBy
             );

             if (receipt.getDamagedQuantity() > 0) {
                 createStockMovement(
                         item.getProduct(),
                         transfer.getDestinationBranch(),
                         receipt.getDamagedQuantity(),
                         MovementType.DAMAGE,
                         transfer.getTransferNumber(),
                         item.getUnitCost(),
                         "Damaged during transfer",
                         receivedBy
                 );
             }

             if (receipt.getReceivedQuantity() < item.getShippedQuantity()) {
                 allReceived = false;
             }
         }

         transfer.setReceivedBy(receivedBy);
         transfer.setStatus(
                 allReceived ? TransferStatus.COMPLETED : TransferStatus.PARTIALLY_RECEIVED
         );

         transferRequestRepository.save(transfer);

         resolveRelatedAlert(transfer);

         log.info("Transfer {} received. Status: {}",
                 transfer.getTransferNumber(), transfer.getStatus());
     }

    private void updateAverageCost(ProductInventory inventory, int addedQty, BigDecimal newCost) {
        BigDecimal currentValue = inventory.getAverageCost()
                .multiply(BigDecimal.valueOf(inventory.getQuantityOnHand()));
        BigDecimal addedValue = newCost.multiply(BigDecimal.valueOf(addedQty));
        BigDecimal totalValue = currentValue.add(addedValue);
        int totalQty = inventory.getQuantityOnHand() + addedQty;

        if (totalQty > 0) {
            inventory.setAverageCost(
                    totalValue.divide(BigDecimal.valueOf(totalQty), 2, BigDecimal.ROUND_HALF_UP)
            );
        }
    }

    private void createStockMovement(
            Product product,
            Branch branch,
            Integer quantity,
            MovementType type,
            String reference,
            BigDecimal unitCost,
            String remarks,
            String performedBy
    ) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setBranch(branch);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setMovementDate(LocalDateTime.now());
        movement.setReferenceNumber(reference);
        movement.setUnitCost(unitCost);
        movement.setTotalCost(unitCost.multiply(BigDecimal.valueOf(Math.abs(quantity))));
        movement.setRemarks(remarks);
        movement.setPerformedBy(performedBy);

        stockMovementRepository.save(movement);
    }

    private void resolveRelatedAlert(StockTransferRequest transfer) {
        LowStockAlert alert = lowStockAlertRepository.findByTransferRequestId(transfer.getId());
        if (alert != null && (transfer.getStatus() == TransferStatus.COMPLETED ||
                transfer.getStatus() == TransferStatus.PARTIALLY_RECEIVED)) {
            alert.setStatus(AlertStatus.RESOLVED);
            lowStockAlertRepository.save(alert);
            log.info("Resolved low stock alert {} related to transfer {}",
                    alert.getId(), transfer.getTransferNumber());
        }
    }

    private String generateTransferNumber() {
        long count = transferRequestRepository.count() + 1;
        return String.format("STR-%d-%03d",LocalDateTime.now().getYear(),count);

    }
}


        @lombok.Data
        class TransferItemRequest{
        private Long productId;
        private Integer quantity;
        private String reason;
        }

        @lombok.Data
        class ReceiptItem{
        private Long itemId;
        private Integer receivedQuantity;
        private Integer damagedQuantity;
        private String notes;
        }

