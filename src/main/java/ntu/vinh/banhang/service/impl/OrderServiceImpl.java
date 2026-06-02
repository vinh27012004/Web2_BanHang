package ntu.vinh.banhang.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ntu.vinh.banhang.entity.Invoice;
import ntu.vinh.banhang.entity.InvoiceItem;
import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.entity.User;
import ntu.vinh.banhang.model.CartItem;
import ntu.vinh.banhang.repository.InvoiceRepository;
import ntu.vinh.banhang.repository.ProductRepository;
import ntu.vinh.banhang.repository.UserRepository;
import ntu.vinh.banhang.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Invoice createOrder(List<CartItem> cartItems, Customer customer, Double customerPaid) {
        Invoice invoice = new Invoice();
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setCustomer(customer);
        
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        invoice.setUser(currentUser);
        
        // Tải trước tất cả sản phẩm liên quan bằng một truy vấn duy nhất (Batch Fetching)
        List<Long> productIds = cartItems.stream()
            .map(item -> item.getProduct().getId())
            .collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
        
        double totalAmount = 0;
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getId();
            Product product = productMap.get(productId);
            if (product == null) {
                throw new RuntimeException("Product not found with ID: " + productId);
            }
            
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setProduct(product);
            invoiceItem.setQuantity(cartItem.getQuantity());
            invoiceItem.setUnitPrice(product.getPrice());
            invoiceItem.setTotalPrice(cartItem.getTotalPrice());
            invoiceItem.setInvoice(invoice);
            
            invoiceItems.add(invoiceItem);
            totalAmount += invoiceItem.getTotalPrice();
            
            // Cập nhật số lượng tồn kho trực tiếp trên entity được JPA quản lý (dirty checking sẽ tự động đồng bộ)
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
        }
        
        invoice.setTotalAmount(totalAmount);
        invoice.setCustomerPaid(customerPaid);
        invoice.setChangeAmount(customerPaid - totalAmount);
        invoice.setItems(invoiceItems);
        
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getOrder(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Invoice> getAllOrders() {
        return invoiceRepository.findAll();
    }
} 