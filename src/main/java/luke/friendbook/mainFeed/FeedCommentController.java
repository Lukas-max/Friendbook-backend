package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.mainFeed.services.IFeedCommentService;
import luke.friendbook.model.Chunk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed-comment")
public class FeedCommentController {

    private final IFeedCommentService feedCommentService;

    public FeedCommentController(IFeedCommentService feedCommentService) {
        this.feedCommentService = feedCommentService;
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<Chunk<FeedComment>> getFeedComments(@PathVariable String feedId,
                                                              @RequestParam String limit,
                                                              @RequestParam String offset) {
        Chunk<FeedComment> feedCommentChunk = feedCommentService.findFeedCommentChunk(
                Long.parseLong(feedId),
                Integer.parseInt(limit),
                Long.parseLong(offset));

        return ResponseEntity.ok(feedCommentChunk);
    }

    @PostMapping
    public ResponseEntity<?> saveFeedComment(@RequestBody FeedComment feedComment) {
        feedCommentService.saveComment(feedComment);
        return ResponseEntity.ok()
                .header("Accepted", "Saved feed comment")
                .build();
    }

    @DeleteMapping("/{feedId}")
    public ResponseEntity<?> deleteFeedComment(@PathVariable String feedId){
        return ResponseEntity.ok().build();
    }
}
