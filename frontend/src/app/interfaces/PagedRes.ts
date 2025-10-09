export interface PagedRes<T> {
  totalPages: number;
  totalElements: number;
  currentPage: number;
  elementsInPage: number;
  content: T[];
}
