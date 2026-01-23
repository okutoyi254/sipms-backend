import entity.LowStockAlert;
import entity.ReceiptItem;
import entity.StockTransferRequest;
import enums.AlertSeverity;
import enums.AlertStatus;
import enums.MovementType;
import enums.TransferStatus;
import model.Branch;
import model.Product;
import model.ProductInventory;
import model.StockMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.*;
import service.BranchTransferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Branch Transfer Service Unit Tests")
public class BranchTransferServiceTest {

    @Mock
    private StockTransferRequestRepository transferRequestRepository;

    @Mock
    private LowStockAlertRepository lowStockAlertRepository;

    @Mock
    private ProductInventoryRepository inventoryRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @InjectMocks
    private BranchTransferService transferService;

    private Product laptop;
    private Branch nairobiBranch;
    private Branch mombasaBranch;
    private ProductInventory nairobiInventory;
    private ProductInventory mombasaInventory;

    @BeforeEach
    void setUp() {
        laptop = new Product();
        laptop.setId(1L);
        laptop.setProductCode("PROD-001");
        laptop.setProductName("Laptop Dell XPS");
        laptop.setStandardPrice(new BigDecimal("50000"));
        laptop.setStandardCost(new BigDecimal("40000"));

        nairobiBranch = new Branch();
        nairobiBranch.setId(1L);
        nairobiBranch.setBranchId("BR-001");
        nairobiBranch.setBranchName("Nairobi Main");

        mombasaBranch = new Branch();
        mombasaBranch.setId(2L);
        mombasaBranch.setBranchId("BR-002");
        mombasaBranch.setBranchName("Mombasa Branch");

        nairobiInventory = new ProductInventory();
        nairobiInventory.setId(1L);
        nairobiInventory.setProduct(laptop);
        nairobiInventory.setBranch(nairobiBranch);
        nairobiInventory.setQuantityOnHand(50);
        nairobiInventory.setQuantityAvailable(50);
        nairobiInventory.setMinimumStockLevel(10);
        nairobiInventory.setAverageCost(new BigDecimal("40000"));

        mombasaInventory = new ProductInventory();
        mombasaInventory.setId(2L);
        mombasaInventory.setProduct(laptop);
        mombasaInventory.setBranch(mombasaBranch);
        mombasaInventory.setQuantityOnHand(2);
        mombasaInventory.setQuantityAvailable(2);
        mombasaInventory.setMinimumStockLevel(10);
        mombasaInventory.setAverageCost(new BigDecimal("40000"));

    }

