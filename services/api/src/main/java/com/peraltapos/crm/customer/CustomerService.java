package com.peraltapos.crm.customer;

import com.peraltapos.common.web.BusinessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> list(String search) {
        Sort sort = Sort.by("name").ascending();
        List<Customer> customers = (search == null || search.isBlank())
                ? customerRepository.findAll(sort)
                : customerRepository.findByNameContainingIgnoreCaseOrFiscalIdContainingIgnoreCase(search, search, sort);

        return customers.stream().map(CustomerResponse::from).toList();
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer();
        customer.updateFrom(request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = findEntity(id);
        customer.updateFrom(request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse deactivate(UUID id) {
        Customer customer = findEntity(id);
        customer.deactivate();
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(UUID id) {
        return CustomerResponse.from(findEntity(id));
    }

    private Customer findEntity(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));
    }
}
