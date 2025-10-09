import { User } from '../model/User';

export interface LoginRes {
  accessToken: string;
  refreshToken: string;
  user: User;
}
