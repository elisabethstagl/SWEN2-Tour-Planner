import {inject, Injectable} from "@angular/core";
import {HttpClient} from '@angular/common/http';
import {Observable} from "rxjs";

export interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = 'http://localhost:8080/api/auth';
  private tokenValidationUrl = 'http://localhost:8080/api/token';
  private usersApiUrl = 'http://localhost:8080/api/users';

  private readonly http = inject(HttpClient);

  login(username: string, password: string) {
    return this.http.post<AuthResponse>(`${this.authApiUrl}/login`, {
      username,
      password
    });
  }

  register(username: string, email: string, password: string) {
    return this.http.post(`${this.usersApiUrl}`, {
      username,
      email,
      password
    });
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  validateToken(): Observable<void> {
    return this.http.get<void>(`${this.tokenValidationUrl}/validate`);
  }
}
