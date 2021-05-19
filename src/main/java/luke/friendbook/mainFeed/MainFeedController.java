package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.mainFeed.services.IFeedCommentService;
import luke.friendbook.mainFeed.services.IFeedService;
import luke.friendbook.model.Chunk;
import luke.friendbook.storage.model.DirectoryType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/feed")
public class MainFeedController {

    private final IFeedService feedService;
    private final IFeedCommentService commentService;
    private final SimpMessageSendingOperations messageTemplate;

    public MainFeedController(IFeedService feedService,
                              IFeedCommentService commentService,
                              SimpMessageSendingOperations messageTemplate) {
        this.feedService = feedService;
        this.commentService = commentService;
        this.messageTemplate = messageTemplate;
    }

    // działaj dalej tutaj:
    @GetMapping
    public ResponseEntity<Chunk<FeedModelDto>> getFeed(@RequestParam String limit, @RequestParam String offset)
            throws IOException {
        Chunk<FeedModelDto> feedModelDtoChunk =
                feedService.findFeedChunkData(Integer.parseInt(limit), Long.parseLong(offset));
        return ResponseEntity.ok().body(feedModelDtoChunk);
    }

    @GetMapping("/file/{feedId}/{fileName}")
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
        FeedModelDto feedDto = feedService.saveTextFeed(text);
        messageTemplate.convertAndSend("/topic/feed", feedDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addons")
    public ResponseEntity<?> postFeedWithFiles(@RequestBody MultipartFile[] files,
                                               @RequestParam String text) throws IOException {
        FeedModelDto feedDto = feedService.saveFeedWithFiles(files, text);
        messageTemplate.convertAndSend("/topic/feed", feedDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addons-comp")
    public ResponseEntity<?> postFeedWithFilesPlusCompressed(@RequestBody MultipartFile[] files,
                                                             @RequestBody MultipartFile[] images,
                                                             @RequestParam String text) throws IOException {
        FeedModelDto feedDto = feedService.saveFeedWithFilesPlusCompressed(files, images, text);
        messageTemplate.convertAndSend("/topic/feed", feedDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeed(@PathVariable String feedId) {
        feedService.deleteFeed(feedId);
        commentService.deleteAllCommentsByFeed(Long.parseLong(feedId));
        messageTemplate.convertAndSend("/topic/delete-feed", feedId);
        return ResponseEntity.ok().build();
    }
}




















