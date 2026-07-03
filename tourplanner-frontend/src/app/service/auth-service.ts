import {Injectable} from "@angular/core";
import {HttpClient} from '@angular/common/http';

export interface AuthResponse {
  token: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = 'http://localhost:8080/api/auth';
  private usersApiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

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
}
