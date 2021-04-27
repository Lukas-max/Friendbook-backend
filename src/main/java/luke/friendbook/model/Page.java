package luke.friendbook.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder 
public class Page<T> {
    private long totalItems;
    private int totalPages;
    private long items;
    private int currentPage;
    private List<T> content;
}
