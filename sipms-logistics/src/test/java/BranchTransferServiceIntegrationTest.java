
import com.sipms.SipmsApplication;
import com.sipms.branch.model.*;
import com.sipms.branch.repository.BranchRepository;
import com.sipms.inventory.enums.MovementType;
import com.sipms.inventory.enums.ProductStatus;
import com.sipms.inventory.model.Category;
import com.sipms.inventory.model.Product;
import com.sipms.inventory.model.ProductInventory;
import com.sipms.inventory.model.StockMovement;
import com.sipms.inventory.repository.CategoryRepository;
import com.sipms.inventory.repository.ProductInventoryRepository;
import com.sipms.inventory.repository.ProductRepository;
import com.sipms.inventory.repository.StockMovementRepository;
import com.sipms.logistics.repository.*;
import com.sipms.logistics.entity.LowStockAlert;
import com.sipms.logistics.entity.ReceiptItem;
import com.sipms.logistics.entity.StockTransferRequest;
import com.sipms.logistics.enums.AlertStatus;
import com.sipms.logistics.enums.TransferStatus;
import com.sipms.logistics.service.BranchTransferService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = SipmsApplication.class)
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Branch Transfer Service Integration Tests")
class BranchTransferServiceIntegrationTest {

    @Autowired
    private BranchTransferService transferService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductInventoryRepository inventoryRepository;

    @Autowired
    private StockTransferRequestRepository transferRequestRepository;

    @Autowired
    private LowStockAlertRepository lowStockAlertRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    private Branch nairobiBranch;
    private Branch mombasaBranch;
    private Branch kisumuBranch;
    private Product laptop;
    private Category electronics;

    @BeforeEach
    void setUp() {
        // Create category
        electronics = new Category();
        electronics.setCategoryCode("ELEC");
        electronics.setCategoryName("Electronics");
        electronics.setCreatedAt(LocalDateTime.now());
        electronics = categoryRepository.save(electronics);

        // Create product
        laptop = new Product();
        laptop.setProductCode("LAPTOP-001");
        laptop.setProductName("Dell XPS 15");
        laptop.setStatus( ProductStatus.ACTIVE);
        laptop.setCategory(electronics);

        laptop.setStandardPrice(new BigDecimal("50000"));
        laptop.setStandardCost(new BigDecimal("40000"));
        laptop.setUnitOfMeasure("PIECE");
        laptop.setCreatedAt(LocalDateTime.now());
        laptop = productRepository.save(laptop);

        // Create branches
        nairobiBranch = new Branch();
        nairobiBranch.setBranchId("BR-NAIROBI");
        nairobiBranch.setBranchName("Nairobi Main Branch");
        nairobiBranch.setBranchLocation("Westlands, Nairobi");
        nairobiBranch.setUpdatedAt(Instant.now());
        nairobiBranch.setCreatedAt(Instant.now());
        nairobiBranch = branchRepository.save(nairobiBranch);

        mombasaBranch = new Branch();
        mombasaBranch.setBranchId("BR-MOMBASA");
        mombasaBranch.setBranchName("Mombasa Branch");
        mombasaBranch.setBranchLocation("Nyali, Mombasa");
        mombasaBranch.setCreatedAt(Instant.now());
        mombasaBranch.setUpdatedAt(Instant.now());
        mombasaBranch = branchRepository.save(mombasaBranch);

        kisumuBranch = new Branch();
        kisumuBranch.setBranchId("BR-KISUMU");
        kisumuBranch.setBranchName("Kisumu Branch");
        kisumuBranch.setBranchLocation("Milimani, Kisumu");
        kisumuBranch.setCreatedAt(Instant.now());
        kisumuBranch.setUpdatedAt(Instant.now());
        kisumuBranch = branchRepository.save(kisumuBranch);

        // Create inventory
        createInventory(nairobiBranch, 50, 10);
        createInventory(mombasaBranch, 2, 10); // Low stock
        createInventory(kisumuBranch, 3, 10);  // Low stock
    }

    private void createInventory(Branch branch, int quantity, int minimum) {
        ProductInventory inventory = new ProductInventory();
        inventory.setProduct(laptop);
        inventory.setBranch(branch);
        inventory.setQuantityOnHand(quantity);
        inventory.setQuantityAvailable(quantity);
        inventory.setQuantityReserved(0);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setMinimumStockLevel(minimum);
        inventory.setAverageCost(new BigDecimal("40000"));
        inventoryRepository.save(inventory);
    }


