package ntu.vinh.banhang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import ntu.vinh.banhang.entity.Product;
import ntu.vinh.banhang.service.ProductService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("availableImages", listAvailableImages());
        return "products";
    }

    /**
     * Quét thư mục classpath:/image/ để lấy danh sách tên file ảnh có sẵn,
     * dùng cho dropdown chọn ảnh khi thêm/sửa sản phẩm.
     */
    private List<String> listAvailableImages() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/image/*");
            List<String> names = new ArrayList<>();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && !filename.isBlank()) {
                    names.add(filename);
                }
            }
            Collections.sort(names);
            return names;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @PostMapping("/add")
    @ResponseBody
    public String addProduct(@RequestParam @NotBlank(message = "Mã sản phẩm không được để trống") String code,
                           @RequestParam @NotBlank(message = "Tên sản phẩm không được để trống") String name,
                           @RequestParam @Min(value = 0, message = "Giá không được âm") double price,
                           @RequestParam @Min(value = 0, message = "Số lượng không được âm") int quantity,
                           @RequestParam(required = false) String image) {
        // Kiểm tra xem sản phẩm đã tồn tại chưa
        Product existingProduct = productService.getProductByCode(code);
        if (existingProduct != null) {
            // Nếu đã tồn tại, cập nhật thông tin
            existingProduct.setName(name);
            existingProduct.setPrice(price);
            existingProduct.setQuantity(quantity);
            existingProduct.setImage(image);
            productService.saveProduct(existingProduct);
            return "Cập nhật sản phẩm thành công";
        }
        // Nếu chưa tồn tại, thêm mới
        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImage(image);
        productService.saveProduct(product);
        return "Thêm sản phẩm thành công";
    }

    @PostMapping("/{id}/update")
    @ResponseBody
    public String updateProduct(@PathVariable Long id,
                              @RequestParam @NotBlank(message = "Tên sản phẩm không được để trống") String name,
                              @RequestParam @Min(value = 0, message = "Giá không được âm") double price,
                              @RequestParam @Min(value = 0, message = "Số lượng không được âm") int quantity,
                              @RequestParam(required = false) String image) {
        Product product = productService.getProductById(id);
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        if (image != null) {
            product.setImage(image);
        }
        productService.saveProduct(product);
        return "Cập nhật sản phẩm thành công";
    }

    @PostMapping("/{id}/delete")
    @ResponseBody
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Xóa sản phẩm thành công";
    }

    @PostMapping("/{id}/add-to-cart")
    @ResponseBody
    public String addToCart(@PathVariable Long id,
                            @RequestParam @Min(value = 1, message = "Số lượng phải lớn hơn 0") Integer quantity) {
        productService.addToCart(id, quantity);
        return "success";
    }
} 