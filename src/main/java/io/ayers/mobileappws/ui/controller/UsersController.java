package io.ayers.mobileappws.ui.controller;

import io.ayers.mobileappws.mapping.UserMapper;
import io.ayers.mobileappws.services.UserService;
import io.ayers.mobileappws.shared.UserDto;
import io.ayers.mobileappws.ui.model.request.UserDetailsRequestModel;
import io.ayers.mobileappws.ui.model.response.UserDetailsResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        path = "/users",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
public class UsersController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<String> getAll() {
        return ResponseEntity.status(HttpStatus.OK)
                             .body("Get all was called");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseModel> getOne(@PathVariable(name = "userId") String userId) {

        UserDto userDto = userService.getUserDetailsByUserId(userId);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(userDto);

        return ResponseEntity.status(HttpStatus.OK)
                             .body(userDetailsResponseModel);
    }

    @PostMapping
    public ResponseEntity<UserDetailsResponseModel> createOne(@RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = userMapper.requestModelToDto(userDetailsRequestModel);
        UserDto savedUserDto = userService.createUser(userDto);
        UserDetailsResponseModel userDetailsResponseModel = userMapper.dtoToResponseModel(savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(userDetailsResponseModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateOne(@PathVariable(name = "id") String id) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(String.format("Update one was called with id: '%s'", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOne(@PathVariable(name = "id") String id) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(String.format("Delete one was called with id: '%s'", id));
    }

}
