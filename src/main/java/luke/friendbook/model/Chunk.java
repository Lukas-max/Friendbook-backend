package luke.friendbook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Chunk<T> {
    private int limit;
    private long offset;
    private List<T> content;
}
