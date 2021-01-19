package io.ayers.mobileappws.repositories;

import io.ayers.mobileappws.models.entities.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository
        extends CrudRepository<AuthorityEntity, Long> {
    AuthorityEntity findByName(String name);
}
