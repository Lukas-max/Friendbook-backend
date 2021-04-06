package luke.friendbook;

import luke.friendbook.user.model.User;
import luke.friendbook.user.model.UserRequestDto;
import luke.friendbook.user.model.UserResponseDto;
import luke.friendbook.user.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final IUserService userService;

    public AccountController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto savedUser = userService.register(userRequestDto);
        return ResponseEntity.ok().body(savedUser);
    }

    @PostMapping("/confirm-account")
    public void confirmAccount(@RequestBody String tokenUUID) {
        userService.confirmRegistration(tokenUUID);
    }
}















