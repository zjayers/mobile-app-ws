package io.ayers.mobileappws.models.requests;

import lombok.Data;

@Data
public class UserLoginRequestModel {
    private String email;
    private String password;
}
