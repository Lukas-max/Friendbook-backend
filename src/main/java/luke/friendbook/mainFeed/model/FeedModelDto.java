package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luke.friendbook.storage.model.FileData;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedModelDto {

    private Long id;
    private String text;
    private Boolean files;
    private Boolean images;
    private Timestamp feedTimestamp;
    private FileData[] fileData;
    private String username;
    private String userUUID;


    public FeedModelDto(Long id, String text, Boolean files, Boolean images, Timestamp feedTimestamp, String username, String userUUID) {
        this.id = id;
        this.text = text;
        this.files = files;
        this.images = images;
        this.feedTimestamp = feedTimestamp;
        this.username = username;
        this.userUUID = userUUID;
    }
}
