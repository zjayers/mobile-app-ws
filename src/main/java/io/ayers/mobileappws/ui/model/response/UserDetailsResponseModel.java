package io.ayers.mobileappws.ui.model.response;

import lombok.*;

import java.util.Collection;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponseModel {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private Collection<AddressResponseModel> addresses;
}
