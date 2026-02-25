import com.sipms.dto.purchaseorderdtos.CreatePurchaseOrderItemRequest;
import com.sipms.dto.purchaseorderdtos.CreatePurchaseOrderRequest;
import com.sipms.dto.purchaseorderdtos.PurchaseOrderDTO;
import com.sipms.dto.purchaserequisitiondtos.CreateRequisitionItemRequest;
import com.sipms.dto.purchaserequisitiondtos.CreateRequisitionRequest;
import com.sipms.dto.purchaserequisitiondtos.PurchaseRequisitionDTO;
import com.sipms.enums.POStatus;
import com.sipms.enums.PRStatus;
import com.sipms.enums.Priority;
import com.sipms.enums.SupplierStatus;
import com.sipms.mapper.ProcurementMapper;
import com.sipms.model.InventoryPurchaseOrder;
import com.sipms.model.InventoryPurchaseRequisition;
import com.sipms.model.Supplier;
import com.sipms.repository.InventoryPurchaseOrderRepository;
import com.sipms.repository.PurchaseRequisitionRepository;
import com.sipms.repository.SupplierRepository;
import com.sipms.service.impl.ProcurementServiceImpl;
import com.sipms.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcurementServiceTest {




    @Mock
    private InventoryPurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseRequisitionRepository requisitionRepository;

    @Mock
    private SupplierRepository vendorRepository;


    @Mock
    private ProcurementMapper mapper;

    @InjectMocks
    private ProcurementServiceImpl procurementService;

    private CreateRequisitionRequest createRequisitionRequest;
    private InventoryPurchaseRequisition purchaseRequisition;
    private PurchaseRequisitionDTO requisitionDTO;
    private CreatePurchaseOrderRequest createPurchaseOrderRequest;
    private InventoryPurchaseOrder purchaseOrder;
    private PurchaseOrderDTO purchaseOrderDTO;
    private Supplier supplier;

    @Mock
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        setupRequisitionTestData();
        setupPurchaseOrderTestData();
        setupVendorTestData();
    }

    private void setupRequisitionTestData() {
        // Create requisition request
        List<CreateRequisitionItemRequest> items = Arrays.asList(
                CreateRequisitionItemRequest.builder()
                        .productCode("ITEM001")
                        .quantityRequested(BigDecimal.valueOf(10))
                        .unitOfMeasure("PCS")
                        .build()
        );

        createRequisitionRequest = CreateRequisitionRequest.builder()
                .requesterId(1)
                .departmentId(1)
                .description("Test Requisition")
                .justification("For testing purposes")
                .priority(Priority.MEDIUM)
                .items(items)
                .build();

        // Create requisition entity
        purchaseRequisition = InventoryPurchaseRequisition.builder()
                .id(1)
                .prNumber("PR-0000000001")
                .requestedBy(1)
                .departmentId(1)
                .description("Test Requisition")
                .status(PRStatus.DRAFT)
                .totalAmount(new BigDecimal("1000.00"))
                .createdAt(Instant.now())
                .build();

        // Create requisition DTO
        requisitionDTO = PurchaseRequisitionDTO.builder()
                .id(1L)
                .requisitionNumber("PR-0000000001")
                .requesterId(Long.valueOf(purchaseRequisition.getRequestedBy())).build();
    }

    private void setupPurchaseOrderTestData() {
        supplier = Supplier.builder()
                .supplierCode("VND000001")
                .name("Test Vendor")
                .email("vendor@test.com")
                .status(SupplierStatus.ACTIVE)
                .createdAt(Instant.now()).updatedAt(null).contactPerson("Manager").overallRating(BigDecimal.valueOf(4.0)).build();

        List<CreatePurchaseOrderItemRequest> items = Arrays.asList(
                CreatePurchaseOrderItemRequest.builder()
                        .productCode("ITEM001")
                        .unit("PCS")
                        .unitPrice(new BigDecimal("100.00"))
                        .quantity(10D).build()
        );

        createPurchaseOrderRequest = CreatePurchaseOrderRequest.builder()
                .id(1L)
                .expectedDeliveryDate(LocalDate.now().plusDays(30))
                .deliveryDestination("123 Test St")
                .paymentTerms("Net 30")
                .items(items)
                .createdBy(1L)
                .SupplierId(1L).build();

        purchaseOrder = InventoryPurchaseOrder.builder()
                .id(1L)
                .poNumber("PO-0000000001")
                .supplier(supplier)
                .status(String.valueOf(POStatus.DRAFT))
                .totalAmount(new BigDecimal("1000.00"))
                .createdAt(Instant.now())
                .poDate(LocalDate.now()).deliveryDate(null).createdBy(1L).updatedAt(Instant.now()).build();

        purchaseOrderDTO = PurchaseOrderDTO.builder()
                .id(1L)
                .poNumber("PO-0000000001")
                .status(POStatus.DRAFT)
                .totalAmount(new BigDecimal("1000.00"))
                .supplier(supplier).orderDate(LocalDateTime.now()).expectedDeliveryDate(null).build();
    }

    private void setupVendorTestData() {
        supplier = Supplier.builder()
                .id(1)
                .supplierCode("VND000001")
                .name("Test Vendor")
                .email("vendor@test.com")
                .phone("1234567890")
                .status(SupplierStatus.ACTIVE)
                .createdAt(Instant.now()).updatedAt(Instant.now()).contactPerson("Manager").overallRating(BigDecimal.valueOf(1.0)).build();
    }

    // ==================== Purchase Requisition Tests ====================

    @Test
    void createRequisition_Success() {
        // Arrange
        doNothing().when(validationUtil).validateCreateRequisitionRequest(any());
        when(requisitionRepository.save(any(InventoryPurchaseRequisition.class))).thenReturn(purchaseRequisition);
        when(mapper.toRequisitionDTO(any(InventoryPurchaseRequisition.class))).thenReturn(requisitionDTO);

        // Act
        PurchaseRequisitionDTO result = procurementService.createRequisition(createRequisitionRequest);

        // Assert
        assertNotNull(result);
        assertEquals("PR-0000000001", result.getRequisitionNumber());
        verify(requisitionRepository, times(1)).save(any(InventoryPurchaseRequisition.class));
        verify(validationUtil, times(1)).validateCreateRequisitionRequest(any());
    }
}
