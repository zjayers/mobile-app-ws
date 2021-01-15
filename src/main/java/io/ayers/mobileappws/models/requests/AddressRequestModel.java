package io.ayers.mobileappws.models.requests;

import lombok.Data;

@Data
public class AddressRequestModel {
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
}
