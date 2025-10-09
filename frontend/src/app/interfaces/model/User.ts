export interface User {
  id: string;
  email: string;
  role: UserRole;
  firstname: string;
  lastname: string;
  fullname: string;
}

export interface UserReq {
  email: string;
  password: string;
  role: string;
  firstname: string;
  lastname: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  AGENT = 'AGENT',
  CUSTOMER = 'CUSTOMER',
}
