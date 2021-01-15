package io.ayers.mobileappws.models.requests;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserDetailsRequestModel {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Collection<AddressRequestModel> addresses = new ArrayList<>();
}
