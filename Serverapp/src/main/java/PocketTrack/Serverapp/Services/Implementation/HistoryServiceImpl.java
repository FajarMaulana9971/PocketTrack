package PocketTrack.Serverapp.Services.Implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Repositories.HistoryRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.HistoryService;
import PocketTrack.Serverapp.Utilities.GenericSpecificationsBuilder;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
import PocketTrack.Serverapp.Utilities.SpecificationFactory;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HistoryServiceImpl extends BaseServicesImpl<History, String> implements HistoryService {
    private SpecificationFactory<History> specificationFactory;
    private HistoryRepository historyRepository;
    private PaginationUtil paginationUtil;

    /**
     * This method is used to get all histories for public page
     * 
     * @param keyword - Keyword for search
     * @param page    - Page number
     * @param size    - Size per page
     * @return List of budget with pagination
     */
    @Override
    public ObjectResponseData<History> getAllHistories(String historyId, String keywoard, int page, int size) {
        if (page > 0)
            page = page - 1;

        GenericSpecificationsBuilder<History> historyBuilder = new GenericSpecificationsBuilder<>();
        if (keywoard != null) {
            historyBuilder.with(specificationFactory.isContain("date", keywoard)
                    .or(specificationFactory.isContain("notes", keywoard)));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<History> historyPage = historyRepository.findAll(historyBuilder.build(), pageable);
            PageData pagination = paginationUtil.setPageData(page, size, (int) historyPage.getTotalElements());
            return new ObjectResponseData<>(historyPage.getContent(), pagination);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }
}
