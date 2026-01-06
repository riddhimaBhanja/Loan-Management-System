package com.loanmanagement.notification.infrastructure.config;

import com.loanmanagement.notification.domain.model.NotificationTemplate;
import com.loanmanagement.notification.domain.model.NotificationType;
import com.loanmanagement.notification.domain.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialize default notification templates on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final NotificationTemplateRepository templateRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing default notification templates...");

        createTemplateIfNotExists(
                "loan-submitted-default",
                NotificationType.LOAN_SUBMITTED,
                "📝 Loan Application Submitted Successfully",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#4facfe 0%,#00f2fe 100%);padding:40px 30px;text-align:center;}" +
                        ".header-icon{font-size:64px;margin-bottom:10px;}" +
                        ".header h1{color:#fff;margin:0;font-size:28px;font-weight:600;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:20px;color:#333;margin-bottom:20px;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".info-box{background:#e6f7ff;border-left:4px solid #4facfe;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid #e0e0e0;}" +
                        ".info-row:last-child{border-bottom:none;}" +
                        ".info-label{font-weight:600;color:#666;}" +
                        ".info-value{color:#333;font-weight:500;}" +
                        ".timeline-box{background:#f0f9ff;padding:25px;border-radius:10px;margin:25px 0;}" +
                        ".timeline-title{font-weight:600;color:#0369a1;margin-bottom:15px;font-size:16px;}" +
                        ".timeline-step{padding:10px 0;color:#555;font-size:14px;line-height:1.6;}" +
                        ".timeline-step::before{content:'✓';color:#10b981;font-weight:700;margin-right:10px;}" +
                        ".status-badge{background:linear-gradient(135deg,#fbbf24 0%,#f59e0b 100%);color:#fff;padding:12px 20px;text-align:center;border-radius:8px;margin:20px 0;font-size:16px;font-weight:600;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>📝</div><h1>Application Submitted!</h1></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='message'>Thank you for submitting your loan application! We have received your request and it is now being processed by our team.</div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><span class='info-label'>Application ID:</span><span class='info-value'>{{loanId}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Requested Amount:</span><span class='info-value'>₹{{loanAmount}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Status:</span><span class='info-value'>Under Review</span></div>" +
                        "</div>" +
                        "<div class='status-badge'>⏳ APPLICATION UNDER REVIEW</div>" +
                        "<div class='timeline-box'><div class='timeline-title'>📋 What happens next?</div>" +
                        "<div class='timeline-step'>Your application will be reviewed by our loan officers</div>" +
                        "<div class='timeline-step'>We'll verify your documents and creditworthiness</div>" +
                        "<div class='timeline-step'>You'll receive a decision notification via email</div>" +
                        "<div class='timeline-step'>If approved, funds will be disbursed to your account</div></div>" +
                        "<div class='message'>We typically process applications within 2-3 business days. You will be notified via email once a decision has been made.</div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Your financial journey starts here<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for loan submission confirmation"
        );

        createTemplateIfNotExists(
                "loan-approved-default",
                NotificationType.LOAN_APPROVED,
                "🎉 Congratulations! Your Loan is Approved",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f4f7fa;'>" +
                        "<div style='max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);'>" +
                        "<div style='background:#667eea;padding:40px 30px;text-align:center;'>" +
                        "<div style='font-size:64px;margin-bottom:10px;'>🎉</div>" +
                        "<h1 style='color:#fff;margin:0;font-size:28px;font-weight:600;'>Loan Approved!</h1>" +
                        "</div>" +
                        "<div style='padding:40px 30px;'>" +
                        "<div style='font-size:20px;color:#333;margin-bottom:20px;'>Dear {{recipientName}},</div>" +
                        "<div style='color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;'>We are delighted to inform you that your loan application has been <strong>approved</strong>! Congratulations on taking this important step forward.</div>" +
                        "<div style='background:#f8f9ff;border-left:4px solid #667eea;padding:20px;margin:20px 0;border-radius:6px;'>" +
                        "<table width='100%' cellpadding='8' style='border-collapse:collapse;'>" +
                        "<tr style='border-bottom:1px solid #e0e0e0;'><td style='font-weight:600;color:#666;'>Loan ID:</td><td style='color:#333;font-weight:500;text-align:right;'>{{loanId}}</td></tr>" +
                        "<tr style='border-bottom:1px solid #e0e0e0;'><td style='font-weight:600;color:#666;'>Requested Amount:</td><td style='color:#333;font-weight:500;text-align:right;'>₹{{loanAmount}}</td></tr>" +
                        "<tr><td style='font-weight:600;color:#666;'>Approved Amount:</td><td style='color:#333;font-weight:500;text-align:right;'>₹{{approvedAmount}}</td></tr>" +
                        "</table>" +
                        "</div>" +
                        "<div style='background:#48bb78;color:#fff;padding:15px 20px;border-radius:8px;text-align:center;margin:25px 0;font-size:18px;font-weight:600;'>💰 Amount will be disbursed shortly</div>" +
                        "<div style='color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;'>The approved loan amount will be disbursed to your registered bank account within 2-3 business days. You will receive EMI payment details soon.</div>" +
                        "</div>" +
                        "<div style='background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;'><strong style='color:#333;'>Loan Management System</strong><br/>Your trusted financial partner<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for loan approval notification"
        );

        createTemplateIfNotExists(
                "loan-rejected-default",
                NotificationType.LOAN_REJECTED,
                "📋 Loan Application Update",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f4f7fa;'>" +
                        "<div style='max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);'>" +
                        "<div style='background:#fc8181;padding:40px 30px;text-align:center;'>" +
                        "<div style='font-size:64px;margin-bottom:10px;'>📋</div>" +
                        "<h1 style='color:#fff;margin:0;font-size:28px;font-weight:600;'>Application Update</h1>" +
                        "</div>" +
                        "<div style='padding:40px 30px;'>" +
                        "<div style='font-size:20px;color:#333;margin-bottom:20px;'>Dear {{recipientName}},</div>" +
                        "<div style='color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;'>Thank you for your interest in our loan services. After careful review of your application, we regret to inform you that we are unable to approve your loan request at this time.</div>" +
                        "<div style='background:#fff5f5;border-left:4px solid #fc8181;padding:20px;margin:20px 0;border-radius:6px;'>" +
                        "<table width='100%' cellpadding='8' style='border-collapse:collapse;'>" +
                        "<tr style='border-bottom:1px solid #e0e0e0;'><td style='font-weight:600;color:#666;'>Loan ID:</td><td style='color:#333;font-weight:500;text-align:right;'>{{loanId}}</td></tr>" +
                        "<tr><td style='font-weight:600;color:#666;'>Application Date:</td><td style='color:#333;font-weight:500;text-align:right;'>Today</td></tr>" +
                        "</table>" +
                        "</div>" +
                        "<div style='background:#fef5e7;border:2px solid #f39c12;padding:20px;border-radius:8px;margin:20px 0;'>" +
                        "<div style='font-weight:600;color:#d68910;margin-bottom:10px;'>📝 Reason for Decision:</div>" +
                        "<div style='color:#555;line-height:1.6;'>{{reason}}</div>" +
                        "</div>" +
                        "<div style='background:#ebf8ff;padding:20px;border-radius:8px;margin:25px 0;text-align:center;'>" +
                        "<div style='color:#2c5282;font-size:15px;margin-bottom:15px;'>💡 Don't worry! You can reapply after addressing the concerns mentioned above.</div>" +
                        "<a href='#' style='display:inline-block;background:#4299e1;color:#fff;padding:12px 30px;border-radius:6px;text-decoration:none;font-weight:600;'>Contact Support</a>" +
                        "</div>" +
                        "</div>" +
                        "<div style='background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;'><strong style='color:#333;'>Loan Management System</strong><br/>We're here to help<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for loan rejection notification"
        );

        createTemplateIfNotExists(
                "loan-disbursed-default",
                NotificationType.LOAN_DISBURSED,
                "💰 Loan Disbursed Successfully!",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#11998e 0%,#38ef7d 100%);padding:40px 30px;text-align:center;}" +
                        ".header-icon{font-size:64px;margin-bottom:10px;}" +
                        ".header h1{color:#fff;margin:0;font-size:28px;font-weight:600;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:20px;color:#333;margin-bottom:20px;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".amount-box{background:linear-gradient(135deg,#d4fc79 0%,#96e6a1 100%);padding:30px;text-align:center;border-radius:10px;margin:25px 0;border:3px solid #38ef7d;}" +
                        ".amount-label{color:#065f46;font-size:14px;font-weight:600;margin-bottom:8px;}" +
                        ".amount-value{color:#065f46;font-size:40px;font-weight:700;}" +
                        ".info-box{background:#ecfdf5;border-left:4px solid #11998e;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid #e0e0e0;}" +
                        ".info-row:last-child{border-bottom:none;}" +
                        ".info-label{font-weight:600;color:#666;}" +
                        ".info-value{color:#333;font-weight:500;}" +
                        ".success-icon{font-size:48px;text-align:center;margin:20px 0;}" +
                        ".next-steps-box{background:#f0fdfa;padding:25px;border-radius:10px;margin:25px 0;border:2px solid #5eead4;}" +
                        ".next-steps-title{font-weight:600;color:#0f766e;margin-bottom:15px;font-size:16px;}" +
                        ".next-step{padding:8px 0;color:#555;font-size:14px;line-height:1.6;}" +
                        ".next-step::before{content:'→';color:#14b8a6;font-weight:700;margin-right:10px;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>💰</div><h1>Funds Disbursed!</h1></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='success-icon'>✨</div>" +
                        "<div class='message'>Great news! Your loan has been successfully disbursed. The funds have been credited to your registered bank account.</div>" +
                        "<div class='amount-box'><div class='amount-label'>DISBURSED AMOUNT</div><div class='amount-value'>₹{{amount}}</div></div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><span class='info-label'>Loan ID:</span><span class='info-value'>{{loanId}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Disbursement Date:</span><span class='info-value'>{{disbursementDate}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Status:</span><span class='info-value'>✅ Credited</span></div>" +
                        "</div>" +
                        "<div class='next-steps-box'><div class='next-steps-title'>📌 Important Next Steps</div>" +
                        "<div class='next-step'>Please verify the credit in your bank account within 24 hours</div>" +
                        "<div class='next-step'>Your EMI schedule will be shared in a separate email</div>" +
                        "<div class='next-step'>First EMI payment will be due as per the schedule</div>" +
                        "<div class='next-step'>Keep your loan documents safe for future reference</div></div>" +
                        "<div class='message'>If you have any questions or don't see the funds in your account within 24 hours, please contact our support team immediately.</div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Empowering your financial goals<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for loan disbursement notification"
        );

        createTemplateIfNotExists(
                "emi-due-default",
                NotificationType.EMI_DUE,
                "⏰ EMI Payment Reminder - Due Soon!",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#f093fb 0%,#f5576c 100%);padding:40px 30px;text-align:center;}" +
                        ".header-icon{font-size:64px;margin-bottom:10px;}" +
                        ".header h1{color:#fff;margin:0;font-size:28px;font-weight:600;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:20px;color:#333;margin-bottom:20px;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".amount-box{background:linear-gradient(135deg,#ffeaa7 0%,#fdcb6e 100%);padding:30px;text-align:center;border-radius:10px;margin:25px 0;}" +
                        ".amount-label{color:#2d3436;font-size:14px;font-weight:600;margin-bottom:8px;}" +
                        ".amount-value{color:#2d3436;font-size:36px;font-weight:700;}" +
                        ".info-box{background:#f8f9ff;border-left:4px solid #f093fb;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid #e0e0e0;}" +
                        ".info-row:last-child{border-bottom:none;}" +
                        ".info-label{font-weight:600;color:#666;}" +
                        ".info-value{color:#333;font-weight:500;}" +
                        ".alert-box{background:#fff3cd;border:2px solid #ffc107;padding:15px;border-radius:8px;margin:20px 0;text-align:center;}" +
                        ".alert-text{color:#856404;font-size:14px;font-weight:600;}" +
                        ".cta-button{display:inline-block;background:linear-gradient(135deg,#11998e 0%,#38ef7d 100%);color:#fff;padding:14px 40px;border-radius:8px;text-decoration:none;font-weight:600;margin:20px 0;font-size:16px;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>⏰</div><h1>Payment Reminder</h1></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='message'>This is a friendly reminder that your EMI payment is due soon. Please ensure timely payment to maintain a good credit history.</div>" +
                        "<div class='amount-box'><div class='amount-label'>AMOUNT DUE</div><div class='amount-value'>₹{{amount}}</div></div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><span class='info-label'>EMI Number:</span><span class='info-value'>#{{emiNumber}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Due Date:</span><span class='info-value'>{{dueDate}}</span></div>" +
                        "</div>" +
                        "<div class='alert-box'><span class='alert-text'>⚠️ Late payments may incur additional charges</span></div>" +
                        "<div style='text-align:center;'><a href='#' class='cta-button'>Pay Now →</a></div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Secure • Trusted • Reliable<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for EMI due reminder"
        );

        createTemplateIfNotExists(
                "emi-paid-default",
                NotificationType.EMI_PAID,
                "✅ EMI Payment Received Successfully",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#43e97b 0%,#38f9d7 100%);padding:40px 30px;text-align:center;}" +
                        ".header-icon{font-size:64px;margin-bottom:10px;}" +
                        ".header h1{color:#fff;margin:0;font-size:28px;font-weight:600;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:20px;color:#333;margin-bottom:20px;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".success-badge{background:linear-gradient(135deg,#10b981 0%,#059669 100%);color:#fff;padding:20px;text-align:center;border-radius:10px;margin:25px 0;font-size:20px;font-weight:700;}" +
                        ".info-box{background:#ecfdf5;border-left:4px solid #43e97b;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid #d1fae5;}" +
                        ".info-row:last-child{border-bottom:none;}" +
                        ".info-label{font-weight:600;color:#666;}" +
                        ".info-value{color:#333;font-weight:500;}" +
                        ".amount-highlight{background:#d1fae5;padding:15px;border-radius:8px;text-align:center;margin:20px 0;border:2px solid #6ee7b7;}" +
                        ".amount-label{color:#065f46;font-size:13px;font-weight:600;margin-bottom:5px;}" +
                        ".amount-value{color:#047857;font-size:32px;font-weight:700;}" +
                        ".appreciation-box{background:#fef3c7;padding:20px;border-radius:10px;margin:25px 0;text-align:center;}" +
                        ".appreciation-text{color:#92400e;font-size:15px;font-weight:600;line-height:1.6;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>✅</div><h1>Payment Successful!</h1></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='success-badge'>💚 PAYMENT RECEIVED</div>" +
                        "<div class='message'>Thank you! We have successfully received your EMI payment. Your payment has been processed and credited to your loan account.</div>" +
                        "<div class='amount-highlight'><div class='amount-label'>AMOUNT PAID</div><div class='amount-value'>₹{{amount}}</div></div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><span class='info-label'>EMI Number:</span><span class='info-value'>{{emiNumber}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Payment Date:</span><span class='info-value'>{{paymentDate}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Payment Status:</span><span class='info-value'>✓ Confirmed</span></div>" +
                        "<div class='info-row'><span class='info-label'>Outstanding Balance:</span><span class='info-value'>Updated</span></div>" +
                        "</div>" +
                        "<div class='appreciation-box'><div class='appreciation-text'>⭐ Thank you for your timely payment!<br/>You're maintaining an excellent payment history.</div></div>" +
                        "<div class='message'>Your payment receipt and updated loan statement are being generated. You can view your complete payment history and remaining balance in your account dashboard.</div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Thank you for your trust<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for EMI payment confirmation"
        );

        createTemplateIfNotExists(
                "loan-closed-default",
                NotificationType.LOAN_CLOSED,
                "✅ Congratulations! Loan Successfully Closed",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#56ab2f 0%,#a8e063 100%);padding:40px 30px;text-align:center;}" +
                        ".header-icon{font-size:64px;margin-bottom:10px;}" +
                        ".header h1{color:#fff;margin:0;font-size:28px;font-weight:600;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:20px;color:#333;margin-bottom:20px;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".success-badge{background:linear-gradient(135deg,#56ab2f 0%,#a8e063 100%);color:#fff;padding:20px;text-align:center;border-radius:10px;margin:25px 0;font-size:24px;font-weight:700;}" +
                        ".info-box{background:#f0fdf4;border-left:4px solid #56ab2f;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{display:flex;justify-content:space-between;padding:8px 0;border-bottom:1px solid #e0e0e0;}" +
                        ".info-row:last-child{border-bottom:none;}" +
                        ".info-label{font-weight:600;color:#666;}" +
                        ".info-value{color:#333;font-weight:500;}" +
                        ".congrats-box{background:#e0f2fe;padding:25px;border-radius:10px;margin:25px 0;text-align:center;}" +
                        ".congrats-text{color:#0c4a6e;font-size:16px;line-height:1.6;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>✅</div><h1>Loan Closed Successfully!</h1></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='success-badge'>🎊 LOAN FULLY PAID! 🎊</div>" +
                        "<div class='message'>Congratulations! Your loan account has been successfully closed. All your EMI payments have been received and your loan obligation is now complete.</div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><span class='info-label'>Loan ID:</span><span class='info-value'>{{loanId}}</span></div>" +
                        "<div class='info-row'><span class='info-label'>Status:</span><span class='info-value'>✅ CLOSED</span></div>" +
                        "<div class='info-row'><span class='info-label'>Final Payment:</span><span class='info-value'>Completed</span></div>" +
                        "</div>" +
                        "<div class='congrats-box'><div class='congrats-text'>🌟 Thank you for being a valued customer! Your timely payments demonstrate excellent financial responsibility. We look forward to serving you again in the future.</div></div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Building financial futures together<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for loan closure notification"
        );

        createTemplateIfNotExists(
                "account-created-default",
                NotificationType.ACCOUNT_CREATED,
                "🎉 Welcome to Loan Management System!",
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><style>" +
                        "body{margin:0;padding:0;font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;background-color:#f4f7fa;}" +
                        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}" +
                        ".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:50px 30px;text-align:center;}" +
                        ".header-icon{font-size:72px;margin-bottom:15px;}" +
                        ".header h1{color:#fff;margin:0;font-size:32px;font-weight:700;}" +
                        ".header-subtitle{color:#e0e7ff;margin-top:10px;font-size:16px;}" +
                        ".content{padding:40px 30px;}" +
                        ".greeting{font-size:22px;color:#333;margin-bottom:20px;font-weight:600;}" +
                        ".message{color:#555;font-size:16px;line-height:1.6;margin-bottom:30px;}" +
                        ".welcome-badge{background:linear-gradient(135deg,#fbbf24 0%,#f59e0b 100%);color:#fff;padding:20px;text-align:center;border-radius:10px;margin:25px 0;font-size:18px;font-weight:700;}" +
                        ".info-box{background:#f5f3ff;border-left:4px solid #667eea;padding:20px;margin:20px 0;border-radius:6px;}" +
                        ".info-row{padding:8px 0;}" +
                        ".info-label{font-weight:600;color:#666;font-size:14px;margin-bottom:4px;}" +
                        ".info-value{color:#333;font-size:16px;font-weight:500;}" +
                        ".features-box{background:#eff6ff;padding:25px;border-radius:10px;margin:25px 0;}" +
                        ".features-title{font-weight:600;color:#1e40af;margin-bottom:15px;font-size:17px;}" +
                        ".feature{padding:10px 0;color:#555;font-size:14px;line-height:1.6;}" +
                        ".feature::before{content:'✓';color:#10b981;font-weight:700;margin-right:12px;font-size:16px;}" +
                        ".cta-box{text-align:center;margin:30px 0;}" +
                        ".cta-button{display:inline-block;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#fff;padding:15px 40px;border-radius:8px;text-decoration:none;font-weight:600;font-size:16px;}" +
                        ".footer{background:#f8f9fa;padding:30px;text-align:center;color:#666;font-size:14px;}" +
                        ".footer strong{color:#333;}" +
                        ".divider{border-top:2px dashed #e5e7eb;margin:25px 0;}" +
                        "</style></head><body>" +
                        "<div class='container'>" +
                        "<div class='header'><div class='header-icon'>🎉</div><h1>Welcome Aboard!</h1><div class='header-subtitle'>Your account is ready to go</div></div>" +
                        "<div class='content'>" +
                        "<div class='greeting'>Dear {{recipientName}},</div>" +
                        "<div class='message'>Welcome to Loan Management System! We're thrilled to have you join our community. Your account has been successfully created and you're all set to begin your financial journey with us.</div>" +
                        "<div class='welcome-badge'>🌟 ACCOUNT ACTIVATED</div>" +
                        "<div class='info-box'>" +
                        "<div class='info-row'><div class='info-label'>Username</div><div class='info-value'>{{username}}</div></div>" +
                        "<div class='info-row'><div class='info-label'>Email Address</div><div class='info-value'>{{email}}</div></div>" +
                        "<div class='info-row'><div class='info-label'>Account Status</div><div class='info-value'>✅ Active</div></div>" +
                        "</div>" +
                        "<div class='features-box'><div class='features-title'>🚀 What you can do now:</div>" +
                        "<div class='feature'>Apply for personal, home, or business loans</div>" +
                        "<div class='feature'>Track your loan applications in real-time</div>" +
                        "<div class='feature'>View and manage your EMI schedules</div>" +
                        "<div class='feature'>Access your complete payment history</div>" +
                        "<div class='feature'>Update your profile and preferences anytime</div></div>" +
                        "<div class='cta-box'><a href='#' class='cta-button'>Get Started →</a></div>" +
                        "<div class='divider'></div>" +
                        "<div class='message'>If you have any questions or need assistance, our support team is here to help you 24/7. We're committed to making your loan experience smooth and hassle-free.</div>" +
                        "</div>" +
                        "<div class='footer'><strong>Loan Management System</strong><br/>Your trusted financial partner<br/>📧 support@loanmanagement.com | 📞 1800-123-4567</div>" +
                        "</div></body></html>",
                "Beautiful template for account creation notification"
        );

        log.info("Default notification templates initialized successfully.");
    }

    private void createTemplateIfNotExists(String name, NotificationType type, String subject,
                                           String bodyTemplate, String description) {
        if (!templateRepository.existsByName(name)) {
            NotificationTemplate template = NotificationTemplate.builder()
                    .name(name)
                    .type(type)
                    .subject(subject)
                    .bodyTemplate(bodyTemplate)
                    .description(description)
                    .isActive(true)
                    .build();

            templateRepository.save(template);
            log.info("Created template: {}", name);
        } else {
            // Update existing template to ensure latest version is used
            NotificationTemplate existingTemplate = templateRepository.findByName(name).orElse(null);
            if (existingTemplate != null) {
                existingTemplate.setSubject(subject);
                existingTemplate.setBodyTemplate(bodyTemplate);
                existingTemplate.setDescription(description);
                existingTemplate.setType(type);
                templateRepository.save(existingTemplate);
                log.info("Updated existing template: {}", name);
            }
        }
    }
}
