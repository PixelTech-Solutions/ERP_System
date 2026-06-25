package com.pixeltech.erp.customer;

import com.pixeltech.erp.common.BusinessException;
import com.pixeltech.erp.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer " + id + " not found"));
    }

    @Transactional
    public Customer create(CustomerRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException("A customer with email " + request.getEmail() + " already exists");
        }
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        return repository.save(customer);
    }

    @Transactional
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = findById(id);
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        return repository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer " + id + " not found");
        }
        repository.deleteById(id);
    }
}
