package com.sipms.logistics.controller;


import com.sipms.logistics.dto.*;

import com.sipms.logistics.entity.StockTransferRequest;
import com.sipms.logistics.service.BranchTransferService;
import com.sipms.branch.model.Branch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class BranchTransferController {

    private final BranchTransferService branchTransferService;

    @GetMapping("/source-branch")
    public ResponseEntity<SourceBranchResponse> findSourceBranch(
            @RequestParam Long productId,
            @RequestParam Long destinationBranchId,
            @RequestParam Integer requiredQuantity
    ) {
        log.info("Finding source branch for product: {}, destination: {}, quantity: {}",
                productId, destinationBranchId, requiredQuantity);

        Optional<Branch> sourceBranch = branchTransferService.findSourceBranch(
                productId, destinationBranchId, requiredQuantity
        );

        return sourceBranch.map(branch -> ResponseEntity.ok(new SourceBranchResponse(
                true,
                branch,
                "Source branch found successfully"
        ))).orElseGet(() -> ResponseEntity.ok(new SourceBranchResponse(
                false,
                null,
                "No suitable source branch found"
        )));

    }

    @PostMapping("/auto-create/{alertId}")
    public ResponseEntity<TransferResponse> autoCreateTransferFromAlert(
            @PathVariable Long alertId
    ) {
        log.info("Auto-creating transfer request for alert: {}", alertId);

        try {
            StockTransferRequest transfer = branchTransferService.autoCreateTransferFromAlert(alertId);

            if (transfer == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new TransferResponse(
                                false,
                                null,
                                "Cannot create transfer: No source branch available"
                        ));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TransferResponse(
                            true,
                            transfer,
                            "Transfer request created successfully"
                    ));

        } catch (RuntimeException e) {
            log.error("Error auto-creating transfer from alert: {}", alertId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TransferResponse(false, null, e.getMessage()));
        }
    }

    @PostMapping("/manual")
    public ResponseEntity<TransferResponse> createManualTransferRequest(
            @Valid @RequestBody ManualTransferRequest request
    ) {
        log.info("Creating manual transfer request from branch: {} to branch: {}",
                request.getSourceBranchId(), request.getDestinationBranchId());

        try {
            StockTransferRequest transfer = branchTransferService.createManualTransferRequest(
                    request.getSourceBranchId(),
                    request.getDestinationBranchId(),
                    request.getItems(),
                    request.getRequestedBy()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TransferResponse(
                            true,
                            transfer,
                            "Manual transfer request created successfully"
                    ));

        } catch (RuntimeException e) {
            log.error("Error creating manual transfer request", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TransferResponse(false, null, e.getMessage()));
        }
    }

    @PutMapping("/{transferId}/approve")
    public ResponseEntity<ApiResponse> approveTransfer(
            @PathVariable Long transferId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        log.info("Approving transfer request: {} by {}", transferId, request.getApprovedBy());

        try {
            branchTransferService.approveTransfer(transferId, request.getApprovedBy());

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Transfer request approved successfully"
            ));

        } catch (RuntimeException e) {
            log.error("Error approving transfer: {}", transferId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{transferId}/reject")
    public ResponseEntity<ApiResponse> rejectTransfer(
            @PathVariable Long transferId,
            @Valid @RequestBody RejectionRequest request
    ) {
        log.info("Rejecting transfer request: {} by {}", transferId, request.getRejectedBy());

        try {
            branchTransferService.rejectTransfer(
                    transferId,
                    request.getRejectedBy(),
                    request.getReason()
            );

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Transfer request rejected successfully"
            ));

        } catch (RuntimeException e) {
            log.error("Error rejecting transfer: {}", transferId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }


    @PutMapping("/{transferId}/ship")
    public ResponseEntity<ApiResponse> shipTransfer(
            @PathVariable Long transferId,
            @Valid @RequestBody ShipmentRequest request
    ) {
        log.info("Shipping transfer request: {} by {}", transferId, request.getShippedBy());

        try {
            branchTransferService.shipTransfer(
                    transferId,
                    request.getShippedBy(),
                    request.getCarrier(),
                    request.getTrackingNumber()
            );

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Transfer shipped successfully"
            ));

        } catch (RuntimeException e) {
            log.error("Error shipping transfer: {}", transferId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{transferId}/receive")
    public ResponseEntity<ApiResponse> receiveTransfer(
            @PathVariable Long transferId,
            @Valid @RequestBody ReceiveTransferRequest request
    ) {
        log.info("Receiving transfer request: {} by {}", transferId, request.getReceivedBy());

        try {
            branchTransferService.receiveTransfer(
                    transferId,
                    request.getReceivedBy(),
                    request.getReceivedItems()
            );

            return ResponseEntity.ok(new ApiResponse(
                    true,
                    "Transfer received successfully"
            ));

        } catch (RuntimeException e) {
            log.error("Error receiving transfer: {}", transferId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception e) {
        log.error("Unexpected error in BranchTransferController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "An unexpected error occurred: " + e.getMessage()));
    }
}
