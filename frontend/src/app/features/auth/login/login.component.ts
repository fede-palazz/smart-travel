import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { AuthService } from '../../../services/auth.service';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { BrandComponent } from '../../../shared/brand.component';
import { CheckoutService } from '../../checkout/services/checkout.service';
import { tap } from 'rxjs';

@Component({
  selector: 'smt-login',
  standalone: true,
  imports: [
    CommonModule,
    CardModule,
    ReactiveFormsModule,
    DividerModule,
    ToastModule,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    BrandComponent,
    RouterModule,
  ],
  templateUrl: './login.component.html',
  styles: ``,
})
export class LoginComponent implements OnInit {
  error = '';
  loginForm!: FormGroup;
  loading = false;
  returnUrl!: string;
  backUrl!: string;

  // Injectables
  private messageService = inject(MessageService);
  private checkoutService = inject(CheckoutService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  get f() {
    return this.loginForm.controls;
  }

  ngOnInit() {
    // Initialize form group
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
    });

    // Set return URL
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/home';
    this.backUrl = this.route.snapshot.queryParams['backUrl'] || '/home';
  }

  handleBack() {
    this.checkoutService.clearOrder(); // Clear pending order if not logged in
    this.router.navigateByUrl(this.backUrl);
  }

  handleSubmit() {
    if (this.loginForm.invalid) {
      return;
    }
    this.authService
      .login(this.f['email'].value, this.f['password'].value)
      .pipe(
        tap(({ loading }) => {
          if (loading) this.loading = loading;
        }),
      )
      .subscribe(({ data, error }) => {
        if (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error during login',
            detail: error,
          });
          this.loading = false;
          return;
        }
        if (data) {
          this.messageService.add({
            severity: 'success',
            summary: `Welcome ${data.user.firstname}`,
            detail: 'You are being redirected...',
          });
          setTimeout(() => {
            this.router.navigateByUrl(this.returnUrl);
          }, 1000);
        }
      });
  }
}
