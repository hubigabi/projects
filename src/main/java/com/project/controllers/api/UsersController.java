package com.project.controllers.api;

import com.project.model.User;
import com.project.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping()
    public ResponseEntity<CollectionModel<User>> findAllUsers() {
        List<User> allUsers = usersService.findAll();
        allUsers.forEach(user -> user.addIf(!user.hasLinks(),
                () -> getLinkToUser(user)));
        Link link = linkTo(UsersController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(allUsers, link));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") long id) {
        Optional<User> optionalUser = usersService.findById(id);
        return optionalUser.map(user -> {
            user.add(getLinkToUser(user));
            return new ResponseEntity<>(user, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody User user) {
        user = usersService.create(user);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    public ResponseEntity<User> update(@RequestBody User user) {
        user = usersService.update(user);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/existsByEmail/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable("email") String email) {
        Boolean existsByEmail = usersService.existsByEmail(email);
        return new ResponseEntity<>(existsByEmail, HttpStatus.OK);
    }

    private Link getLinkToUser(User user) {
        return linkTo(UsersController.class).slash(user.getID()).withSelfRel();
    }
}
