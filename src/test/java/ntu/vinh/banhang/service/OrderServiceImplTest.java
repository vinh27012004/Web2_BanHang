package ntu.vinh.banhang.service;

import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.entity.Invoice;
import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.entity.User;
import ntu.vinh.banhang.exception.BusinessException;
import ntu.vinh.banhang.model.CartItem;
import ntu.vinh.banhang.repository.InvoiceRepository;
import ntu.vinh.banhang.repository.ProductRepository;
import ntu.vinh.banhang.repository.UserRepository;
import ntu.vinh.banhang.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUsername("admin");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "pwd"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Product product(Long id, double price, int quantity) {
        Product p = new Product();
        p.setId(id);
        p.setCode("SP" + id);
        p.setName("Sản phẩm " + id);
        p.setPrice(price);
        p.setQuantity(quantity);
        return p;
    }

    @Test
    void createOrder_tinhDungTong_tienThua_vaTruKho() {
        Product p = product(1L, 10000d, 100);
        CartItem item = new CartItem(p, 3); // 30.000

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(p));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = orderService.createOrder(List.of(item), new Customer(), 50000d, "CASH");

        assertThat(invoice.getTotalAmount()).isEqualTo(30000d);
        assertThat(invoice.getChangeAmount()).isEqualTo(20000d);
        assertThat(invoice.getPaymentMethod()).isEqualTo("CASH");
        assertThat(invoice.getItems()).hasSize(1);
        // Tồn kho phải bị trừ từ 100 xuống 97
        assertThat(p.getQuantity()).isEqualTo(97);
    }

    @Test
    void createOrder_thanhToanQR_khongCoTienThua() {
        Product p = product(1L, 10000d, 100);
        CartItem item = new CartItem(p, 2); // 20.000

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(p));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        // Thanh toán QR: customerPaid bằng đúng tổng tiền, không có tiền thừa
        Invoice invoice = orderService.createOrder(List.of(item), new Customer(), 20000d, "QR");

        assertThat(invoice.getPaymentMethod()).isEqualTo("QR");
        assertThat(invoice.getChangeAmount()).isEqualTo(0d);
        assertThat(invoice.getTotalAmount()).isEqualTo(20000d);
    }

    @Test
    void createOrder_khiKhongDuTonKho_thiNemBusinessException() {
        Product p = product(1L, 10000d, 2);
        CartItem item = new CartItem(p, 5); // yêu cầu 5 nhưng chỉ còn 2

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(currentUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(p));

        assertThatThrownBy(() -> orderService.createOrder(List.of(item), new Customer(), 100000d, "CASH"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Không đủ tồn kho");
    }
}
