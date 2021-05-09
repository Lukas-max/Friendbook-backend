package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import luke.friendbook.account.model.User;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "feed")
public class FeedModel implements Serializable {
    private static final long serialVersionUID = -8214675052760203546L;

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