    @Test
    @DisplayName("Should create low stock alert when inventory is below minimum")
    void testCreateLowStockAlert_Success() {

        when(lowStockAlertRepository.save(any(LowStockAlert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LowStockAlert alert = transferService.createLowStockAlert(mombasaInventory);

        assertNotNull(alert);
        assertEquals("BR-002", alert.getBranch().getBranchId());
        assertEquals("Laptop Dell XPS", alert.getProduct().getProductName());
        assertEquals(2, alert.getCurrentStock());
        assertEquals(10, alert.getMinimumStockLevel());
        assertEquals(AlertStatus.NEW, alert.getStatus());
        assertEquals(AlertSeverity.CRITICAL, alert.getSeverity());
    }

    @Test
    @DisplayName("Should set OUT_OF_STOCK severity for zero inventory")
    void testCreateLowStockAlert_OutOfStockSeverity() {
        mombasaInventory.setQuantityAvailable(0);

        when(lowStockAlertRepository.save(any(LowStockAlert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LowStockAlert alert = transferService.createLowStockAlert(mombasaInventory);

        assertNotNull(alert);
        assertEquals(AlertSeverity.OUT_OF_STOCK, alert.getSeverity());
    }

    @Test
    @DisplayName("Should find source branch with sufficient inventory")
    void testFindSourceBranchWithSufficientInventory_Success() {

        List<ProductInventory> availableStock = Arrays.asList(nairobiInventory);
        when(inventoryRepository.findAllByProductId(1L)).thenReturn(availableStock);

        Optional<Branch> sourceBranch = transferService.findSourceBranch(1L, 5L, 8);

        assertTrue(sourceBranch.isPresent());
        assertEquals(nairobiBranch, sourceBranch.get());
    }

    @Test
    @DisplayName("Should return empty when no branch has sufficient inventory")
    void testFindSourceBranchWithSufficientInventory_Failure() {
        List<ProductInventory> availableStock = Arrays.asList(mombasaInventory);
        when(inventoryRepository.findAllByProductId(1L)).thenReturn(availableStock);

        Optional<Branch> sourceBranch = transferService.findSourceBranch(1L, 5L, 8);

        assertFalse(sourceBranch.isPresent());
    }

    @Test
    @DisplayName("Should not use branch that would go below minimum stock level")
    void testFindSourceBranch_RespectsMinimumStockLevel() {
        nairobiInventory.setQuantityAvailable(15);
        nairobiInventory.setMinimumStockLevel(10);

        List<ProductInventory> availableStock = Arrays.asList(nairobiInventory);
        when(inventoryRepository.findAllByProductId(1L)).thenReturn(availableStock);

        Optional<Branch> sourceBranch = transferService.findSourceBranch(1L, 5L, 8);

        assertFalse(sourceBranch.isPresent());
    }

    @Test
    @DisplayName("Should exclude destination branch from source search")
    void testFindSourceBranch_ExcludesDestinationBranch() {
        List<ProductInventory> availableStock = Arrays.asList(nairobiInventory, mombasaInventory);
        when(inventoryRepository.findAllByProductId(1L)).thenReturn(availableStock);
        Optional<Branch> sourceBranch = transferService.findSourceBranch(1L, mombasaBranch.getId(), 5);
        assertTrue(sourceBranch.isPresent());
        assertEquals(nairobiBranch, sourceBranch.get());
    }

    @Test
    @DisplayName("Should auto-create transfer request for low stock alert")
    void testAutoCreateTransferRequestForLowStockAlert_Success() {
        LowStockAlert alert = new LowStockAlert();
        alert.setId(1L);
        alert.setBranch(mombasaBranch);
        alert.setProduct(laptop);
        alert.setShortageQuantity(8);
        alert.setCurrentStock(2);
        alert.setMinimumStockLevel(10);

        lenient().when(branchRepository.findById(mombasaBranch.getId()))
                .thenReturn(Optional.of(mombasaBranch));
        lenient().when(productRepository.findById(laptop.getId()))
                .thenReturn(Optional.of(laptop));
        when(inventoryRepository.findAllByProductId(laptop.getId()))
                .thenReturn(Arrays.asList(nairobiInventory, mombasaInventory));
        when(transferRequestRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(lowStockAlertRepository.findById(1L)).thenReturn(Optional.of(alert));


        var transferRequest = transferService.autoCreateTransferFromAlert(alert.getId());

        assertNotNull(transferRequest);
        assertEquals(mombasaBranch, transferRequest.getDestinationBranch());
        assertEquals(1, transferRequest.getTransferItems().size());
        assertEquals(nairobiBranch, transferRequest.getSourceBranch());
        assertEquals(laptop, transferRequest.getTransferItems().get(0).getProduct());
        assertEquals(8, transferRequest.getTransferItems().get(0).getRequestedQuantity());

        verify(lowStockAlertRepository, times(1)).save(argThat(a ->
                a.getStatus() == AlertStatus.IN_PROGRESS
        ));
    }

    @Test
    @DisplayName("Should fail auto-create transfer request when no source branch found")
    void testAutoCreateTransferRequestForLowStockAlert_Failure_NoSourceBranch() {
        LowStockAlert alert = new LowStockAlert();
        alert.setId(1L);
        alert.setBranch(mombasaBranch);
        alert.setProduct(laptop);
        alert.setShortageQuantity(8);
        alert.setCurrentStock(2);
        alert.setMinimumStockLevel(10);

        when(lowStockAlertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(inventoryRepository.findAllByProductId(laptop.getId()))
                .thenReturn(Arrays.asList(mombasaInventory));
        when(lowStockAlertRepository.save(any(LowStockAlert.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockTransferRequest transferRequest = transferService.autoCreateTransferFromAlert(alert.getId());
        assertNull(transferRequest);
        verify(lowStockAlertRepository, times(1)).save(argThat(a ->
                a.getStatus() == AlertStatus.ACKNOWLEDGED
        ));
    }

    @Test
    @DisplayName("Should approve transfer request successfully")
    void testApproveTransferRequest_Success() {
        StockTransferRequest transferRequest = createPendingTransferRequest();

        when(transferRequestRepository.findById(1L))
                .thenReturn(Optional.of(transferRequest));
        when(transferRequestRepository.save(any(StockTransferRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(inventoryRepository.findByProductIdAndBranchId(1L, 1L))
                .thenReturn(Optional.of(nairobiInventory));

        transferService.approveTransfer(1L, "managerUser");
        assertEquals("managerUser", transferRequest.getApprovedBy());
        assertEquals(TransferStatus.APPROVED, transferRequest.getStatus());
        assertEquals(8, transferRequest.getTransferItems().get(0).getApprovedQuantity());

        verify(transferRequestRepository, times(1)).save(transferRequest);
    }

    private StockTransferRequest createPendingTransferRequest() {
        StockTransferRequest transferRequest = new StockTransferRequest();
        transferRequest.setId(1L);
        transferRequest.setSourceBranch(nairobiBranch);
        transferRequest.setDestinationBranch(mombasaBranch);
        transferRequest.setApprovalDate(LocalDate.now());
        transferRequest.setStatus(TransferStatus.PENDING_APPROVAL);

        var item = new entity.StockTransferItem();
        item.setProduct(laptop);
        item.setRequestedQuantity(8);
        item.setApprovedQuantity(8);
        transferRequest.setTransferItems(List.of(item));

        return transferRequest;

    }

    @Test
    @DisplayName("Should fail approve transfer request when insufficient stock in source branch")
    void testApproveTransferRequest_Failure_InsufficientStock() {
        StockTransferRequest transferRequest = createPendingTransferRequest();
        nairobiInventory.setQuantityAvailable(5); // Not enough for requested 8

        when(transferRequestRepository.findById(1L))
                .thenReturn(Optional.of(transferRequest));
        when(inventoryRepository.findByProductIdAndBranchId(1L, 1L))
                .thenReturn(Optional.of(nairobiInventory));

        assertThrows(RuntimeException.class, () ->
                transferService.approveTransfer(1L, "managerUser")
        );
    }

    @Test
    @DisplayName("Should fail approve transfer request when already approved")
    void testApproveTransferRequest_Failure_AlreadyApproved() {
        StockTransferRequest transferRequest = createPendingTransferRequest();
        transferRequest.setStatus(TransferStatus.APPROVED);

        when(transferRequestRepository.findById(1L))
                .thenReturn(Optional.of(transferRequest));
        assertThrows(RuntimeException.class, () ->
                transferService.approveTransfer(1L, "managerUser")
        );
    }

    @Test
    @DisplayName("Should ship approved transfer request and update inventories")
    void testShipTransferRequest_Success() {
        StockTransferRequest transferRequest = createApprovedTransferRequest();

        when(transferRequestRepository.findById(1L))
                .thenReturn(Optional.of(transferRequest));
        when(transferRequestRepository.save(any(StockTransferRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(inventoryRepository.findByProductIdAndBranchId(1L, 1L))
                .thenReturn(Optional.of(nairobiInventory));
        lenient().when(inventoryRepository.findByProductIdAndBranchId(1L, 2L))
                .thenReturn(Optional.of(mombasaInventory));

        transferService.shipTransfer(
                1L,
                "shipperUser",
                "FastExpress",
                "TRACK123");

        assertEquals("shipperUser", transferRequest.getShippedBy());
        assertEquals(TransferStatus.APPROVED, transferRequest.getStatus());
        assertEquals(42, nairobiInventory.getQuantityAvailable());
        assertEquals(2, mombasaInventory.getQuantityAvailable());

        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    private StockTransferRequest createApprovedTransferRequest() {
        StockTransferRequest transferRequest = new StockTransferRequest();
        transferRequest.setId(1L);
        transferRequest.setSourceBranch(nairobiBranch);
        transferRequest.setDestinationBranch(mombasaBranch);
        transferRequest.setApprovalDate(LocalDate.now());
        transferRequest.setStatus(TransferStatus.APPROVED);

        var item = new entity.StockTransferItem();
        item.setProduct(laptop);
        item.setUnitCost(BigDecimal.valueOf(10000));
        item.setRequestedQuantity(8);
        item.setApprovedQuantity(8);
        transferRequest.setTransferItems(List.of(item));

        return transferRequest;
    }

    @Test
    @DisplayName("Should receive transfer and update destination inventory")
    void testReceiveTransfer_FullyReceived() {
        // Given
        StockTransferRequest transfer = createInTransitTransferRequest();

        ReceiptItem receipt = new ReceiptItem();
        receipt.setItemId(1L);
        receipt.setReceivedQuantity(8);
        receipt.setDamagedQuantity(0);
        receipt.setNotes("All good");

        when(transferRequestRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(inventoryRepository.findByProductIdAndBranchId(1L, 2L))
                .thenReturn(Optional.of(mombasaInventory));
        when(inventoryRepository.save(any(ProductInventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transferRequestRepository.save(any(StockTransferRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        transferService.receiveTransfer(1L, "warehouse@mombasa.com", Arrays.asList(receipt));

        // Then
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
        assertEquals(10, mombasaInventory.getQuantityOnHand()); // 2 + 8
        assertEquals(10, mombasaInventory.getQuantityAvailable());
        assertEquals(8, transfer.getTransferItems().get(0).getReceivedQuantity());

        verify(stockMovementRepository, times(1)).save(argThat(sm ->
                sm.getMovementType() == MovementType.TRANSFER_IN && sm.getQuantity() == 8
        ));
    }

    private StockTransferRequest createInTransitTransferRequest() {
        StockTransferRequest transferRequest = new StockTransferRequest();
        transferRequest.setId(1L);
        transferRequest.setSourceBranch(nairobiBranch);
        transferRequest.setDestinationBranch(mombasaBranch);
        transferRequest.setApprovalDate(LocalDate.now());
        transferRequest.setStatus(TransferStatus.IN_TRANSIT);

        var item = new entity.StockTransferItem();
        item.setId(1L);
        item.setProduct(laptop);
        item.setUnitCost(BigDecimal.valueOf(10000));
        item.setRequestedQuantity(8);
        item.setApprovedQuantity(8);
        item.setShippedQuantity(8);
        transferRequest.setTransferItems(List.of(item));

        return transferRequest;
    }
}
//   @lombok.Data
//   @lombok.NoArgsConstructor
//    class ReceiptItem {
//       private Long productId;
//       private Integer receivedQuantity;
//       private Integer damagedQuantity;
//       private String notes;
//
//       public ReceiptItem(Long productId, Integer receivedQuantity, Integer damagedQuantity,String notes) {
//           this.productId = productId;
//           this.receivedQuantity = receivedQuantity;
//           this.damagedQuantity = damagedQuantity;
//              this.notes = notes;
//       }
//   }