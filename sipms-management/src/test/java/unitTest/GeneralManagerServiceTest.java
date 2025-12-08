package unitTest;


import dto.OrderedProduct;
import dto.ShipProduct;
import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.DeliveryLogsRepository;
import repository.ProductRepository;
import repository.ShippedProductRepo;
import repository.SupplierRepository;
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


    @InjectMocks
    private GeneralManagerService generalManagerService;

    @Test
    void orderProducts_returns_201CREATED(){

        Supplier supplier=new Supplier(1,"MRM Distributors",new ArrayList<>(List.of("Iron Sheets","Nails","Y8 Steel")));
        OrderedProduct product=new OrderedProduct("Iron Sheets",20,1000);
        List<OrderedProduct> productsList=new ArrayList<>(
                List.of(product));

        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(logsRepository.save(any(DeliveryLogs.class)))
                .thenAnswer(invocation->invocation.getArgument(0));

        DeliveryLogs logs=generalManagerService.orderPlacement(supplier.getSupplierId(),productsList);
        System.out.println(logs.getSupplierName());

        assertEquals("MRM Distributors",logs.getSupplierName());
        assertEquals(1,logs.getTotalItems());
        assertEquals(20000,logs.getTotalPrice());

        verify(supplierRepository,times(1)).findById(supplier.getSupplierId());
        verify(logsRepository, times(1)).save(any(DeliveryLogs.class));

}

@Test
    void ship_products_to_another_branch_returns_201CREATED(){

        Product product=new Product(1L,"Chair",300D);

        product.setProductQuantity(300);
         List<Product>productsA= new LinkedList<Product>(List.of
                 (product));

         product.setProductQuantity(10);
         List<Product>productB=new LinkedList<Product>(List.of
                 (product));

    Branch branchA=new Branch();
    branchA.setBranchId("RQA12");
    branchA.setBranchLocation("Nairobi");
    branchA.setBranchName("Kakamega Elects");
    branchA.setProductList(productsA);

    Branch branchB=new Branch();
    branchB.setBranchId("RQA12");
    branchB.setBranchLocation("Nairobi");
    branchB.setBranchName("Kakamega Elects");
    branchB.setProductList(productB);

    ShipProduct prod=new ShipProduct(branchA.getBranchId(),branchB.getBranchId(),1L,30);
    ShippedProductRecord record=new ShippedProductRecord
            (1L,prod.source(),prod.destination(),prod.productId(), prod.quantity());

      shippedProductRepo.save(record);

      verify(shippedProductRepo,times(1)).save(any(ShippedProductRecord.class));
}
}
