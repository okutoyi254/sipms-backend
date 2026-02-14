package com.sipms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inventory_purchase_requisition", schema = "procurement")
public class InventoryPurchaseRequisition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "pr_number", nullable = false, length = 50)
    private String prNumber;

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "pr_date", nullable = false)
    private LocalDate prDate;

    @Column(name = "department_id")
    private Integer departmentId;

    @NotNull
    @Column(name = "requested_by", nullable = false)
    private Integer requestedBy;

    @Column(name = "cost_center_id")
    private Integer costCenterId;

    @Size(max = 100)
    @Column(name = "project_code", length = 100)
    private String projectCode;

    @NotNull
    @Column(name = "required_date", nullable = false)
    private LocalDate requiredDate;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'MEDIUM'")
    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'DRAFT'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "purpose", length = Integer.MAX_VALUE)
    private String purpose;

    @Column(name = "justification", length = Integer.MAX_VALUE)
    private String justification;

    @Column(name = "estimated_budget", precision = 15, scale = 2)
    private BigDecimal estimatedBudget;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Size(max = 100)
    @Column(name = "approval_workflow_id", length = 100)
    private String approvalWorkflowId;

    @Column(name = "rejection_reason", length = Integer.MAX_VALUE)
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;


}