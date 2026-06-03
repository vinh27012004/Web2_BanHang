package ntu.vinh.banhang.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ (10-11 chữ số, bắt đầu bằng 0)")
    @Column(nullable = false, unique = true)
    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Invoice> invoices;

    // Explicit getters and setters for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
} 