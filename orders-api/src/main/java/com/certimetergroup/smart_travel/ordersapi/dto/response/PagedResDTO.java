package com.certimetergroup.smart_travel.ordersapi.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PagedResDTO<T>(
    int totalPages,
    long totalElements,
    int currentPage,
    int elementsInPage,
    List<T> content
) {

}
