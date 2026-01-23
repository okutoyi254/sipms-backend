package entity;

@lombok.Data
public class ReceiptItem{
    private Long itemId;
    private Integer receivedQuantity;
    private Integer damagedQuantity;
    private String notes;
}
