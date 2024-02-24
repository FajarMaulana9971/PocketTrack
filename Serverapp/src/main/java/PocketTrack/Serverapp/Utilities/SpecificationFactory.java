package PocketTrack.Serverapp.Utilities;

import java.util.Collections;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import PocketTrack.Serverapp.Domains.Enums.SearchOperation;

@Component
public class SpecificationFactory<T> {
    public Specification<T> isEqual(String key, Object arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.EQUALITY, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isLike(String key, Object arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.LIKE, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isContain(String key, Object arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.CONTAIN, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isGreaterThan(String key, Comparable arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.GREATER_THAN, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isLessThan(String key, Comparable arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.LESS_THAN, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isIn(String key, Object arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.IN_JOIN_CATEGORY, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isYearIn(String key, Object arg) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.IN_YEAR, Collections.singletonList(arg), null).build();
    }

    public Specification<T> isEqualJoin(String key, Object arg, List<String> join) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.JOIN, Collections.singletonList(arg), join).build();
    }

    public Specification<T> isEqualJoinTwoTables(String key, Object arg, List<String> join) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.JOIN_TWO_TABLES, Collections.singletonList(arg), join).build();
    }

    public Specification<T> isRoleIn(String key, Object arg, List<String> join) {
        GenericSpecificationsBuilder<T> builder = new GenericSpecificationsBuilder<>();
        return builder.with(key, SearchOperation.EQUALITY_LIST, Collections.singletonList(arg), join).build();
    }
}
