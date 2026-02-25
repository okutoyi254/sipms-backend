package com.sipms.service.impl;

import com.sipms.dto.purchaseorderdtos.*;
import com.sipms.dto.purchaserequisitiondtos.*;
import com.sipms.dto.quotationdtos.CreateQuotationRequest;
import com.sipms.dto.quotationdtos.QuotationDTO;
import com.sipms.dto.quotationdtos.UpdateQuotationRequest;
import com.sipms.dto.supplierdtos.*;
import com.sipms.enums.PRStatus;
import com.sipms.mapper.ProcurementMapper;
import com.sipms.model.InventoryPurchaseRequisition;
import com.sipms.model.InventoryPurchaseRequisitionItem;
import com.sipms.repository.PurchaseRequisitionRepository;
import com.sipms.service.ProcurementService;
import com.sipms.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ProcurementServiceImpl implements ProcurementService {

    private final ValidationUtil validationUtil;
    private final PurchaseRequisitionRepository requisitionRepository;
    private final ProcurementMapper mapper;

    @Override
    public PurchaseRequisitionDTO createRequisition(CreateRequisitionRequest request) {
        log.info("Creating new purchase requisition for requester: {}", request.getRequesterId());

        validationUtil.validateCreateRequisitionRequest(request);

        InventoryPurchaseRequisition requisition = InventoryPurchaseRequisition.builder()
                .requestedBy(request.getRequesterId())
                .departmentId(request.getDepartmentId())
                .description(request.getDescription())
                .justification(request.getJustification())
                .priority(request.getPriority())
                .status(PRStatus.DRAFT)
                .build();

        AtomicInteger lineCounter = new AtomicInteger(1);

        List<InventoryPurchaseRequisitionItem> items = request.getItems().stream()
                .map(itemDto -> new InventoryPurchaseRequisitionItem(
                        requisition,
                        String.valueOf(lineCounter.getAndIncrement()),
                        itemDto.getProductCode(),
                        itemDto.getUnitOfMeasure(),
                        itemDto.getQuantityRequested()
                ))
                .toList();
        requisition.setItems(items);
        requisition.calculateTotalCost();

        InventoryPurchaseRequisition saved = requisitionRepository.save(requisition);
        log.info("Purchase requisition created successfully with ID:{}",saved.getId());

        return mapper.toRequisitionDTO(saved);


    }


    @Override
    public PurchaseRequisitionDTO updateRequisition(Long id, UpdateRequisitionRequest request) {
        return null;
    }

    @Override
    public PurchaseRequisitionDTO getRequisitionById(Long id) {
        return null;
    }

    @Override
    public List<PurchaseRequisitionDTO> getAllRequisitions(RequisitionFilter filter) {
        return List.of();
    }

    @Override
    public PurchaseRequisitionDTO submitRequisition(Long id) {
        return null;
    }

    @Override
    public PurchaseRequisitionDTO approveRequisition(Long id, ApprovalRequest request) {
        return null;
    }

    @Override
    public PurchaseRequisitionDTO rejectRequisition(Long id, RejectionRequest request) {
        return null;
    }

    @Override
    public void deleteRequisition(Long id) {

    }

    @Override
    public PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request) {
        return null;
    }

    @Override
    public PurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderRequest request) {
        return null;
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        return null;
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders(PurchaseOrderFilter filter) {
        return List.of();
    }

    @Override
    public PurchaseOrderDTO approvePurchaseOrder(Long id, ApprovePurchaseOrderRequest request) {
        return null;
    }

    @Override
    public PurchaseOrderDTO sendPurchaseOrder(Long id) {
        return null;
    }

    @Override
    public PurchaseOrderDTO acknowledgePurchaseOrder(Long id, AcknowledgementRequest request) {
        return null;
    }

    @Override
    public void cancelPurchaseOrder(Long id, CancellationRequest request) {

    }

    @Override
    public QuotationDTO createQuotation(CreateQuotationRequest request) {
        return null;
    }

    @Override
    public QuotationDTO updateQuotation(Long id, UpdateQuotationRequest request) {
        return null;
    }

    @Override
    public QuotationDTO getQuotationById(Long id) {
        return null;
    }

    @Override
    public List<QuotationDTO> getQuotationsByRequisition(Long requisitionId) {
        return List.of();
    }

    @Override
    public QuotationDTO selectQuotation(Long id) {
        return null;
    }

    @Override
    public void deleteQuotation(Long id) {

    }

    @Override
    public SupplierDTO registerSupplier(CreateSupplierRequest request) {
        return null;
    }

    @Override
    public SupplierDTO updateSupplier(Long id, UpdateSupplierRequest request) {
        return null;
    }

    @Override
    public SupplierDTO getSupplierById(Long id) {
        return null;
    }

    @Override
    public List<SupplierDTO> getAllSuppliers(SupplierFilter filter) {
        return List.of();
    }

    @Override
    public SupplierDTO activateSupplier(Long id) {
        return null;
    }

    @Override
    public SupplierDTO deactivateSupplier(Long id) {
        return null;
    }

    @Override
    public SupplierPerformanceDTO getSupplierPerformance(Long vendorId) {
        return null;
    }

}
