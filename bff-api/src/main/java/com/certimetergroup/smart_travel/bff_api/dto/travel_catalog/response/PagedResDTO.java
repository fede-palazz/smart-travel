package com.certimetergroup.smart_travel.bff_api.dto.travel_catalog.response;

import java.util.List;

public record PagedResDTO<T>(
    int totalPages,
    long totalElements,
    int currentPage,
    int elementsInPage,
    List<T> content) {

}
