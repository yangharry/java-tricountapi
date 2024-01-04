package tricountapi.javatricountapi.model;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement {
    private Long id;
    private String name;

    @Builder.Default
    private List<Member> participants = Collections.emptyList();

}
