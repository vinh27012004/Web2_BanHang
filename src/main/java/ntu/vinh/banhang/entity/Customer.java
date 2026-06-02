package ntu.vinh.banhang.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

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