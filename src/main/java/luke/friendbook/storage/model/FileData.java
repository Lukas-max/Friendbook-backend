package luke.friendbook.storage.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FileData {

    private String fileName;
    private String url;
    private String imageUrl;
    private String mimeType;
    private String type;
    private long size;

    public FileData(String fileName, String url, String mimeType, String type, long size) {
        this.fileName = fileName;
        this.url = url;
        this.mimeType = mimeType;
        this.type = type;
        this.size = size;
    }
}
