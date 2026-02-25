package com.sipms.service;

import com.sipms.dto.purchaseorderdtos.*;
import com.sipms.dto.purchaserequisitiondtos.*;
import com.sipms.dto.quotationdtos.CreateQuotationRequest;
import com.sipms.dto.quotationdtos.QuotationDTO;
import com.sipms.dto.quotationdtos.UpdateQuotationRequest;
import com.sipms.dto.supplierdtos.*;

import java.util.List;

public interface ProcurementService {

    PurchaseRequisitionDTO createRequisition(CreateRequisitionRequest request);
    PurchaseRequisitionDTO updateRequisition(Long id, UpdateRequisitionRequest request);
    PurchaseRequisitionDTO getRequisitionById(Long id);
    List<PurchaseRequisitionDTO> getAllRequisitions(RequisitionFilter filter);
    PurchaseRequisitionDTO submitRequisition(Long id);
    PurchaseRequisitionDTO approveRequisition(Long id, ApprovalRequest request);
    PurchaseRequisitionDTO rejectRequisition(Long id, RejectionRequest request);
    void deleteRequisition(Long id);

    PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request);
    PurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderRequest request);
    PurchaseOrderDTO getPurchaseOrderById(Long id);
    List<PurchaseOrderDTO> getAllPurchaseOrders(PurchaseOrderFilter filter);
    PurchaseOrderDTO approvePurchaseOrder(Long id, ApprovePurchaseOrderRequest request);
    PurchaseOrderDTO sendPurchaseOrder(Long id);
    PurchaseOrderDTO acknowledgePurchaseOrder(Long id, AcknowledgementRequest request);
    void cancelPurchaseOrder(Long id, CancellationRequest request);

    QuotationDTO createQuotation(CreateQuotationRequest request);
    QuotationDTO updateQuotation(Long id, UpdateQuotationRequest request);
    QuotationDTO getQuotationById(Long id);
    List<QuotationDTO> getQuotationsByRequisition(Long requisitionId);
    QuotationDTO selectQuotation(Long id);
    void deleteQuotation(Long id);

    SupplierDTO registerSupplier(CreateSupplierRequest request);
    SupplierDTO updateSupplier(Long id, UpdateSupplierRequest request);
    SupplierDTO getSupplierById(Long id);
    List<SupplierDTO> getAllSuppliers(SupplierFilter filter);
    SupplierDTO activateSupplier(Long id);
    SupplierDTO deactivateSupplier(Long id);
    SupplierPerformanceDTO getSupplierPerformance(Long vendorId);

}
