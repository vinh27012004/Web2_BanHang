package ntu.vinh.banhang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.service.CustomerService;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customer/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/form";
    }

    @PostMapping("/add")
    public String addCustomer(@Valid @ModelAttribute Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "customer/form";
        }
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("customer", customer);
        return "customer/form";
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Long id, @Valid @ModelAttribute Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "customer/form";
        }
        customer.setId(id);
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }

    @GetMapping("/search")
    public String searchCustomers(@RequestParam String phone, Model model) {
        List<Customer> customers = customerService.searchByPhone(phone);
        model.addAttribute("customers", customers);
        return "customer/list";
    }

    @GetMapping("/api/by-phone")
    @ResponseBody
    public org.springframework.http.ResponseEntity<Customer> getCustomerByPhone(@RequestParam String phone) {
        List<Customer> list = customerService.searchByPhone(phone);
        if (!list.isEmpty()) {
            return org.springframework.http.ResponseEntity.ok(list.get(0));
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    @PostMapping("/api/add-quick")
    @ResponseBody
    public org.springframework.http.ResponseEntity<Customer> addCustomerQuick(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam(required = false) String email) {
        try {
            if (phone == null || phone.trim().isEmpty() || name == null || name.trim().isEmpty()) {
                return org.springframework.http.ResponseEntity.badRequest().build();
            }
            
            // Check for duplicate phone number
            List<Customer> existing = customerService.searchByPhone(phone);
            if (!existing.isEmpty()) {
                return org.springframework.http.ResponseEntity.ok(existing.get(0));
            }
            
            Customer customer = new Customer();
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email != null ? email : "");
            Customer saved = customerService.saveCustomer(customer);
            return org.springframework.http.ResponseEntity.ok(saved);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().build();
        }
    }
} 