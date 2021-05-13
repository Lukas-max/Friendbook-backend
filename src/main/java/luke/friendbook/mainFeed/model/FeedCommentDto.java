package luke.friendbook.mainFeed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedCommentDto {

    private Long id;

    @NotNull(message = "Brak numeru id postu")
    private Long feedId;

    @NotBlank(message = "Brak nazwy użytkownika")
    private String username;

    @NotBlank(message = "Brak numeru uuid użytkownika generującego komentarz")
    private String userUUID;

    @NotBlank(message = "Komentarz nie może być pusty")
    @Size(min = 1, max = 255, message = "Zła długość komentarza")
    private String content;

    @NotNull(message = "Brak daty utworzenia komentarza")
    private Timestamp timestamp;

    private Timestamp lastUpdated;
}
