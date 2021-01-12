package io.ayers.mobileappws.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity
        implements Serializable {
    private static final long serialVersionUID = 3879730109360508434L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerificationStatus = false;

    private String emailVerificationToken;
}
