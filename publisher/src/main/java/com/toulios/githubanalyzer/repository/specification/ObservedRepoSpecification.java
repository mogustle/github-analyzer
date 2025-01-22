package com.toulios.githubanalyzer.repository.specification;

import com.toulios.githubanalyzer.dto.request.ObservedRepoFilter;
import com.toulios.githubanalyzer.model.ObservedRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Specification class for ObservedRepo
 */
public class ObservedRepoSpecification {

    /**
     * Creates a specification for ObservedRepo based on the provided filter
     *
     * @param filter the filter criteria
     * @return the specification
     */
    public static Specification<ObservedRepo> withFilter(ObservedRepoFilter filter) {
        return Specification
                .where(hasOwner(filter.getOwner()))
                .and(hasName(filter.getName()))
                .and(hasStatus(filter.getStatus().toString()))
                .and(hasLicence(filter.getLicence()));
    }

    /**
     * Specification for filtering by owner
     *
     * @param owner the owner to filter by
     * @return the specification
     */
    private static Specification<ObservedRepo> hasOwner(String owner) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(owner)) {
                return null;
            }
            return cb.like(cb.lower(root.get("owner")), "%" + owner.toLowerCase() + "%");
        };
    }

    /**
     * Specification for filtering by name
     *
     * @param name the name to filter by
     * @return the specification
     */
    private static Specification<ObservedRepo> hasName(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Specification for filtering by status
     *
     * @param status the status to filter by
     * @return the specification
     */
    private static Specification<ObservedRepo> hasStatus(String status) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(status)) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Specification for filtering by licence
     *
     * @param licence the licence to filter by
     * @return the specification
     */
    private static Specification<ObservedRepo> hasLicence(String licence) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(licence)) {
                return null;
            }
            return cb.like(cb.lower(root.get("licence")), "%" + licence.toLowerCase() + "%");
        };
    }
} 