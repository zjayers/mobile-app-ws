package io.ayers.mobileappws.ui.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsRequestModel {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
}
