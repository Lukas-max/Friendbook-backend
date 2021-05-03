package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.mainFeed.services.IFeedCommentService;
import luke.friendbook.model.Chunk;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed-comment")
public class FeedCommentController {

    private final IFeedCommentService feedCommentService;
    private final SimpMessageSendingOperations messageTemplate;

    public FeedCommentController(IFeedCommentService feedCommentService,
                                 SimpMessageSendingOperations messageTemplate) {
        this.feedCommentService = feedCommentService;
        this.messageTemplate = messageTemplate;
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<Chunk<FeedComment>> getFeedComments(@PathVariable String feedId,
                                                              @RequestParam String limit,
                                                              @RequestParam String offset) {
        Chunk<FeedComment> feedCommentChunk = feedCommentService.findCommentChunk(
                Long.parseLong(feedId),
                Integer.parseInt(limit),
                Long.parseLong(offset));

        return ResponseEntity.ok(feedCommentChunk);
    }

    @PostMapping
    public ResponseEntity<?> saveFeedComment(@RequestBody FeedComment feedComment) {
        FeedComment savedComment = feedCommentService.saveComment(feedComment);
        messageTemplate.convertAndSend("/topic/comment", savedComment);
        return ResponseEntity.ok()
                .header("Accepted", "Saved feed comment")
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteFeedComment(@PathVariable String commentId){
        feedCommentService.deleteCommentById(Long.parseLong(commentId));
        return ResponseEntity.ok().build();
    }
}


