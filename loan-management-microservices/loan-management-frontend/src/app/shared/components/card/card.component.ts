import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card [class.clickable]="clickable" [class.elevated]="elevated">
      <mat-card-header *ngIf="title || subtitle">
        <mat-card-title *ngIf="title">{{ title }}</mat-card-title>
        <mat-card-subtitle *ngIf="subtitle">{{ subtitle }}</mat-card-subtitle>
      </mat-card-header>

      <mat-card-content>
        <ng-content></ng-content>
      </mat-card-content>

      <mat-card-actions *ngIf="hasActions" align="end">
        <ng-content select="[actions]"></ng-content>
      </mat-card-actions>
    </mat-card>
  `,
  styles: [`
    mat-card {
      margin-bottom: 16px;

      &.clickable {
        cursor: pointer;
        transition: box-shadow 0.3s ease;

        &:hover {
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }
      }

      &.elevated {
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
    }
  `]
})
export class CardComponent {
  @Input() title?: string;
  @Input() subtitle?: string;
  @Input() clickable: boolean = false;
  @Input() elevated: boolean = true;
  @Input() hasActions: boolean = false;
}
