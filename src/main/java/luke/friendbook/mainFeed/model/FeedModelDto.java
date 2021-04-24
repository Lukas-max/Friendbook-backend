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

    private Long feedId;
    private String text;
    private Boolean files;
    private Boolean images;
    private Timestamp feedTimestamp;
    private FileData[] fileData;
    private String username;
    private String userUUID;


    public FeedModelDto(Long feedId, String text, Boolean files, Boolean images, Timestamp feedTimestamp, String username, String userUUID) {
        this.feedId = feedId;
        this.text = text;
        this.files = files;
        this.images = images;
        this.feedTimestamp = feedTimestamp;
        this.username = username;
        this.userUUID = userUUID;
    }

    public FeedModelDto(FeedModel feed) {
        this.feedId = feed.getId();
        this.text = feed.getText();
        this.files = feed.getFiles();
        this.images = feed.getImages();
        this.feedTimestamp = feed.getFeedTimestamp();
        this.username = feed.getUser().getUsername();
        this.userUUID = feed.getUser().getUserUUID();
    }

    public FeedModelDto(FeedModel feed, FileData[] fileData) {
        this.feedId = feed.getId();
        this.text = feed.getText();
        this.files = feed.getFiles();
        this.images = feed.getImages();
        this.feedTimestamp = feed.getFeedTimestamp();
        this.fileData = fileData;
        this.username = feed.getUser().getUsername();
        this.userUUID = feed.getUser().getUserUUID();
    }
}
