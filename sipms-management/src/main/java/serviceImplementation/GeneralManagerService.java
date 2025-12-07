package serviceImplementation;

import model.*;
import repository.ProductRepository;
import repository.SupplierRepository;

import java.util.List;

public class GeneralManagerService {

    private  final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public GeneralManagerService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public DeliveryLogs orderPlacement(Integer supplierId, List<OrderedProduct>products) {
        Supplier supplier=supplierRepository.findById(supplierId).orElseThrow();

        double totalPrice=0;

        for(OrderedProduct product:products) {

            totalPrice +=product.unitPrice()* product.quantity();

        }
        DeliveryLogs logs= DeliveryLogs.builder()
                .supplierName(supplier.getSupplierName())
                .deliveryStatus(DeliveryStatus.PENDING)
                .supplierEmail(supplier.getSupplierName())
                .totalItems(products.size())
                .totalPrice(totalPrice).build();

        return logs;
    }
}

