package com.sipms.repository;

import com.sipms.enums.PRItemStatus;
import com.sipms.model.InventoryPurchaseOrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderItemRepository {

    // Find by PO
    List<InventoryPurchaseOrderItem> findByPurchaseOrderId(Long poId);

    // Find by PO and status
    List<InventoryPurchaseOrderItem> findByPurchaseOrderIdAndStatus(Long poId, PRItemStatus status);

    // Find by product
    List<InventoryPurchaseOrderItem> findByProductId(Long productId);

    // Find by PR item
    Optional<InventoryPurchaseOrderItem> findByPrItemId(Long prItemId);

    // Count items by PO
    long countByPurchaseOrderId(Long poId);

    // Find pending receipt items
    @Query("SELECT poi FROM InventoryPurchaseOrderItem poi WHERE poi.status IN ('PENDING', 'PARTIALLY_RECEIVED') AND " +
            "poi.purchaseOrder.status IN ('ACKNOWLEDGED', 'PARTIALLY_RECEIVED') AND " +
            "poi.purchaseOrder.isDeleted = false")
    List<InventoryPurchaseOrderItem> findPendingReceiptItems();

    // Calculate fulfillment percentage for PO
    @Query("SELECT SUM(poi.quantityOrdered), SUM(poi.quantityReceived) FROM InventoryPurchaseOrderItem poi " +
            "WHERE poi.purchaseOrder.id = :poId")
    Object[] calculateFulfillmentStats(@Param("poId") Long poId);

    // Sum quantity ordered by product
    @Query("SELECT SUM(poi.quantityOrdered) FROM InventoryPurchaseOrderItem poi WHERE poi.productId = :productId AND " +
            "poi.purchaseOrder.status NOT IN ('CANCELLED') AND poi.purchaseOrder.isDeleted = false")
    Double sumQuantityOrderedByProduct(@Param("productId") Long productId);

    // Find items by product and supplier
    @Query("SELECT poi FROM InventoryPurchaseOrderItem poi WHERE poi.productId = :productId AND " +
            "poi.purchaseOrder.supplier.id = :supplierId AND poi.purchaseOrder.isDeleted = false " +
            "ORDER BY poi.purchaseOrder.poDate DESC")
    List<InventoryPurchaseOrderItem> findByProductAndSupplier(@Param("productId") Long productId,
                                                              @Param("supplierId") Long supplierId);

    // Get average price for product
    @Query("SELECT AVG(poi.unitPrice) FROM InventoryPurchaseOrderItem poi WHERE poi.productId = :productId AND " +
            "poi.purchaseOrder.status IN ('FULLY_RECEIVED', 'CLOSED') AND " +
            "poi.purchaseOrder.poDate >= :fromDate AND poi.purchaseOrder.isDeleted = false")
    Double getAveragePrice(@Param("productId") Long productId, @Param("fromDate") LocalDate fromDate);
}
