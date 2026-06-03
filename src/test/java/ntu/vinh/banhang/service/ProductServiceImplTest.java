package ntu.vinh.banhang.service;

import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.exception.ResourceNotFoundException;
import ntu.vinh.banhang.repository.ProductRepository;
import ntu.vinh.banhang.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartService cartService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProductById_khiKhongTonTai_thiNemResourceNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getProductById_khiTonTai_thiTraVeSanPham() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Coca");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        Product result = productService.getProductById(1L);

        assertThat(result.getName()).isEqualTo("Coca");
    }

    @Test
    void saveProduct_traVeSanPhamDaLuu() {
        Product p = new Product();
        p.setCode("SP01");
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product saved = productService.saveProduct(p);

        assertThat(saved.getCode()).isEqualTo("SP01");
    }
}
