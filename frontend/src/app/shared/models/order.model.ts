import { User } from "./user.model";

export interface Order {
  id: number;
  orderNumber: string;
  status: string;
  amount: number;
  creationDate: Date;
  user: User;
  flightInfo?: any;
}