import { Directive, Input, TemplateRef, ViewContainerRef, OnInit, OnDestroy, inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';
import { Subject, takeUntil } from 'rxjs';

@Directive({
  selector: '[hasAnyRole]',
  standalone: true
})
export class HasAnyRoleDirective implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private templateRef = inject(TemplateRef<any>);
  private viewContainer = inject(ViewContainerRef);
  private destroy$ = new Subject<void>();

  private currentRoles: string[] = [];

  @Input() set hasAnyRole(roles: string[]) {
    this.currentRoles = roles;
    this.updateView();
  }

  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.updateView();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private updateView(): void {
    this.viewContainer.clear();
    if (this.authService.hasAnyRole(this.currentRoles)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
