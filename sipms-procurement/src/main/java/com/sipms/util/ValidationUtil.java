package com.sipms.util;

import com.sipms.dto.purchaserequisitiondtos.CreateRequisitionItemRequest;
import com.sipms.dto.purchaserequisitiondtos.CreateRequisitionRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    private static  final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9+\\-\\s()]+$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9-_]+$"
    );

    public void validateCreateRequisitionRequest(CreateRequisitionRequest request) {

        log.debug("Validating create requisition request: {}", request);

        List<String> errors = new ArrayList<>();

        if(request ==null){
            throw new ValidationException("Request cannot be null");
        }

        if(request.getRequesterId()==null || request.getRequesterId()<=0){
            errors.add("Requester id must be a positive number");
        }

        if(request.getDepartmentId()==null || request.getDepartmentId()<=0){
            errors.add("Department id must be a positive number");
        }

        if(request.getPriority() == null){
            errors.add("Priority is required");
        }

        if(request.getItems()==null || request.getItems().isEmpty()){
            errors.add("At least one item is required");
        }else{
           validateRequisitionItems(request.getItems(),errors);
    }

        if(!errors.isEmpty()){
            log.warn("Validation failed for create requisition request: {}. Errors: {}", request, errors);
            throw new ValidationException(String.join("; ", errors));
        }

        log.debug("Create requisition request is valid");
    }

    private void validateRequisitionItems(List<CreateRequisitionItemRequest>items,List<String> errors){

        if(items.size()>500){
            errors.add("Cannot have more than 500 items in a requisition");
        }
        for(int i=0;i<items.size();i++){
            CreateRequisitionItemRequest item = items.get(i);
            String prefix = "Item "+(i+1)+": ";

            if(item.getProductCode()==null || item.getProductCode().isBlank()) {
                errors.add(prefix + "Item code is required");
            }

//
//            if(item.getQuantity()==null || item.getQuantity()<=0){
//                errors.add(prefix + "Quantity must be a positive number");
//            }
//            else if(item.getQuantity()>10000){
//                errors.add(prefix + "Quantity cannot exceed 10,000");
//            }
//            if(item.getUnit()==null || item.getUnit().isBlank()){
//                errors.add(prefix + "Unit is required");
//            }

            if(item.getQuantityRequested()==null || item.getQuantityRequested().compareTo(BigDecimal.ZERO)<=0){
                errors.add(String.format("Item %d: Quantity must be a positive number", i+1));
            }
        }
    }

}
