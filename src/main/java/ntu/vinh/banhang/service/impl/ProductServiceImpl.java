package ntu.vinh.banhang.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.exception.ResourceNotFoundException;
import ntu.vinh.banhang.repository.ProductRepository;
import ntu.vinh.banhang.service.CartService;
import ntu.vinh.banhang.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    @Override
    public Product getProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        try {
            logger.info("Bắt đầu lưu sản phẩm: {}", product);
            Product savedProduct = productRepository.save(product);
            logger.info("Đã lưu sản phẩm thành công với ID: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            logger.error("Lỗi khi lưu sản phẩm: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void addToCart(Long id, Integer quantity) {
        cartService.addToCart(id, quantity);
    }
} 