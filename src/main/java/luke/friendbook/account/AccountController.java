package luke.friendbook.account;

import luke.friendbook.account.model.Credentials;
import luke.friendbook.account.model.MailData;
import luke.friendbook.account.model.UserRequestModel;
import luke.friendbook.account.model.UserResponseModel;
import luke.friendbook.account.services.IAccountService;
import luke.friendbook.exception.model.ArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$"))
            throw new ArgumentNotValidException("Podano niepoprawny email");

        accountService.sendResetPasswordEmail(email);
    }

    @PatchMapping("/reset-password")
    public void resetPassword(@RequestBody String token) {
        accountService.resetPasswordAndNotify(token);
    }

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody Credentials credentials) {
        accountService.changePassword(credentials);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody MailData mailData) {
        accountService.changeEmail(mailData.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseModel> register(@Valid @RequestBody UserRequestModel userRequestModel) {
        UserResponseModel savedUser = accountService.register(userRequestModel);
        return ResponseEntity.ok().body(savedUser);
    }

    @PostMapping("/confirm-account")
    public void confirmAccount(@RequestBody String tokenUUID) {
        accountService.confirmRegistration(tokenUUID);
    }

    @DeleteMapping
    public void deleteAccount() {
        accountService.deleteAccount();
    }
}















