package luke.friendbook.account;

import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.services.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseModel>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponseModel> getUserByUUID(@PathVariable String uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByUUID(uuid));
    }
}

















