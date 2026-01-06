import { Pipe, PipeTransform } from '@angular/core';

export interface StatusBadge {
  text: string;
  class: string;
}

@Pipe({
  name: 'statusBadge',
  standalone: true
})
export class StatusBadgePipe implements PipeTransform {
  private readonly statusMap: Record<string, StatusBadge> = {
    // Loan statuses
    'APPLIED': { text: 'Applied', class: 'status-info' },
    'UNDER_REVIEW': { text: 'Under Review', class: 'status-warning' },
    'APPROVED': { text: 'Approved', class: 'status-success' },
    'DISBURSED': { text: 'Disbursed', class: 'status-success' },
    'REJECTED': { text: 'Rejected', class: 'status-danger' },
    'CLOSED': { text: 'Closed', class: 'status-secondary' },

    // EMI statuses
    'PENDING': { text: 'Pending', class: 'status-warning' },
    'PAID': { text: 'Paid', class: 'status-success' },
    'OVERDUE': { text: 'Overdue', class: 'status-danger' },
    'PARTIAL': { text: 'Partial', class: 'status-info' },

    // Employment statuses
    'SALARIED': { text: 'Salaried', class: 'status-primary' },
    'SELF_EMPLOYED': { text: 'Self Employed', class: 'status-info' },
    'BUSINESS': { text: 'Business', class: 'status-success' },
    'UNEMPLOYED': { text: 'Unemployed', class: 'status-secondary' }
  };

  transform(status: string | null | undefined): StatusBadge {
    if (!status) {
      return { text: 'Unknown', class: 'status-secondary' };
    }

    return this.statusMap[status] || { text: status, class: 'status-secondary' };
  }
}
