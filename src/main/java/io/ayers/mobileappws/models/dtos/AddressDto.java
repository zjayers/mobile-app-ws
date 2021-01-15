package io.ayers.mobileappws.models.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto
        implements Serializable {
    private final static long serialVersionUID = -5504380137397504197L;
    private long id;
    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
    private List<UserDto> userDetails;
}
