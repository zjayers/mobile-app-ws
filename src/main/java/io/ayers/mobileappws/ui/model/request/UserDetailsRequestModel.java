package io.ayers.mobileappws.ui.model.request;

import lombok.Data;

@Data
public class UserDetailsRequestModel {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
