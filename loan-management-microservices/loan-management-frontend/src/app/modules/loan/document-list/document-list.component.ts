import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../../../core/services/document.service';
import { Document } from '../../../core/models/document.model';
import { DocumentTypeLabels } from '../../../core/models/document-type.enum';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './document-list.component.html',
  styleUrls: ['./document-list.component.scss']
})
export class DocumentListComponent implements OnInit {
  @Input() loanId!: number;

  documents: Document[] = [];
  isLoading = false;
  documentTypeLabels = DocumentTypeLabels;
  canDelete = false;

  constructor(
    private documentService: DocumentService,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.checkDeletePermission();
    this.loadDocuments();
  }

  checkDeletePermission(): void {
    const currentUser = this.authService.getCurrentUser();
    this.canDelete = currentUser?.roles?.includes('LOAN_OFFICER') || currentUser?.roles?.includes('ADMIN') || false;
  }

  loadDocuments(): void {
    if (!this.loanId) return;

    this.isLoading = true;
    this.documentService.getLoanDocuments(this.loanId).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success && response.data) {
          this.documents = response.data;
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.notificationService.error('Failed to load documents');
      }
    });
  }

  onDocumentAdded(document: Document): void {
    this.documents.unshift(document);
  }

  downloadDocument(doc: Document): void {
    this.documentService.downloadDocument(doc.id, doc.originalFileName)
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = window.document.createElement('a');
          link.href = url;
          link.download = doc.originalFileName;
          link.click();
          window.URL.revokeObjectURL(url);
          this.notificationService.success('Document downloaded successfully');
        },
        error: (error) => {
          this.notificationService.error('Failed to download document');
        }
      });
  }

  deleteDocument(document: Document): void {
    if (!confirm(`Are you sure you want to delete ${document.originalFileName}?`)) {
      return;
    }

    this.documentService.deleteDocument(document.id).subscribe({
      next: (response) => {
        if (response.success) {
          this.documents = this.documents.filter(d => d.id !== document.id);
          this.notificationService.success('Document deleted successfully');
        }
      },
      error: (error) => {
        this.notificationService.error(error.error?.message || 'Failed to delete document');
      }
    });
  }

  formatFileSize(bytes: number): string {
    return this.documentService.formatFileSize(bytes);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getFileIcon(contentType: string): string {
    if (contentType.includes('pdf')) return 'fas fa-file-pdf';
    if (contentType.includes('image')) return 'fas fa-file-image';
    return 'fas fa-file';
  }
}
