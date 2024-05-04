package PocketTrack.Serverapp.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Services.Interfaces.HistoryService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("histories")
public class HistoryController {
    private HistoryService historyService;

    @GetMapping("{historyId}")
    public ObjectResponseData<History> getAllHistories(@PathVariable String historyId,
            @RequestParam(required = false) String keywoard,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return historyService.getAllHistories(historyId, keywoard, page, size);
    };
}
