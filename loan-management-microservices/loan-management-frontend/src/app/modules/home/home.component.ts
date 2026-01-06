import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  features = [
    {
      icon: 'account_balance',
      title: 'Quick Loan Processing',
      description: 'Get your loan approved in just 24 hours with our streamlined process'
    },
    {
      icon: 'verified_user',
      title: 'Secure & Trusted',
      description: 'Bank-level security with encrypted transactions and data protection'
    },
    {
      icon: 'savings',
      title: 'Competitive Rates',
      description: 'Enjoy the lowest interest rates in the market with flexible repayment options'
    },
    {
      icon: 'support_agent',
      title: '24/7 Support',
      description: 'Our dedicated team is always available to assist you with any queries'
    }
  ];
}
