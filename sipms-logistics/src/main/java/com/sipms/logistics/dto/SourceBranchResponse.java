package com.sipms.logistics.dto;

import com.sipms.branch.model.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceBranchResponse {
    private boolean found;
    private Branch branch;
    private String message;
}
