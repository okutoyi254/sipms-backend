package unitTest;


import dto.OrderedProduct;
import dto.ShipProduct;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.*;
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

    @Test
    void orderProducts_returns_201CREATED() {

        Supplier supplier = new Supplier(1, "MRM Distributors", new ArrayList<>(List.of("Iron Sheets", "Nails", "Y8 Steel")));
        OrderedProduct product = new OrderedProduct("Iron Sheets", 20, 1000);
        List<OrderedProduct> productsList = new ArrayList<>(
                List.of(product));

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
    void ship_products_to_another_branch_returns_201CREATED_and_updates_inventory(){


        final String SOURCE_ID = "S001";
        final String DEST_ID = "D002";
        final Long PRODUCT_ID = 1L;
        final int SHIP_QUANTITY = 30;
        final int SOURCE_INITIAL_QTY = 100;
        final int DEST_INITIAL_QTY = 10;

        Product sourceProduct = new Product(PRODUCT_ID, "Chair", 300.00);
        sourceProduct.setProductQuantity(SOURCE_INITIAL_QTY);

        Product destProduct = new Product(PRODUCT_ID, "Chair", 300.00);
        destProduct.setProductQuantity(DEST_INITIAL_QTY);

        Branch branchA = new Branch();
        branchA.setBranchId(SOURCE_ID);
        branchA.setBranchLocation("Nairobi");
        branchA.setProductList(new LinkedList<>(List.of(sourceProduct)));

        Branch branchB = new Branch();
        branchB.setBranchId(DEST_ID);
        branchB.setBranchLocation("Mombasa");
        branchB.setProductList(new LinkedList<>(List.of(destProduct)));

        ShipProduct shipDetails = new ShipProduct(SOURCE_ID, DEST_ID, PRODUCT_ID, SHIP_QUANTITY);

        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(branchRepository.findById(SOURCE_ID)).thenReturn(Optional.of(branchA));
        when(branchRepository.findById(DEST_ID)).thenReturn(Optional.of(branchB));

        when(shippedProductRepo.save(any(ShippedProductRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));



        ShippedProductRecord productRecord = generalManagerService.shipProductFxn(shipDetails);


       assertEquals(SOURCE_ID, productRecord.getSource());
        assertEquals(DEST_ID, productRecord.getDestination());
        assertEquals(SHIP_QUANTITY, productRecord.getQuantity());


        assertEquals(SOURCE_INITIAL_QTY - SHIP_QUANTITY, sourceProduct.getProductQuantity(),
                "Source inventory was not correctly debited.");

        assertEquals(DEST_INITIAL_QTY + SHIP_QUANTITY, destProduct.getProductQuantity(),
                "Destination inventory was not correctly credited.");



        verify(branchRepository, times(1)).findById(SOURCE_ID);
        verify(branchRepository, times(1)).findById(DEST_ID);

        verify(shippedProductRepo, times(1)).save(any(ShippedProductRecord.class));

        verify(branchRepository, times(2)).save(any(Branch.class));
    }
}
