import { Component, inject, OnInit } from '@angular/core';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { NavbarComponent } from './features/navbar/navbar.component';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { FooterComponent } from './shared/footer.component';
import { filter, map, Observable, startWith } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    CardModule,
    ButtonModule,
    NavbarComponent,
    FooterComponent,
  ],
  templateUrl: 'app.component.html',
  styles: ``,
})
export class AppComponent implements OnInit {
  // Local variables
  private hideNavRoutes = ['/login', '/register'];

  // State variables
  showNavbar$!: Observable<boolean>;

  // Injectables
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit() {
    // Hide navbar in certain pages
    this.showNavbar$ = this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => {
        return !this.hideNavRoutes.includes(event.urlAfterRedirects);
      }),
      startWith(!this.hideNavRoutes.includes(this.router.url)),
    );
    // Check whether user was logged in
    this.authService
      .initAuth()
      .pipe(filter(({ loading }) => !loading))
      .subscribe(({ data: user, error }) => {
        if (error) {
          console.error(error);
          console.log('Performing logout...');
          this.authService.logout();
          return;
        }
        if (!user) {
          console.log('Performing logout...');
          this.authService.logout();
          return;
        }
        // console.log('Valid user');
        // If token was valid, set user data
        this.authService.setUser(user);
      });
  }
}