    @Test
    @Order(1)
    @DisplayName("Integration: Complete transfer workflow from alert to delivery")
    void testCompleteTransferWorkflow() {
        // STEP 1: Create low stock alert
        ProductInventory mombasaInventory = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow();

        LowStockAlert alert = transferService.createLowStockAlert(mombasaInventory);
        assertNotNull(alert.getId());
        assertEquals(AlertStatus.NEW, alert.getStatus());

        // STEP 2: Auto-create transfer request
        StockTransferRequest transfer = transferService
                .autoCreateTransferFromAlert(alert.getId());

        assertNotNull(transfer);
        assertNotNull(transfer.getId());
        assertEquals(TransferStatus.PENDING_APPROVAL, transfer.getStatus());
        assertEquals(nairobiBranch.getId(), transfer.getSourceBranch().getId());
        assertEquals(mombasaBranch.getId(), transfer.getDestinationBranch().getId());

        // Verify alert status updated
        alert = lowStockAlertRepository.findById(alert.getId()).orElseThrow();
        assertEquals(AlertStatus.IN_PROGRESS, alert.getStatus());

        // STEP 3: Approve transfer
        transferService.approveTransfer(
                transfer.getId(),
                "manager@test.com"
        );

        transfer = transferRequestRepository.findById(transfer.getId()).orElseThrow();
        assertEquals(TransferStatus.APPROVED, transfer.getStatus());
        assertEquals("manager@test.com", transfer.getApprovedBy());

        // STEP 4: Ship from source
        int nairobiStockBefore = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();

        transferService.shipTransfer(
                transfer.getId(),
                "warehouse.nairobi@test.com",
                "DHL",
                "DHL-123456"
        );

        transfer = transferRequestRepository.findById(transfer.getId()).orElseThrow();
        assertEquals(TransferStatus.IN_TRANSIT, transfer.getStatus());

        // Verify source inventory decreased
        int nairobiStockAfter = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();
//        assertEquals(nairobiStockBefore - 8, nairobiStockAfter);

        // Verify stock movement created
        List<StockMovement> movements = stockMovementRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId());
        assertTrue(movements.stream()
                .anyMatch(m -> m.getMovementType()== MovementType.TRANSFER_OUT));

        // STEP 5: Receive at destination
        int mombasaStockBefore = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();

        ReceiptItem receipt = new ReceiptItem();
        receipt.setItemId(transfer.getTransferItems().get(0).getId());
        receipt.setReceivedQuantity(8);
        receipt.setDamagedQuantity(0);
        receipt.setNotes("All items received in good condition");

        transferService.receiveTransfer(
                transfer.getId(),
                "warehouse.mombasa@test.com",
                Arrays.asList(receipt)
        );

