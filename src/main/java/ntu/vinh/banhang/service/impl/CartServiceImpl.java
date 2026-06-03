package ntu.vinh.banhang.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.exception.BusinessException;
import ntu.vinh.banhang.exception.ResourceNotFoundException;
import ntu.vinh.banhang.model.CartItem;
import ntu.vinh.banhang.repository.ProductRepository;
import ntu.vinh.banhang.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "cartItems";

    @Autowired
    private ProductRepository productRepository;

    private Map<Long, CartItem> getCartItemsFromSession() {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        @SuppressWarnings("unchecked")
        Map<Long, CartItem> cartItems = (Map<Long, CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cartItems == null) {
            cartItems = new ConcurrentHashMap<>();
            session.setAttribute(CART_SESSION_KEY, cartItems);
        }
        return cartItems;
    }

    @Override
    public void addToCart(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        if (product.getQuantity() < quantity) {
            throw new BusinessException("Số lượng vượt quá tồn kho hiện có");
        }

        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        cartItems.compute(productId, (key, existingItem) -> {
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (product.getQuantity() < newQuantity) {
                    throw new BusinessException("Số lượng vượt quá tồn kho hiện có");
                }
                existingItem.setQuantity(newQuantity);
                return existingItem;
            }
            return new CartItem(product, quantity);
        });
    }

    @Override
    public void removeFromCart(Long productId) {
        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        cartItems.remove(productId);
    }

    @Override
    public void updateQuantity(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        if (product.getQuantity() < quantity) {
            throw new BusinessException("Số lượng vượt quá tồn kho hiện có");
        }

        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        cartItems.computeIfPresent(productId, (key, item) -> {
            item.setQuantity(quantity);
            return item;
        });
    }

    @Override
    public List<CartItem> getCartItems() {
        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        return new ArrayList<>(cartItems.values());
    }

    @Override
    public Double getTotalAmount() {
        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        return cartItems.values().stream()
            .mapToDouble(CartItem::getTotalPrice)
            .sum();
    }

    @Override
    public void clearCart() {
        Map<Long, CartItem> cartItems = getCartItemsFromSession();
        cartItems.clear();
    }
} 