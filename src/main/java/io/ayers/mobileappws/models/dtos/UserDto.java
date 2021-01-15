package io.ayers.mobileappws.models.dtos;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto
        implements Serializable {
    private static final long serialVersionUID = 2375589247238863218L;

    private long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String encryptedPassword;
    private String emailVerificationToken;

    @Builder.Default
    private List<AddressDto> addresses = new ArrayList<>();

    @Builder.Default
    private Boolean emailVerificationStatus = false;
}
