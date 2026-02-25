package com.sipms.mapper;

import com.sipms.dto.purchaserequisitiondtos.PurchaseRequisitionDTO;
import com.sipms.model.InventoryPurchaseRequisition;

public class ProcurementMapper  {

    public PurchaseRequisitionDTO toRequisitionDTO(InventoryPurchaseRequisition requisition){

        if(requisition == null)
            return null;

        return PurchaseRequisitionDTO.builder()
                .id(Long.valueOf(requisition.getId()))
                .requesterId(Long.valueOf(requisition.getRequestedBy()))
                .requisitionNumber(requisition.getPrNumber())
                .build();

    }
}