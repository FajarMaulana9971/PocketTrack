package PocketTrack.Serverapp.Services.Implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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
    public ObjectResponseData<History> getAllHistories(String keywoard, int page, int size) {
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

    /**
     * This method is used to get all histories based on type for public page
     * 
     * @param budgetId - budget id for param
     * @param keyword  - Keyword for search
     * @param page     - Page number
     * @param size     - Size per page
     * @return List of budget with pagination
     */
    public ObjectResponseData<History> getAllHistoriesBasedOnType(String budgetId, String keyword, int page, int size) {
        if (page > 0)
            page = page - 1;

        GenericSpecificationsBuilder<History> historyBuilder = new GenericSpecificationsBuilder<>();
        historyBuilder.with(specificationFactory.isEqual("budgetId", budgetId));

        if ("income".equals(keyword)) {
            historyBuilder.with(specificationFactory.isEqual("type", "income"));
        } else if ("outcome".equals(keyword)) {
            historyBuilder.with(specificationFactory.isEqual("type", "outcome"));
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

    /**
     * This method is used to delete all history when history date has 2 month
     * 
     * @return History has been deleted
     */
    @Override
    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteHistory() {
        LocalDate twoMonthsAgo = LocalDate.now().minusMonths(2);
        Specification<History> oldHistoriesSpec = specificationFactory.isLessThan("date", twoMonthsAgo);
        List<History> oldHistories = historyRepository.findAll(oldHistoriesSpec);
        historyRepository.deleteAll(oldHistories);
    }
}