        transfer = transferRequestRepository.findById(transfer.getId()).orElseThrow();
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());

        // Verify destination inventory increased
        int mombasaStockAfter = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();
        assertEquals(mombasaStockBefore + 8, mombasaStockAfter);

        // Verify alert resolved
        alert = lowStockAlertRepository.findById(alert.getId()).orElseThrow();
        assertEquals(AlertStatus.RESOLVED, alert.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Integration: Should handle multiple low stock branches")
    void testMultipleLowStockBranches() {
        // Both Mombasa and Kisumu have low stock
        // Nairobi has enough for both

        ProductInventory mombasaInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow();

        ProductInventory kisumuInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), kisumuBranch.getId())
                .orElseThrow();

        // Create alerts for both
        LowStockAlert mombasaAlert = transferService.createLowStockAlert(mombasaInv);
        LowStockAlert kisumuAlert = transferService.createLowStockAlert(kisumuInv);

        // Create transfers for both
        StockTransferRequest mombasaTransfer = transferService
                .autoCreateTransferFromAlert(mombasaAlert.getId());
        StockTransferRequest kisumuTransfer = transferService
                .autoCreateTransferFromAlert(kisumuAlert.getId());

        assertNotNull(mombasaTransfer);
        assertNotNull(kisumuTransfer);

        // Both should use Nairobi as source
        assertEquals(nairobiBranch.getId(), mombasaTransfer.getSourceBranch().getId());
        assertEquals(nairobiBranch.getId(), kisumuTransfer.getSourceBranch().getId());

        // Nairobi should have enough for both (50 available, needs 8+7=15)
        ProductInventory nairobiInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId())
                .orElseThrow();
        assertTrue(nairobiInv.getQuantityAvailable() >= 15);
    }

    @Test
    @Order(3)
    @DisplayName("Integration: Should handle transfer rejection")
    void testRejectTransfer() {
        // Create transfer request
        ProductInventory mombasaInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow();

        LowStockAlert alert = transferService.createLowStockAlert(mombasaInv);
        StockTransferRequest transfer = transferService
                .autoCreateTransferFromAlert(alert.getId());

        // Reject transfer
        transferService.rejectTransfer(
                transfer.getId(),
                "manager@test.com",
                "Source branch needs stock for local sales"
        );

        transfer = transferRequestRepository.findById(transfer.getId()).orElseThrow();
        assertEquals(TransferStatus.REJECTED, transfer.getStatus());

        // Inventory should be unchanged
        ProductInventory nairobiInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId())
                .orElseThrow();
        assertEquals(50, nairobiInv.getQuantityOnHand()); // Still 50
    }

    @Test
    @Order(4)
    @DisplayName("Integration: Should handle partial receipt with damaged items")
    void testPartialReceiptWithDamage() {
        // Create and approve transfer
        ProductInventory mombasaInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow();

        LowStockAlert alert = transferService.createLowStockAlert(mombasaInv);
        StockTransferRequest transfer = transferService
                .autoCreateTransferFromAlert(alert.getId());

        transferService.approveTransfer(transfer.getId(), "manager@test.com");
        transferService.shipTransfer(
                transfer.getId(),
                "warehouse@test.com",
                "DHL",
                "DHL123"
        );

        // Receive with damage
        ReceiptItem receipt = new ReceiptItem();
        receipt.setItemId(transfer.getTransferItems().get(0).getId());
        receipt.setReceivedQuantity(8);
        receipt.setDamagedQuantity(2); // 2 damaged
        receipt.setNotes("2 laptops damaged in transit");

        int stockBefore = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();

        transferService.receiveTransfer(
                transfer.getId(),
                "warehouse.mombasa@test.com",
                Arrays.asList(receipt)
        );

        // Should add 6 (8 received - 2 damaged)
        int stockAfter = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow()
                .getQuantityOnHand();

        assertEquals(stockBefore + 6, stockAfter);

        // Verify stock movements
        List<StockMovement> movements = stockMovementRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId());

        assertTrue(movements.stream()
                .anyMatch(m -> m.getMovementType() == MovementType.TRANSFER_IN && m.getQuantity() == 6));
        assertTrue(movements.stream()
                .anyMatch(m -> m.getMovementType() == MovementType.DAMAGE && m.getQuantity() == 2));
    }


    @Test
    @Order(7)
    @DisplayName("Integration: Should handle concurrent transfers correctly")
    void testConcurrentTransfers() {
        // Create two transfer requests from Nairobi
        ProductInventory mombasaInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), mombasaBranch.getId())
                .orElseThrow();

        ProductInventory kisumuInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), kisumuBranch.getId())
                .orElseThrow();

        LowStockAlert alert1 = transferService.createLowStockAlert(mombasaInv);
        LowStockAlert alert2 = transferService.createLowStockAlert(kisumuInv);

        StockTransferRequest transfer1 = transferService
                .autoCreateTransferFromAlert(alert1.getId());
        StockTransferRequest transfer2 = transferService
                .autoCreateTransferFromAlert(alert2.getId());

        // Approve both
        transferService.approveTransfer(transfer1.getId(), "mgr1@test.com");
        transferService.approveTransfer(transfer2.getId(), "mgr2@test.com");

        // Ship both
        transferService.shipTransfer(transfer1.getId(), "wh@test.com", "DHL", "D1");
        transferService.shipTransfer(transfer2.getId(), "wh@test.com", "DHL", "D2");

        // Check Nairobi inventory
        ProductInventory nairobiInv = inventoryRepository
                .findByProductIdAndBranchId(laptop.getId(), nairobiBranch.getId())
                .orElseThrow();

        // Should have deducted both transfers (8 + 7 = 15)
        assertEquals(35, nairobiInv.getQuantityOnHand());
    }
}