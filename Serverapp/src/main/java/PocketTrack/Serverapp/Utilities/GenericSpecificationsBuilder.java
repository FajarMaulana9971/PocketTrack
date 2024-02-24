package PocketTrack.Serverapp.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import PocketTrack.Serverapp.Domains.Enums.SearchOperation;
import PocketTrack.Serverapp.Domains.Models.SearchCriteria;

public class GenericSpecificationsBuilder<T> {

    private final List<SearchCriteria> params;
    private final List<Specification<T>> specifications;

    public GenericSpecificationsBuilder() {
        this.params = new ArrayList<>();
        this.specifications = new ArrayList<>();
    }

    public final GenericSpecificationsBuilder<T> with(final String key, final SearchOperation searchOperation,
            final List<Object> arguments,
            List<String> join) {
        return with(key, searchOperation, false, arguments, join);
    }

    public final GenericSpecificationsBuilder<T> with(final String key, final SearchOperation searchOperation,
            final boolean isOrOperation, final List<Object> arguments,
            List<String> join) {
        params.add(new SearchCriteria(key, searchOperation, isOrOperation, arguments, join));
        return this;
    }

    public final GenericSpecificationsBuilder<T> with(Specification<T> specification) {
        specifications.add(specification);
        return this;
    }

    public Specification<T> build() {
        Specification<T> result = null;
        if (!params.isEmpty()) {
            result = new GenericSpecifications<>(params.get(0));
            for (int index = 1; index < params.size(); ++index) {
                SearchCriteria searchCriteria = params.get(index);
                result = searchCriteria.getIsOrOperation()
                        ? Specification.where(result).or(new GenericSpecifications<>(searchCriteria))
                        : Specification.where(result).and(new GenericSpecifications<>(searchCriteria));
            }
        }
        if (!specifications.isEmpty()) {
            int index = 0;
            if (Objects.isNull(result)) {
                result = specifications.get(index++);
            }
            for (; index < specifications.size(); ++index) {
                result = Specification.where(result).and(specifications.get(index));
            }
        }
        return result;
    }
}
