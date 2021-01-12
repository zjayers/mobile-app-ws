package io.ayers.mobileappws.shared;

import lombok.*;

import java.io.Serializable;


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
    private Boolean emailVerificationStatus = false;
}