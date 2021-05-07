package luke.friendbook.account;

import luke.friendbook.account.model.UserRequestModel;
import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.services.IAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController{

    private final IAccountService accountService;

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/email")
    public ResponseEntity<Boolean> checkIfEmailExists(@RequestParam String email) {
        return ResponseEntity.ok().body(accountService.doesEmailExist(email));
    }

    @GetMapping("/reset-request")
    public void resetPasswordRequest(@RequestParam String email) {
        accountService.sendResetPasswordEmail(email);
    }

    @PutMapping("/reset-password")
    public void resetPassword(@RequestBody String token) {
        accountService.resetPasswordAndNotify(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseModel> register(@RequestBody UserRequestModel userRequestModel) {
        UserResponseModel savedUser = accountService.register(userRequestModel);
        return ResponseEntity.ok().body(savedUser);
    }

    @PostMapping("/confirm-account")
    public void confirmAccount(@RequestBody String tokenUUID) {
        accountService.confirmRegistration(tokenUUID);
    }

}















