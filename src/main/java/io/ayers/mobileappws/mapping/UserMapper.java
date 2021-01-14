package io.ayers.mobileappws.mapping;

import io.ayers.mobileappws.domain.AddressEntity;
import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.shared.AddressDto;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserDetailsRequestModel;
import io.ayers.mobileappws.ui.model.response.AddressResponseModel;
import io.ayers.mobileappws.ui.model.response.UserDetailsResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {


    // Step 1: Request Model To DTO
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encryptedPassword", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "emailVerificationStatus", ignore = true)
    UserDto requestModelToDto(UserDetailsRequestModel userDetailsRequestModel);

    // Step 2: DTO to Domain Entity
    UserEntity dtoToDomainEntity(UserDto userDto);

    // Step 3: Domain Entity to DTO@Mapping(target = "password", ignore = true)
    //    UserDto domainEntityToDto(UserEntity userEntity);
    @Mapping(target = "password", ignore = true)
    @Mapping(source = "addresses", target = "addresses", qualifiedByName = "addressWithoutUsers")
    UserDto domainEntityToDto(UserEntity userEntity);

    @Mapping(source = "addresses", target = "addresses", qualifiedByName = "addressWithoutUsers")
    Collection<UserDto> domainEntityToDto(Collection<UserEntity> userEntity);

    // Step 4: DTO to Response Model
    UserDetailsResponseModel dtoToResponseModel(UserDto userDto);

    Collection<UserDetailsResponseModel> dtoToResponseModel(Collection<UserDto> userDto);

    @Mapping(target = "userDetails", ignore = true)
    AddressDto addressEntityToDto(AddressEntity addressEntity);

    @Mapping(target = "userDetails", ignore = true)
    List<AddressResponseModel> addressDtoToResponse(List<AddressDto> addressDto);

    @Named("addressWithoutUsers")
    default List<AddressDto> addressEntityListToDtoList(List<AddressEntity> addressEntities) {
        return addressEntities.stream().map(this::addressEntityToDto).collect(Collectors.toList());
    }
}


