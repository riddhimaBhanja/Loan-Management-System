import { Injectable, signal } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();
  public isLoading = signal<boolean>(false);

  private activeRequests = 0;

  show(): void {
    this.activeRequests++;
    if (this.activeRequests > 0) {
      this.loadingSubject.next(true);
      this.isLoading.set(true);
    }
  }

  hide(): void {
    this.activeRequests--;
    if (this.activeRequests <= 0) {
      this.activeRequests = 0;
      this.loadingSubject.next(false);
      this.isLoading.set(false);
    }
  }

  reset(): void {
    this.activeRequests = 0;
    this.loadingSubject.next(false);
    this.isLoading.set(false);
  }
}
