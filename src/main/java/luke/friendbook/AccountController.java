package luke.friendbook;

import luke.friendbook.user.model.UserRequestModel;
import luke.friendbook.user.model.UserResponseModel;
import luke.friendbook.user.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController{

    private final IUserService userService;

    public AccountController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email")
    public ResponseEntity<Boolean> checkIfEmailExists(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.doesEmailExist(email));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseModel> register(@RequestBody UserRequestModel userRequestModel) {
        UserResponseModel savedUser = userService.register(userRequestModel);
        return ResponseEntity.ok().body(savedUser);
    }

    @PostMapping("/confirm-account")
    public void confirmAccount(@RequestBody String tokenUUID) {
        userService.confirmRegistration(tokenUUID);
    }
}















