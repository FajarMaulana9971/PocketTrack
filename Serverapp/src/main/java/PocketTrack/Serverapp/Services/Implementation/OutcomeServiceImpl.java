package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.OutcomeRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.OutcomeService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OutcomeServiceImpl extends BaseServicesImpl<Outcome, String> implements OutcomeService {
    private OutcomeRepository outcomeRepository;

    /**
     * This method is used to get outcome by title
     * 
     * @param title - title of outcome
     * @return outcome by title
     */
    @Override
    public Outcome getByTitle(String title) {
        return outcomeRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "title : " + title + NOT_FOUND));
    }

    /**
     * This method is used to get outcome by title with response
     * 
     * @param title - title of outcome
     * @return outcome by title with response
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> getByTitleWithResponse(String title) {
        return ResponseEntity.ok(new ResponseData<>(getByTitle(title), "Outcome" + SUCCESSFULLY_RETRIEVED));
    }
}
