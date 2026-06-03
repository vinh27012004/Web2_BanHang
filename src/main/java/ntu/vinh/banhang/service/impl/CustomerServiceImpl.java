package ntu.vinh.banhang.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ntu.vinh.banhang.entity.Customer;
import ntu.vinh.banhang.exception.ResourceNotFoundException;
import ntu.vinh.banhang.repository.CustomerRepository;
import ntu.vinh.banhang.service.CustomerService;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + id));
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public List<Customer> searchByPhone(String phone) {
        return customerRepository.findByPhone(phone)
            .map(List::of)
            .orElse(List.of());
    }
} 