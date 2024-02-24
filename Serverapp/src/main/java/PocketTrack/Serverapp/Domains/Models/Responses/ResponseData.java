package PocketTrack.Serverapp.Domains.Models.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseData<T> {
    private T data;
    private String message;
}
