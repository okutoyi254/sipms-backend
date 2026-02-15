package com.sipms.repository;

import com.sipms.model.InventoryGoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryGoodsReceiptItemRepository extends JpaRepository<InventoryGoodsReceiptItem,Long> {

    // Find by GRN
    List<InventoryGoodsReceiptItem> findByGoodsReceiptNoteId(Long grnId);

    // Find by PO item
    List<InventoryGoodsReceiptItem> findByPoItemId(Long poItemId);

    // Find by product
    List<InventoryGoodsReceiptItem> findByProductId(Long productId);

    // Find by batch number
    List<InventoryGoodsReceiptItem> findByBatchNumber(String batchNumber);

    // Find items with rejections
    @Query("SELECT gri FROM InventoryGoodsReceiptItem gri WHERE gri.quantityRejected > 0 AND " +
            "gri.goodsReceiptNote.isDeleted = false")
    List<InventoryGoodsReceiptItem> findItemsWithRejections();

    // Find by condition
    List<InventoryGoodsReceiptItem> findByCondition(String condition);

    // Find expiring items
    @Query("SELECT gri FROM InventoryGoodsReceiptItem gri WHERE gri.expiryDate BETWEEN :startDate AND :endDate AND " +
            "gri.goodsReceiptNote.status = 'POSTED_TO_INVENTORY' AND gri.goodsReceiptNote.isDeleted = false")
    List<InventoryGoodsReceiptItem> findExpiringItems(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    // Count items by GRN
    long countByGoodsReceiptNoteId(Long grnId);

    // Sum quantities by product
    @Query("SELECT SUM(gri.quantityAccepted) FROM InventoryGoodsReceiptItem gri WHERE gri.productId = :productId AND " +
            "gri.goodsReceiptNote.grnDate BETWEEN :startDate AND :endDate AND " +
            "gri.goodsReceiptNote.isDeleted = false")
    double sumQuantityReceivedByProduct(@Param("productId") Long productId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}

