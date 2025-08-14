import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Order } from '../../shared/models/order.model';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';

import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag'; 
import { NzButtonModule } from 'ng-zorro-antd/button';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NzTableModule,
    NzTagModule,
    NzButtonModule
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {

  orders$!: Observable<Order[]>;
  listData: Order[] = [];
  loading = true;
  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.orderService.getOrders().subscribe(data => {
      this.listData = data.data;
      this.orders$ = new BehaviorSubject(data.data);
      this.loading = false;
      this.changeDetectorRef.detectChanges();
    });
  }

  getStatusColor(status: Order['status']): string {
    switch (status) {
      case 'PAID': return 'green';
      case 'TICKETED': return 'blue';
      case 'CANCELLED': return 'red';
      case 'PENDING_PAYMENT': return 'orange';
      default: return 'default';
    }
  }

  logout() {
    this.authService.logout();
  }

}
