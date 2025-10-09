import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DividerModule } from 'primeng/divider';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { BrandComponent } from '../../../shared/brand.component';
import { Router, RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'smt-register',
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
  templateUrl: './register.component.html',
  styles: ``,
})
export class RegisterComponent {
  error = '';
  signupForm!: FormGroup;
  loading = false;
  returnUrl!: string;

  // Injectables
  private messageService = inject(MessageService);
  private authService = inject(AuthService);
  private router = inject(Router);

  get f() {
    return this.signupForm.controls;
  }

  ngOnInit() {
    this.signupForm = new FormGroup({
      firstname: new FormControl('', [Validators.required]),
      lastname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(25),
      ]),
    });
  }

  handleBack() {
    if (this.returnUrl) this.router.navigate([this.returnUrl]);
    else this.router.navigate(['/home']);
  }

  handleSubmit() {
    if (this.signupForm.invalid) {
      return;
    }
    this.loading = true;
    console.log(this.signupForm.getRawValue());
  }
}
