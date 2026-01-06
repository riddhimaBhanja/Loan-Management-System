import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../../../core/services/document.service';
import { DocumentType, DocumentTypeLabels } from '../../../core/models/document-type.enum';
import { Document } from '../../../core/models/document.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-document-upload',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './document-upload.component.html',
  styleUrls: ['./document-upload.component.scss']
})
export class DocumentUploadComponent {
  @Input() loanId!: number;
  @Output() documentUploaded = new EventEmitter<Document>();

  selectedFile: File | null = null;
  selectedDocumentType: DocumentType = DocumentType.ID_PROOF;
  isUploading = false;
  uploadProgress = 0;

  documentTypes = Object.values(DocumentType);
  documentTypeLabels = DocumentTypeLabels;

  constructor(
    private documentService: DocumentService,
    private notificationService: NotificationService
  ) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const validation = this.documentService.validateFile(file);
      if (validation.valid) {
        this.selectedFile = file;
      } else {
        this.notificationService.error(validation.error || 'Invalid file');
        event.target.value = '';
      }
    }
  }

  onUpload(): void {
    if (!this.selectedFile || !this.loanId) {
      this.notificationService.error('Please select a file');
      return;
    }

    this.isUploading = true;
    this.uploadProgress = 0;

    this.documentService.uploadDocument(this.loanId, this.selectedDocumentType, this.selectedFile)
      .subscribe({
        next: (response) => {
          this.isUploading = false;
          this.uploadProgress = 100;
          if (response.success && response.data) {
            this.notificationService.success('Document uploaded successfully');
            this.documentUploaded.emit(response.data);
            this.resetForm();
          }
        },
        error: (error) => {
          this.isUploading = false;
          this.uploadProgress = 0;
          this.notificationService.error(error.error?.message || 'Failed to upload document');
        }
      });
  }

  resetForm(): void {
    this.selectedFile = null;
    this.selectedDocumentType = DocumentType.ID_PROOF;
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  getFileSize(): string {
    if (!this.selectedFile) return '';
    return this.documentService.formatFileSize(this.selectedFile.size);
  }
}
