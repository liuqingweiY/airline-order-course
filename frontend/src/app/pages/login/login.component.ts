import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMessageService } from 'ng-zorro-antd/message';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NzFormModule,
    NzInputModule,
    NzButtonModule,
    NzIconModule
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  
  private readonly TOKEN_KEY = 'auth_token';
  loginForm: FormGroup;
  isLoading = false;
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private message: NzMessageService
  ) {
    this.loginForm = this.fb.group({
      userName: ['admin', [Validators.required]],
      password: ['password', [Validators.required]]
    });
  };
  submitForm(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.authService.login(this.loginForm.value).pipe(
        finalize(() => this.isLoading)
      ).subscribe({
        next: () => {
          this.message.success('登陆成功!');
          this.router.navigate(['/orders']);
        },
        error: (error) => {
          this.message.error('登陆失败，请检查用户名密码');
          console.error(error);
        }
      });

    } else {
      Object.values(this.loginForm.controls).forEach(control => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
    }
  };
}
