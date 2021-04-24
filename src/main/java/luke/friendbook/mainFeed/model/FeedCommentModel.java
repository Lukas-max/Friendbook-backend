package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "feed_comment")
public class FeedCommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "feed_id", nullable = false)
    private Long feedId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "userUUID", nullable = false)
    private String userUUID;
}
