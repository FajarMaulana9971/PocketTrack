package PocketTrack.Serverapp.Services.Implementation.Base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

@Service
@Primary
public class BaseServicesImpl<E extends BaseEntity, T> implements BaseServices<E, T> {
    @Autowired
    private BaseRepositories<E, T> baseRepositories;

    @Override
    public E getById(T id) {
        return baseRepositories.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id : " + id + " is not found"));
    }

    @Override
    public ResponseEntity<ResponseData<E>> getByIdWithResponse(T id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseData<>(getById(id), "Id : " + id + " successfully retrived"));
    }

    @Override
    public ResponseEntity<ResponseData<E>> deleteWithResponse(T id) {
        E entity = getById(id);
        baseRepositories.delete(entity);
        return ResponseEntity.ok(new ResponseData<>(entity, entity + " successfully deleted"));
    }
}
