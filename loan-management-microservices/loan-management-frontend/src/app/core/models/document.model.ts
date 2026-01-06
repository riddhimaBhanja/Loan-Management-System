import { DocumentType } from './document-type.enum';

export interface Document {
  id: number;
  loanId: number;
  documentType: DocumentType;
  fileName: string;
  originalFileName: string;
  filePath: string;
  fileSize: number;
  contentType: string;
  uploadedAt: string;
  uploadedBy: string;
  downloadUrl: string;
}

export interface DocumentUploadRequest {
  loanId: number;
  documentType: DocumentType;
  file: File;
}
