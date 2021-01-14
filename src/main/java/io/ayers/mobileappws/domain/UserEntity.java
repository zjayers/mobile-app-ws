package io.ayers.mobileappws.domain;

import io.ayers.mobileappws.config.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


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

    @Builder.Default
    @Column(nullable = false, unique = true)
    private String userId = UUID.randomUUID().toString();

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

    @Builder.Default
    private String emailVerificationToken = Jwts.builder()
                                                .setSubject($default$userId())
                                                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                                                .signWith(SignatureAlgorithm.HS512, SecurityConstants.TOKEN_SECRET)
                                                .compact();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))

    @Builder.Default
    private List<AddressEntity> addresses = new ArrayList<>();

}
