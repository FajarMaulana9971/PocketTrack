package PocketTrack.Serverapp.Services.Interfaces.Base;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;

public interface BaseServices<E extends BaseEntity, T> {

    E getById(T id);

    ResponseEntity<ResponseData<E>> getByIdWithResponse(T id);

    ResponseEntity<ResponseData<E>> deleteWithResponse(T id);

}
