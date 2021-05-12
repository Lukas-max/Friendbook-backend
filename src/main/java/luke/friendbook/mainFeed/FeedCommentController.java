package luke.friendbook.mainFeed;

import luke.friendbook.mainFeed.model.FeedComment;
import luke.friendbook.mainFeed.model.FeedCommentDto;
import luke.friendbook.mainFeed.model.FeedModelDto;
import luke.friendbook.mainFeed.services.IFeedCommentService;
import luke.friendbook.model.Chunk;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/feed-comment")
public class FeedCommentController {

    private final IFeedCommentService feedCommentService;
    private final SimpMessageSendingOperations messageTemplate;
    private final ModelMapper modelMapper;

    public FeedCommentController(IFeedCommentService feedCommentService,
                                 SimpMessageSendingOperations messageTemplate,
                                 ModelMapper modelMapper) {
        this.feedCommentService = feedCommentService;
        this.messageTemplate = messageTemplate;
        this.modelMapper = modelMapper;
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
    public ResponseEntity<?> saveFeedComment(@Valid @RequestBody FeedCommentDto feedCommentDto) {
        FeedComment feedComment = modelMapper.map(feedCommentDto, FeedComment.class);
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


