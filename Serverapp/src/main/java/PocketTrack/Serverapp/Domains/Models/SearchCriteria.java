package PocketTrack.Serverapp.Domains.Models;

import java.util.List;

import PocketTrack.Serverapp.Domains.Enums.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchCriteria {
    private String key;
    private SearchOperation searchOperation;
    private Boolean isOrOperation;
    private List<Object> arguments;
    private List<String> join;
}
