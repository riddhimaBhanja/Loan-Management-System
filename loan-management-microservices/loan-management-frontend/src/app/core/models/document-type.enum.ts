export enum DocumentType {
  ID_PROOF = 'ID_PROOF',
  INCOME_PROOF = 'INCOME_PROOF',
  ADDRESS_PROOF = 'ADDRESS_PROOF',
  BANK_STATEMENT = 'BANK_STATEMENT',
  EMPLOYMENT_PROOF = 'EMPLOYMENT_PROOF',
  BUSINESS_PROOF = 'BUSINESS_PROOF',
  OTHER = 'OTHER'
}

export const DocumentTypeLabels: Record<DocumentType, string> = {
  [DocumentType.ID_PROOF]: 'ID Proof (Aadhaar, PAN, Passport)',
  [DocumentType.INCOME_PROOF]: 'Income Proof (Salary Slips, IT Returns)',
  [DocumentType.ADDRESS_PROOF]: 'Address Proof (Utility Bills)',
  [DocumentType.BANK_STATEMENT]: 'Bank Statement',
  [DocumentType.EMPLOYMENT_PROOF]: 'Employment Proof',
  [DocumentType.BUSINESS_PROOF]: 'Business Proof (GST, License)',
  [DocumentType.OTHER]: 'Other Documents'
};
