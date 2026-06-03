package ntu.vinh.banhang.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.zxing.WriterException;

import ntu.vinh.banhang.config.VietQrProperties;
import ntu.vinh.banhang.model.CartItem;
import ntu.vinh.banhang.service.CartService;
import ntu.vinh.banhang.service.OrderService;
import ntu.vinh.banhang.service.VietQrService;
import ntu.vinh.banhang.service.CustomerService;
import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.entity.Invoice;
import ntu.vinh.banhang.util.QrCodeUtil;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VietQrService vietQrService;

    @Autowired
    private VietQrProperties vietQrProperties;

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrder(id));
        return "order/detail";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }
        prepareCheckoutModel(model);
        return "checkout";
    }

    @PostMapping("/create")
    public String createOrder(
            @RequestParam(required = false) Double customerPaid,
            @RequestParam(defaultValue = "CASH") String paymentMethod,
            @RequestParam(required = false) Long customerId,
            Model model) {
        try {
            List<CartItem> cartItems = cartService.getCartItems();
            if (cartItems.isEmpty()) {
                return "redirect:/cart";
            }

            Double totalAmount = cartService.getTotalAmount();
            boolean isQr = "QR".equals(paymentMethod);

            if (isQr) {
                // Thanh toán QR: khách chuyển khoản đúng số tiền, không có tiền thừa
                customerPaid = totalAmount;
            } else if (customerPaid == null || customerPaid < totalAmount) {
                model.addAttribute("error", "Số tiền thanh toán không đủ");
                prepareCheckoutModel(model);
                return "checkout";
            }

            Customer customer = null;
            if (customerId != null) {
                customer = customerService.getCustomerById(customerId);
            }

            Invoice order = orderService.createOrder(cartItems, customer, customerPaid, paymentMethod);
            cartService.clearCart();
            return "redirect:/order/" + order.getId();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            prepareCheckoutModel(model);
            return "checkout";
        }
    }

    /**
     * Trả về ảnh QR (PNG) cho thanh toán VietQR với số tiền và nội dung cho trước.
     * Chỉ STAFF/ADMIN truy cập được do nằm dưới /order (xem cấu hình Security).
     */
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] qrImage(@RequestParam long amount,
                          @RequestParam(required = false) String content) throws WriterException, IOException {
        return QrCodeUtil.toPng(vietQrService.buildPayload(amount, content), 320);
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "order-success";
    }

    /** Nạp các thuộc tính cần thiết cho trang checkout (dùng chung cho cả GET và các nhánh lỗi). */
    private void prepareCheckoutModel(Model model) {
        double totalAmount = cartService.getTotalAmount();
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("qrEnabled", vietQrProperties.isEnabled());
        model.addAttribute("bankAccountName", vietQrProperties.getAccountName());
        model.addAttribute("bankAccountNo", vietQrProperties.getAccountNo());
        // Nội dung chuyển khoản giúp đối soát giao dịch
        model.addAttribute("qrContent", "POS" + System.currentTimeMillis());
    }
} 