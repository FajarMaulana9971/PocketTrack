package PocketTrack.Serverapp.Domains.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageData {
    private Integer size, pageSize, pageTotal, current;

    public PageData(Integer jmlPerPage, Integer count) {
        this.size = count;
        this.pageSize = jmlPerPage;
    }
}
