package io.ayers.mobileappws.models.requests;

import lombok.Data;

@Data
public class PasswordResetModel {
    private String token;
    private String password;
}
