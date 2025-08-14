import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient, private router: Router) {};

  login(credentials: { useName: string, password: string }): Observable<{ data: { token: string } }> {
    return this.http.post<{ data: { token: string } }>(`/api/auth/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem(this.TOKEN_KEY, response.data.token);
      })
      );
    };

    logout(): void {
      localStorage.removeItem(this.TOKEN_KEY);
      this.router.navigate(['/login']);
    };

    getToken(): string | null {
      return localStorage.getItem(this.TOKEN_KEY);
    };

    isAuthentication(): boolean {
      return !!this.getToken();
    }

};

  
