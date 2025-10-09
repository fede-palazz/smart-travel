export interface QueryResult<TData = any> {
  data?: TData | null;
  loading: boolean;
  error?: string | null;
}
