package luke.friendbook.storage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileData {

    private String fileName;
    private String url;
    private String type;
    private long size;
}
