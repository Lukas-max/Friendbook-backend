package luke.friendbook.account;

import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.services.IUserService;
import luke.friendbook.account.model.UserRequestModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController{

    private final IUserService userService;

    public AccountController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> test() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        System.out.println("getName(): " + authentication.getName());
        System.out.println("isAuthenticated: " +  authentication.isAuthenticated());
        System.out.println("getPrincipal: " + authentication.getPrincipal());
        System.out.println("roles: " +  authentication.getAuthorities().toString());
        return ResponseEntity.ok().build();
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















