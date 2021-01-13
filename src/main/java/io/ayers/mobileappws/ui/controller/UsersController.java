package io.ayers.mobileappws.ui.controller;

import io.ayers.mobileappws.mapping.UserMapper;
import io.ayers.mobileappws.services.UserService;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserDetailsRequestModel;
import io.ayers.mobileappws.ui.model.response.OperationStatusModel;
import io.ayers.mobileappws.ui.model.response.UserDetailsResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(
        path = "/users",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
public class UsersController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDetailsResponseModel>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "25") int limit) {

        Collection<UserDto> userDtos = userService.getUsers(page, limit);
        Collection<UserDetailsResponseModel> userDetailsResponseModels = userMapper.dtoToResponseModel(userDtos);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModels);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseModel> getOne(@PathVariable(name = "userId") String userId) {

        UserDto userDto = userService.getUserDetailsByUserId(userId);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(userDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> createOne(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto savedUserDto = userService.createUser(userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userDetailsResponseModel);
    }

    @PutMapping(path = "/{userId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserDetailsResponseModel> updateOne(@PathVariable(name = "userId") String userId,
                                                              @RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto updatedUserDto = userService.updateUser(userId, userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(updatedUserDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<OperationStatusModel> deleteOne(@PathVariable(name = "userId") String userId) {

        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(OperationStatusModel.builder()
                                                       .operationName("DELETE")
                                                       .operationResult("SUCCESS")
                                                       .build());
    }

}
