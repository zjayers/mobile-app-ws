package io.ayers.mobileappws.ui.model.response;

import lombok.*;

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
}
