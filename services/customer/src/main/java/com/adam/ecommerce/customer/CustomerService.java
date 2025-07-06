package com.adam.ecommerce.customer;

import com.adam.ecommerce.exception.CustomerNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor

public class CustomerService {

    private final CustomerRepository repository ;
    private final CustomerMapper mapper;
    public String createCustomer(@Valid CustomerRequest request) {
        var customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(@Valid CustomerRequest request) {
        var customer = repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                    format("Customer with id %s not found", request.id())
                ));
        mergerCustomer(customer, request);
        repository.save(customer);
    }

    private void mergerCustomer(Customer customer, @Valid CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstName()))
            customer.setFirstName(request.firstName());
        if (StringUtils.isNotBlank(request.lastName()))
            customer.setLastName(request.lastName());
        if (StringUtils.isNotBlank(request.email()))
            customer.setEmail(request.email());
        if (request.adress() != null)
            customer.setAddress(request.adress());
    }

    public List<CustomerResponse> findAllCustomers() {
        return repository
                .findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public CustomerResponse findById(String customerId) {
        return repository.findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(
                        format("Customer with id %s not found", customerId)
                ));
    }

    public void deleteCustomer(String customerId) {
        repository.deleteById(customerId);

    }
}
