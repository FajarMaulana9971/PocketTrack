package PocketTrack.Serverapp.Utilities;

import org.springframework.stereotype.Component;

import PocketTrack.Serverapp.Domains.Models.PageData;

@Component
public class PaginationUtil {
    public PageData setPageData(Integer page, Integer size, Integer totalElements) {
        PageData pageData = new PageData(size, totalElements);
        try {
            pageData.setCurrent(page + 1);
            pageData.setPageTotal(totalElements / size);
            if (totalElements % size > 0) {
                pageData.setPageTotal(totalElements / size + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pageData.setCurrent(1);
            pageData.setPageTotal(1);
        }
        return pageData;

    }
}
