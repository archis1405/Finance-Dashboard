package com.example.finance_dashboard_backend.specification;

import com.example.finance_dashboard_backend.model.BusinessUnit;
import com.example.finance_dashboard_backend.model.FinancialRecord;
import com.example.finance_dashboard_backend.model.RecordType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class FinancialRecordSpecifications {
    private FinancialRecordSpecifications() {
    }

    public static Specification<FinancialRecord> withFilters(RecordType type, List<String> categories, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount, String keyword, BusinessUnit businessUnit) {
        return notDeleted()
                .and(hasType(type)).and(hasCategories(categories))
                .and(onOrAfter(startDate)).and(onOrBefore(endDate))
                .and(amountAtLeast(minAmount)).and(amountAtMost(maxAmount))
                .and(notesKeyword(keyword)).and(inBusinessUnit(businessUnit));
    }

    public static Specification<FinancialRecord> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private static Specification<FinancialRecord> hasType(RecordType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    private static Specification<FinancialRecord> hasCategories(List<String> categories) {
        return (root, query, cb) -> {
            if (categories == null || categories.isEmpty()) {
                return cb.conjunction();
            }

            List<String> normalized = categories.stream().map(value -> value.trim().toLowerCase()).toList();
            return cb.lower(root.get("category")).in(normalized);
        };
    }

    private static Specification<FinancialRecord> onOrAfter(LocalDate startDate) {
        return (root, query, cb) -> startDate == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("entryDate"), startDate);
    }

    private static Specification<FinancialRecord> onOrBefore(LocalDate endDate) {
        return (root, query, cb) -> endDate == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("entryDate"), endDate);
    }

    private static Specification<FinancialRecord> amountAtLeast(BigDecimal minAmount) {
        return (root, query, cb) -> minAmount == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("amount"), minAmount);
    }

    private static Specification<FinancialRecord> amountAtMost(BigDecimal maxAmount) {
        return (root, query, cb) -> maxAmount == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("amount"), maxAmount);
    }

    private static Specification<FinancialRecord> notesKeyword(String keyword) {
        return (root, query, cb) -> StringUtils.isBlank(keyword)
                ? cb.conjunction()
                : cb.like(cb.lower(cb.coalesce(root.get("notes"), "")), "%" + keyword.trim().toLowerCase() + "%");
    }

    private static Specification<FinancialRecord> inBusinessUnit(BusinessUnit businessUnit) {
        return (root, query, cb) -> businessUnit == null ? cb.conjunction() : cb.equal(root.get("businessUnit"), businessUnit);
    }
}
