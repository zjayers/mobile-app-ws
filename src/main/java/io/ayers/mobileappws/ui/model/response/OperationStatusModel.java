package io.ayers.mobileappws.ui.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationStatusModel {
    private String operationResult;
    private String operationName;
}
