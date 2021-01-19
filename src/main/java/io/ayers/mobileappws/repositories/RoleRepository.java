package io.ayers.mobileappws.repositories;

import io.ayers.mobileappws.models.entities.RoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository
        extends CrudRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
