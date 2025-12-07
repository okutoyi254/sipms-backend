package unitTest;


import model.DeliveryLogs;
import model.OrderedProduct;
import model.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ProductRepository;
import repository.SupplierRepository;
import serviceImplementation.GeneralManagerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneralManagerServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GeneralManagerService generalManagerService;

    @Test
    void orderProducts_returns_200OK(){

        Supplier supplier=new Supplier(1,"MRM Distributors",new ArrayList<>(List.of("Iron Sheets","Nails","Y8 Steel")));
        OrderedProduct product=new OrderedProduct("Iron Sheets",20,1000);
        List<OrderedProduct> productsList=new ArrayList<>(
                List.of(product));

        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));

        DeliveryLogs logs=generalManagerService.orderPlacement(supplier.getSupplierId(),productsList);

        assertEquals("MRM Distributors",logs.getSupplierName());
        assertEquals(1,logs.getTotalItems());
        assertEquals(20000,logs.getTotalPrice());

        verify(supplierRepository,times(1)).findById(supplier.getSupplierId());


}
}
