package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import luke.friendbook.account.model.User;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "feed")
public class FeedModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "files")
    private Boolean files;

    @Column(name = "images")
    private Boolean images;

    @Column(name = "timestamp", nullable = false)
    private Timestamp feedTimestamp;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
