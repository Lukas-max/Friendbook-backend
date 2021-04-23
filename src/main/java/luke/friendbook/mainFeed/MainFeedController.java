package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.mainFeed.services.IFeedService;
import luke.friendbook.storage.model.DirectoryType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class MainFeedController {

    private final IFeedService feedService;

    public MainFeedController(IFeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<FeedModelDto>> getFeed() throws IOException {
        List<FeedModelDto> feedModelDtoList = feedService.findFeedData();
        return ResponseEntity.ok().body(feedModelDtoList);
    }

    @GetMapping("/{feedId}/{fileName}")
    public ResponseEntity<byte[]> downloadFeedFile(@PathVariable String feedId, @PathVariable String fileName) {
        byte[] data = feedService.download(feedId, fileName, DirectoryType.STANDARD_DIRECTORY);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "dodano plik " + fileName)
                .body(data);
    }

    @GetMapping("/image/{feedId}/{fileName}")
    public ResponseEntity<byte[]> downloadFeedImageFile(@PathVariable String feedId, @PathVariable String fileName) {
        byte[] data = feedService.download(feedId, fileName, DirectoryType.IMAGE_DIRECTORY);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "dodano plik " + fileName)
                .body(data);
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
    public ResponseEntity<Number> postFeedWithFilesPlusCompressed(@RequestBody MultipartFile[] files,
                                                             @RequestBody MultipartFile[] images,
                                                             @RequestParam String text) {
        int filesCopied = feedService.saveFeedWithFilesPlusCompressed(files, images, text);
        return ResponseEntity.ok().body(filesCopied);
    }
}
