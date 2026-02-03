package com.sipms.logistics.entity;

import com.sipms.logistics.enums.TransferPriority;
import com.sipms.logistics.enums.TransferStatus;
import jakarta.persistence.*;
import lombok.*;
import com.sipms.branch.model.Branch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stock_transfer_requests",schema = "logistics",
       uniqueConstraints = @UniqueConstraint(columnNames = {"transferNumber"}))
@Data
@EqualsAndHashCode(callSuper = true,exclude = {"transferItems"})
@ToString(exclude = {"sourceBranch","destinationBranch","transferItems"})
public class StockTransferRequest extends BaseEntity{

    @Column(nullable = false, unique = true, length = 50)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_branch_id", nullable = false)
    private Branch sourceBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_branch_id", nullable = false)
    private Branch destinationBranch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferPriority priority;

    @Column(nullable = false)
    private LocalDate requestDate;

    private LocalDate approvalDate;

    @Column(length = 100)
    private String requestedBy;

    @Column(length = 100)
    private String approvedBy;

    @Column(length = 100)
    private String shippedBy;

    @Column(length = 100)
    private String receivedBy;

    @OneToMany(mappedBy = "transferRequest",
              cascade = CascadeType.ALL,
              orphanRemoval = true)
    private List<StockTransferItem> transferItems = new ArrayList<>();

    @Column(length = 100)
    private String shippingCarrier;

    @Column(length = 100)
    private String trackingNumber;

    public void addItem(StockTransferItem item) {
        transferItems.add(item);
        item.setTransferRequest(this);
    }

    public void removeItem(StockTransferItem item) {
        transferItems.remove(item);
        item.setTransferRequest(null);
    }
}
