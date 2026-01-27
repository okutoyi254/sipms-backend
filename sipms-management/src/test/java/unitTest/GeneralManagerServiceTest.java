package unitTest;

import com.sipms.branch.model.Branch;
import com.sipms.branch.model.Product;
import com.sipms.branch.model.StockMovement;
import com.sipms.branch.model.Supplier;
import com.sipms.logistics.repository.BranchRepository;
import com.sipms.logistics.repository.DeliveryLogsRepository;
import com.sipms.logistics.repository.ShippedProductRepo;
import com.sipms.logistics.repository.SupplierRepository;
import dto.OrderedProduct;
import dto.ShipProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import serviceImplementation.GeneralManagerService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneralManagerServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private DeliveryLogsRepository logsRepository;

    @Mock
    ShippedProductRepo shippedProductRepo;

    @Mock
    BranchRepository branchRepository;

    @InjectMocks
    private GeneralManagerService generalManagerService;

    private static final String SOURCE_ID = "S001";
    private static final String DEST_ID = "D002";
    private static final long PRODUCT_ID = 1L;
    private static final int SHIP_QUANTITY = 30;
    private static final int SOURCE_INITIAL_QTY = 100;
    private static final int DEST_INITIAL_QTY = 10;

    private Product sourceProduct;
    private Product destProduct;
    private Branch sourceBranch;
    private Branch destBranch;
    private ShipProduct shipDetails;
    private StockMovement shippedProductRecord;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        sourceProduct = new Product(PRODUCT_ID, "Chair", 300.00);
        sourceProduct.setProductQuantity(SOURCE_INITIAL_QTY);

        destProduct = new Product(PRODUCT_ID, "Chair", 300.00);
        destProduct.setProductQuantity(DEST_INITIAL_QTY);

        sourceBranch = new Branch();
        sourceBranch.setBranchId(SOURCE_ID);
        sourceBranch.setBranchLocation("Nairobi");
        sourceBranch.setProductList(new LinkedList<>(List.of(sourceProduct)));

        destBranch = new Branch();
        destBranch.setBranchId(DEST_ID);
        destBranch.setBranchLocation("Mombasa");
        destBranch.setProductList(new LinkedList<>(List.of(destProduct)));

        shippedProductRecord=new ShippedProductRecord();
        shippedProductRecord.setProductId(1L);
        shippedProductRecord.setShippingId(2L);
        shippedProductRecord.setQuantity(1);
        shippedProductRecord.setDestination(destBranch.getBranchId());
        shippedProductRecord.setSource(sourceBranch.getBranchId());

        shipDetails = new ShipProduct(SOURCE_ID, DEST_ID, PRODUCT_ID, SHIP_QUANTITY);

        supplier = new Supplier(1, "MRM Distributors",
                new ArrayList<>(List.of("Iron Sheets", "Nails", "Y8 Steel")));
    }

    @Test
    void orderProducts_returns_201CREATED() {
        OrderedProduct product = new OrderedProduct("Iron Sheets", 20, 1000);
        List<OrderedProduct> productsList = new ArrayList<>(List.of(product));

        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(logsRepository.save(any(DeliveryLogs.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DeliveryLogs logs = generalManagerService.orderPlacement(supplier.getSupplierId(), productsList);

        System.out.println(logs.getSupplierName());
        assertEquals("MRM Distributors", logs.getSupplierName());
        assertEquals(1, logs.getTotalItems());
        assertEquals(20000, logs.getTotalPrice());

        verify(supplierRepository, times(1)).findById(supplier.getSupplierId());
        verify(logsRepository, times(1)).save(any(DeliveryLogs.class));
    }

    @Test
    void ship_products_to_another_branch_returns_201CREATED_and_updates_inventory() {
        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(branchRepository.findById(SOURCE_ID)).thenReturn(Optional.of(sourceBranch));
        when(branchRepository.findById(DEST_ID)).thenReturn(Optional.of(destBranch));
        when(shippedProductRepo.save(any(StockMovement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockMovement productRecord = generalManagerService.shipProductFxn(shipDetails);

        assertEquals(SOURCE_ID, productRecord.getSource());
        assertEquals(DEST_ID, productRecord.getDestination());
        assertEquals(SHIP_QUANTITY, productRecord.getQuantity());

        assertEquals(SOURCE_INITIAL_QTY - SHIP_QUANTITY, sourceProduct.getProductQuantity(),
                "Source inventory was not correctly debited.");

        verify(branchRepository, times(1)).findById(SOURCE_ID);
        verify(branchRepository, times(1)).findById(DEST_ID);
        verify(shippedProductRepo, times(1)).save(any(StockMovement.class));
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    void shipping_verified_returns_200OK() {
        when(branchRepository.findById(DEST_ID)).thenReturn(Optional.of(destBranch));
        when(shippedProductRepo.findById(any(Long.class))).thenReturn(Optional.of(shippedProductRecord));
        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShippedProductRecord record = generalManagerService.shippedProductsReceivedAndVerified(shipDetails);

        assertEquals(DEST_INITIAL_QTY + SHIP_QUANTITY, destProduct.getProductQuantity(),
                "Destination inventory was not correctly credited.");

        verify(branchRepository, times(1)).findById(DEST_ID);
        verify(branchRepository, times(1)).save(any(Branch.class));
    }
}