package PocketTrack.Serverapp.Utilities;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import PocketTrack.Serverapp.Domains.Models.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class GenericSpecifications<T> implements Specification<T> {

    private final SearchCriteria searchCriteria;

    public GenericSpecifications(final SearchCriteria searchCriteria) {
        super();
        this.searchCriteria = searchCriteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Object> arguments = searchCriteria.getArguments();
        Object arg = arguments.get(0);

        switch (searchCriteria.getSearchOperation()) {
            case EQUALITY:
                return criteriaBuilder.equal(root.get(searchCriteria.getKey()), arg);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(searchCriteria.getKey()), (Comparable) arg);
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(searchCriteria.getKey()), (Comparable) arg);
            case CONTAIN:
                return criteriaBuilder.like(root.get(searchCriteria.getKey()), "%" + arg + "%");
            case IN_YEAR:
                return criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class,
                        root.get(searchCriteria.getKey())), arg);
            case JOIN:
                return criteriaBuilder.equal(root.join(searchCriteria.getJoin().get(0)).get(searchCriteria.getKey()),
                        arg);
            case JOIN_TWO_TABLES:
                return criteriaBuilder.like(root.join(searchCriteria.getJoin().get(0))
                        .join(searchCriteria.getJoin().get(1)).get(searchCriteria.getKey()), "%" + arg + "%");
            case EQUALITY_LIST:
                return criteriaBuilder.and(root.join(searchCriteria.getJoin().get(0))
                        .join(searchCriteria.getJoin().get(1)).join(searchCriteria.getJoin().get(2))
                        .get(searchCriteria.getKey()).in(arguments));
        }
        return null;
    }

}
