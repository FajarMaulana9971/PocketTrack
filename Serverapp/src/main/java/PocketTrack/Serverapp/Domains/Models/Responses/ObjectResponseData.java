package PocketTrack.Serverapp.Domains.Models.Responses;

import java.util.List;

import PocketTrack.Serverapp.Domains.Models.PageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ObjectResponseData<T> {
    private List<T> data;
    private PageData pageData;
}
