package ntu.vinh.banhang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh + trang đăng nhập: ai cũng truy cập được
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/image/**").permitAll()
                // Xem danh sách / chi tiết sản phẩm: công khai
                .requestMatchers(HttpMethod.GET, "/products", "/products/*").permitAll()
                // Giỏ hàng (bao gồm thêm vào giỏ qua /products/{id}/add-to-cart): công khai
                .requestMatchers("/cart/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/products/*/add-to-cart").permitAll()
                // Tạo / sửa / xóa sản phẩm và quản lý kho: chỉ ADMIN
                .requestMatchers(HttpMethod.POST, "/products/add", "/products/*/update", "/products/*/delete").hasRole("ADMIN")
                .requestMatchers("/admin/**", "/stock/**").hasRole("ADMIN")
                // Hóa đơn & khách hàng: STAFF hoặc ADMIN
                .requestMatchers("/order/**", "/customers/**").hasAnyRole("ADMIN", "STAFF")
                // Còn lại yêu cầu đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 