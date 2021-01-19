package io.ayers.mobileappws.models.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "authorities")
public class AuthorityEntity
        implements Serializable {
    private static final long serialVersionUID = -1337646607508011556L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.LAZY)
    private List<RoleEntity> roles = new ArrayList<>();
}
