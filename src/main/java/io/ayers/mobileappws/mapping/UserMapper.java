package io.ayers.mobileappws.mapping;

import io.ayers.mobileappws.domain.UserEntity;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserDetailsRequestModel;
import io.ayers.mobileappws.ui.model.response.UserDetailsResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

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
    UserDto domainEntityToDto(UserEntity userEntity);

    Collection<UserDto> domainEntityToDto(Collection<UserEntity> userEntity);

    // Step 4: DTO to Response Model
    UserDetailsResponseModel dtoToResponseModel(UserDto userDto);

    Collection<UserDetailsResponseModel> dtoToResponseModel(Collection<UserDto> userDto);


}
