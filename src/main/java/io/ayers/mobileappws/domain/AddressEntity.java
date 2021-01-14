package io.ayers.mobileappws.domain;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
public class AddressEntity
        implements Serializable {
    private static final long serialVersionUID = 836924623347689605L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Builder.Default
    @Column(nullable = false)
    private String addressId = UUID.randomUUID().toString();

    @Column(nullable = false, length = 15)
    private String city;

    @Column(nullable = false, length = 15)
    private String country;

    @Column(nullable = false, length = 100)
    private String streetName;

    @Column(nullable = false, length = 7)
    private String postalCode;

    @Column(nullable = false, length = 10)
    private String type;

    @JsonIgnore
    @ManyToMany(mappedBy = "addresses", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserEntity> userDetails = new ArrayList<>();

}
