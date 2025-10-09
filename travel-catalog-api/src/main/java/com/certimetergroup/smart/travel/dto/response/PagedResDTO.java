package com.certimetergroup.smart.travel.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PagedResDTO<T> {

  int totalPages;
  long totalElements;
  int currentPage;
  int elementsInPage;
  List<T> content;
}
