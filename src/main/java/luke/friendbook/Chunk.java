package luke.friendbook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Chunk<T> {
    private int limit;
    private long offset;
    private List<T> content;
}
