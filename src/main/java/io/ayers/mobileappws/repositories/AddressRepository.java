package io.ayers.mobileappws.repositories;

import io.ayers.mobileappws.models.entities.AddressEntity;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository
        extends CrudRepository<AddressEntity, Long> {
}
