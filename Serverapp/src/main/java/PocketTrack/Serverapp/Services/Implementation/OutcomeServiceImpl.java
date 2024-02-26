package PocketTrack.Serverapp.Services.Implementation;

import org.springframework.stereotype.Service;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.OutcomeService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OutcomeServiceImpl extends BaseServicesImpl<Outcome, String> implements OutcomeService {

}
