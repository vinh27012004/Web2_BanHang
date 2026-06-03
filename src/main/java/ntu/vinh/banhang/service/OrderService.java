package ntu.vinh.banhang.service;

import java.util.List;

import ntu.vinh.banhang.entity.Invoice;
import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.model.CartItem;

public interface OrderService {
    Invoice createOrder(List<CartItem> cartItems, Customer customer, Double customerPaid, String paymentMethod);
    Invoice getOrder(Long id);
    List<Invoice> getAllOrders();
} 