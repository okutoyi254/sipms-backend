package com.sipms.logistics.entity;

@lombok.Data
public class ReceiptItem{
    private Long itemId;
    private Integer receivedQuantity;
    private Integer damagedQuantity;
    private String notes;
}
