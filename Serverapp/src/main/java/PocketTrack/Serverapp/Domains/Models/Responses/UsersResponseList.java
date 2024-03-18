package PocketTrack.Serverapp.Domains.Models.Responses;

import java.util.List;

import lombok.Data;

@Data
public class UsersResponseList {
    private List<UserResponse> userResponsesList;
}
