package io.ayers.mobileappws.services.interfaces;

import io.ayers.mobileappws.models.dtos.AddressDto;

import java.util.Collection;

public interface AddressService {
    Collection<AddressDto> getUserAddresses(String userId);
}
