import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, EMPTY, finalize, Observable, switchMap } from 'rxjs';
import { Order } from '../../shared/models/order.model';
import { OrderService } from '../../core/services/order.service';

import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { NzTagModule } from 'ng-zorro-antd/tag'; 
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDescriptionsModule } from 'ng-zorro-antd/descriptions';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzMessageService } from 'ng-zorro-antd/message';
import { ApiResponse } from '../../shared/models/api-response.model';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NzDescriptionsModule,
    NzTagModule,
    NzPageHeaderModule,
    NzButtonModule,
    NzIconModule,
    NzSpinModule
  ],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss'
})

export class OrderDetailComponent implements OnInit {

  order$!: Observable<Order>;
  private refresh$ = new BehaviorSubject<void>(undefined);
  isLoading = false;
  orderId!: string;

  constructor(
    private router: ActivatedRoute, 
    private orderService: OrderService, 
    private message: NzMessageService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    
    this.router.paramMap.pipe(
      switchMap((params) => {
        const id = params.get('id');
        if (!id) {
          this.message.error('Invalid order id');
          throw new Error('Invalid order id');
        }
        this.orderId = id;
        return this.refresh$.pipe(
          switchMap(() => this.orderService.getOrderById(Number(id)))
        );
      })
    ).subscribe((data) => {
      this.order$ = new BehaviorSubject(data.data);
      this.changeDetectorRef.detectChanges();
    });
  }
  private refreshData(): void {
    this.refresh$.next();
  }

  onPay(id: number): void {
    this.handleAction(this.orderService.pay(id.toString()), '支付成功。');
  }

  onCancel(id: number): void {
    this.handleAction(this.orderService.cancel(id.toString()), '订单已经取消。');
  }

  onReTry(id: number): void {
    this.isLoading = true;
    this.orderService
    .reTry(id.toString())
    .pipe(finalize(() => (this.isLoading = false)))
    .subscribe(() => {
      this.message.success('重新尝试购票成功。');
      this.refreshData();
    })
    ;
    this.handleAction(this.orderService.reTry(this.orderId), '重新尝试购票');
  }

  private handleAction(
    action$: Observable<ApiResponse<Order>>, 
    successmessage: string): void {
    this.isLoading = true;
    action$
    .pipe(
      finalize(() => {
        this.isLoading = false;
        this.refreshData();
      }),
      catchError((error) => {
        this.message.error("操作失败，请重试！");
        console.error(error);
        return EMPTY;
      })
    )
    .subscribe(() => {
      this.message.success(successmessage);
    })
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
}
