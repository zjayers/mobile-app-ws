package io.ayers.mobileappws.models.responses;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageResponseModel {
    private Date timestamp;
    private String message;
}
