package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "feed_comment")
public class FeedComment implements Serializable {
    private static final long serialVersionUID = 8287839052966474566L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "feed_id")
    private Long feedId;

    @Column(name = "username")
    private String username;

    @Column(name = "user_uuid")
    private String userUUID;

    @Column(name = "content")
    private String content;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "last_updated")
    private Timestamp lastUpdated;
}
