package ntu.vinh.banhang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình phục vụ tài nguyên tĩnh.
 * Ảnh sản phẩm nằm trong thư mục src/main/resources/image/ — mặc định Spring Boot
 * không phục vụ thư mục này, nên ta khai báo thủ công để truy cập qua URL /image/**.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**")
                .addResourceLocations("classpath:/image/");
    }
}
