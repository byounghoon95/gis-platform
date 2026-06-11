package com.gisplatform.backend.location;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class LocationSpecifications {

    private LocationSpecifications() {
    }

    public static Specification<Location> businessTypeEquals(String businessType) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(businessType)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("businessType")),
                    businessType.trim().toLowerCase()
            );
        };
    }

    public static Specification<Location> keywordContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("businessType")), pattern)
            );
        };
    }
}
