import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';

  constructor() {}

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  setAccessToken(accessToken: string) {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  setRefreshToken(refreshToken: string) {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  clearTokens() {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  hasAccessToken(): boolean {
    return !!localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }
}
