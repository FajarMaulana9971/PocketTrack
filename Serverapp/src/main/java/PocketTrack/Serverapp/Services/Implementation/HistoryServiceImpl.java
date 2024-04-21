package PocketTrack.Serverapp.Services.Implementation;

import org.springframework.stereotype.Service;

import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.HistoryService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HistoryServiceImpl extends BaseServicesImpl<History, String> implements HistoryService {

}
