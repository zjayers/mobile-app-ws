package io.ayers.mobileappws.models.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationStatusResponseModel {
    private String operationResult;
    private String operationName;
}
