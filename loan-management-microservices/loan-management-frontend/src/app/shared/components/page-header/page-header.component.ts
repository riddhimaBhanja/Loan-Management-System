import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, RouterModule],
  template: `
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <button
            *ngIf="showBackButton"
            mat-icon-button
            [routerLink]="backRoute || null"
            (click)="!backRoute && onBack()">
            <mat-icon>arrow_back</mat-icon>
          </button>

          <div>
            <h1>{{ title }}</h1>
            <p *ngIf="subtitle" class="subtitle">{{ subtitle }}</p>
          </div>
        </div>

        <div class="actions-section">
          <ng-content select="[actions]"></ng-content>
        </div>
      </div>

      <div *ngIf="hasTabs" class="tabs-section">
        <ng-content select="[tabs]"></ng-content>
      </div>
    </div>
  `,
  styles: [`
    .page-header {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(15px);
      border-bottom: 2px solid rgba(102, 126, 234, 0.1);
      margin-bottom: 32px;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      position: relative;
      overflow: hidden;
    }

    .page-header::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 24px 32px;
    }

    .title-section {
      display: flex;
      align-items: center;
      gap: 16px;

      button {
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
        transition: all 0.3s ease;

        &:hover {
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
          transform: translateX(-4px);
        }

        mat-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }

      h1 {
        margin: 0;
        font-size: 28px;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        letter-spacing: -0.5px;
      }

      .subtitle {
        margin: 6px 0 0;
        color: rgba(0, 0, 0, 0.65);
        font-size: 15px;
        font-weight: 500;
      }
    }

    .actions-section {
      display: flex;
      gap: 16px;
    }

    .tabs-section {
      padding: 0 32px 16px;
    }

    @media (max-width: 768px) {
      .header-content {
        padding: 20px 16px;
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
      }

      .title-section {
        h1 {
          font-size: 22px;
        }

        .subtitle {
          font-size: 14px;
        }
      }

      .actions-section {
        width: 100%;
      }

      .tabs-section {
        padding: 0 16px 16px;
      }
    }
  `]
})
export class PageHeaderComponent {
  @Input() title: string = '';
  @Input() subtitle?: string;
  @Input() showBackButton: boolean = false;
  @Input() backRoute?: string;
  @Input() hasTabs: boolean = false;
  @Output() back = new EventEmitter<void>();

  onBack(): void {
    this.back.emit();
  }
}
