# Document Upload API - Testing Guide

## Backend Status
✅ Backend is running on http://localhost:8080
✅ All 40 endpoints are mapped and ready
✅ Document upload functionality is fully implemented

## How to Test

### Method 1: Using Postman (Recommended)

#### Step 1: Import the Collection
1. Open Postman
2. Click **Import** button
3. Select the file: `Document_Upload_Tests.postman_collection.json`
4. The collection will appear in your Collections panel

#### Step 2: Get JWT Token
1. Open the collection: **Document Upload API Tests**
2. Run **"0. Login - Get JWT Token"**
3. The token will be automatically saved to the collection variable
4. You're now authenticated for all subsequent requests!

#### Step 3: Test Document Upload
1. Run **"1. Upload Document (ID_PROOF)"**
2. In the Body tab, click on **"file"** field
3. Click **"Select Files"** and choose any PDF, JPG, JPEG, or PNG file (under 5MB)
4. Click **Send**
5. Expected response (201 Created):
   ```json
   {
     "success": true,
     "message": "Document uploaded successfully",
     "data": {
       "id": 1,
       "loanId": 4,
       "documentType": "ID_PROOF",
       "originalFileName": "your_file.pdf",
       "fileSize": 123456,
       "contentType": "application/pdf",
       "uploadedAt": "2025-12-29T20:30:00",
       "uploadedBy": "Riddhima Bhanja",
       "downloadUrl": "/api/documents/1/download"
     }
   }
   ```

#### Step 4: Get All Documents for Loan
1. Run **"2. Get All Documents for Loan"**
2. You should see a list of all uploaded documents
3. Expected response (200 OK): Array of documents

#### Step 5: Download Document
1. Run **"4. Download Document"**
2. The file should download with the original filename
3. Open it to verify it's the correct file

#### Step 6: Test Validation
1. Run **"6. Validation - File Too Large"** with a file > 5MB
   - Expected: 400 Bad Request with error message
2. Run **"7. Validation - Invalid File Type"** with a .txt or .exe file
   - Expected: 400 Bad Request with error about file type
3. Run **"8. Validation - Non-Existent Loan"**
   - Expected: 404 Not Found

#### Step 7: Test Security (Optional)
1. Run **"9. Delete Document (Requires Loan Officer)"**
2. As a CUSTOMER user: Expected 403 Forbidden
3. To test successful deletion, you'd need to login as a LOAN_OFFICER or ADMIN

### Method 2: Using Browser/Swagger UI

1. Open: http://localhost:8080/swagger-ui.html
2. Navigate to **Documents** section
3. Click on **POST /api/documents/upload**
4. Click **Try it out**
5. Fill in the parameters:
   - loanId: 4
   - documentType: ID_PROOF
   - file: Select a file
6. Click **Execute**

### Method 3: Using Frontend (After Frontend Implementation)

Once the frontend components are built, you'll be able to:
1. Login at http://localhost:4200
2. Navigate to "My Loans"
3. Click on a loan application
4. Use the "Upload Document" button
5. Select document type and file
6. Upload and view documents directly in the UI

## Document Types Available

- **ID_PROOF**: Aadhaar, PAN, Passport, Voter ID
- **INCOME_PROOF**: Salary Slips, IT Returns, Form 16
- **ADDRESS_PROOF**: Utility Bills, Rental Agreement
- **BANK_STATEMENT**: Bank statements for last 6 months
- **EMPLOYMENT_PROOF**: Employment letter, Offer letter
- **BUSINESS_PROOF**: GST Certificate, Business license
- **OTHER**: Any other supporting documents

## File Upload Constraints

- **Maximum file size**: 5MB
- **Allowed formats**: PDF, JPG, JPEG, PNG
- **Storage location**: `./uploads/{loanId}/`
- **Filename format**: `{loanId}_{uuid}.{extension}`

## API Endpoints Summary

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/documents/upload` | Upload a document | CUSTOMER, LOAN_OFFICER, ADMIN |
| GET | `/api/documents/loan/{loanId}` | Get all documents for a loan | CUSTOMER, LOAN_OFFICER, ADMIN |
| GET | `/api/documents/{documentId}` | Get document details | CUSTOMER, LOAN_OFFICER, ADMIN |
| GET | `/api/documents/{documentId}/download` | Download document file | CUSTOMER, LOAN_OFFICER, ADMIN |
| DELETE | `/api/documents/{documentId}` | Delete a document | LOAN_OFFICER, ADMIN |

## Verification Checklist

After testing, verify:

- ✅ Files are stored in `./uploads/{loanId}/` directory
- ✅ Database has entries in `documents` table
- ✅ Filenames are UUID-based (no conflicts)
- ✅ Original filenames are preserved in metadata
- ✅ File size validation works (>5MB rejected)
- ✅ File type validation works (only PDF/JPG/JPEG/PNG accepted)
- ✅ Security works:
  - Customers can upload and view documents
  - Customers cannot delete documents
  - Loan officers can delete documents
- ✅ Downloaded files match uploaded files
- ✅ Multiple documents per loan are supported
- ✅ Multiple document types per loan are supported

## Troubleshooting

### Issue: "Failed to upload document"
- Check file size (<5MB)
- Check file type (PDF, JPG, JPEG, PNG only)
- Verify loan ID exists
- Check JWT token is valid

### Issue: "Loan not found"
- Verify the loan ID exists in your database
- Check if you're using the correct loan ID for the logged-in user

### Issue: "403 Forbidden" on delete
- This is expected for CUSTOMER users
- Login as LOAN_OFFICER or ADMIN to test deletion

### Issue: Cannot find file
- Check `./uploads/` directory exists
- Verify file storage permissions
- Check backend logs for file I/O errors

## Next Steps

Once backend testing is complete:
1. ✅ Backend document upload API is working
2. ⏳ Create Angular components for document upload UI
3. ⏳ Integrate with loan application form
4. ⏳ Add document viewer/download functionality
5. ⏳ Test end-to-end flow from frontend to backend
