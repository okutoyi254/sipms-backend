package serviceImplementation;

import dto.OrderedProduct;
import dto.ShipProduct;
import lombok.extern.slf4j.Slf4j;
import model.*;
import org.springframework.stereotype.Service;
import repository.*;

import java.util.List;


@Service
@Slf4j
public class GeneralManagerService {

    private  final SupplierRepository supplierRepository;
    private final DeliveryLogsRepository logsRepository;
    private final BranchRepository branchRepository;
    private final ShippedProductRepo shippedProductRepo;

    public GeneralManagerService(SupplierRepository supplierRepository, DeliveryLogsRepository logsRepository, BranchRepository branchRepository, ShippedProductRepo shippedProductRepo) {
        this.supplierRepository = supplierRepository;
        this.logsRepository = logsRepository;
        this.branchRepository = branchRepository;
        this.shippedProductRepo = shippedProductRepo;
    }

    public DeliveryLogs orderPlacement(Integer supplierId, List<OrderedProduct>products) {
        Supplier supplier=supplierRepository.findById(supplierId).orElseThrow();

        double totalPrice=0;

        for(OrderedProduct product:products) {

            totalPrice +=product.unitPrice()* product.quantity();

        }

        return logsRepository.save(SupplierDeliveryLogs.builder()
                .supplierName(supplier.getSupplierName())
                .deliveryStatus(DeliveryStatus.PENDING)
                .supplierEmail(supplier.getSupplierName())
                .totalItems(products.size())
                .totalPrice(totalPrice).build());
    }

    public StockMovement shipProductFxn(ShipProduct product) {

        Branch source = branchRepository.findById(product.source()).orElseThrow();
        Branch destination = branchRepository.findById(product.destination()).orElseThrow();

        for(Product prod :source.getProductList()){
            if(prod.getProductId()== product.productId()){

                log.info("Initial product quantity for source{}", prod.getProductQuantity());
                prod.setProductQuantity(prod.getProductQuantity()- product.quantity());
                log.info("Final product quantity for source{}", prod.getProductQuantity());

            }
        }



        branchRepository.save(source);


        ShippedProductRecord record=ShippedProductRecord.builder()
                        .source(source.getBranchId())
                         .destination(destination.getBranchId())
                        .productId(product.productId())
                        .quantity(product.quantity())
                .shippingstatus(SHIPPINGSTATUS.PENDING_DELIVERY).build();

        return shippedProductRepo.save(record);

    }

    public ShippedProductRecord shippedProductsReceivedAndVerified( ShipProduct product){


        Branch destination = branchRepository.findById(product.destination()).orElseThrow();
        for(Product prod: destination.getProductList()){
            if(prod.getProductId()== product.productId()){
                log.info("Initial product quantity for destination{}", prod.getProductQuantity());
                prod.setProductQuantity(prod.getProductQuantity()+ product.quantity());
                log.info("Final product quantity for destination{}", prod.getProductQuantity());

            }
        }

        branchRepository.save(destination);

        ShippedProductRecord shippedProduct=shippedProductRepo.findById(product.productId())
                .orElseThrow(()->new RuntimeException("No shipped product with the given id"));
        shippedProduct.setShippingstatus(SHIPPINGSTATUS.VERIFIED);

        return shippedProductRepo.save(shippedProduct);
    }
}


