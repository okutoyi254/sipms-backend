package serviceImplementation;

import dto.OrderedProduct;
import model.*;
import repository.DeliveryLogsRepository;
import repository.ProductRepository;
import repository.SupplierRepository;

import java.util.List;

public class GeneralManagerService {

    private  final SupplierRepository supplierRepository;
    private final DeliveryLogsRepository logsRepository;

    public GeneralManagerService(SupplierRepository supplierRepository, DeliveryLogsRepository logsRepository) {
        this.supplierRepository = supplierRepository;
        this.logsRepository = logsRepository;
    }

    public DeliveryLogs orderPlacement(Integer supplierId, List<OrderedProduct>products) {
        Supplier supplier=supplierRepository.findById(supplierId).orElseThrow();

        double totalPrice=0;

        for(OrderedProduct product:products) {

            totalPrice +=product.unitPrice()* product.quantity();

        }

        return logsRepository.save(DeliveryLogs.builder()
                .supplierName(supplier.getSupplierName())
                .deliveryStatus(DeliveryStatus.PENDING)
                .supplierEmail(supplier.getSupplierName())
                .totalItems(products.size())
                .totalPrice(totalPrice).build());
    }
}


