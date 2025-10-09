package com.certimetergroup.smart_travel.userapi.dto;

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
