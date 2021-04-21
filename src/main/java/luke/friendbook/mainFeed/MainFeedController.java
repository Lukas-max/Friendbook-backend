package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.services.IFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequestMapping("/api/feed")
public class MainFeedController {

    private final IFeedService feedService;

    public MainFeedController(IFeedService feedService) {
        this.feedService = feedService;
    }

    @PostMapping
    public ResponseEntity<?> postFeed(@RequestBody String text) {
        feedService.saveTextFeed(text);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addons")
    public ResponseEntity<Number> postFeedWithFiles(@RequestBody MultipartFile[] files, @RequestParam String text) {
        int filesCopied = feedService.saveFeedWithFiles(files, text);
        return ResponseEntity.ok().body(filesCopied);
    }

    @PostMapping("/addons-comp")
    public ResponseEntity<?> postFeedWithFilesPlusCompressed(@RequestBody MultipartFile[] files,
                                                             @RequestBody MultipartFile[] images,
                                                             @RequestParam String text) {
        int filesCopied = feedService.saveFeedWithFilesPlusCompressed(files, images, text);
        return ResponseEntity.ok().body(filesCopied);
    }
}
