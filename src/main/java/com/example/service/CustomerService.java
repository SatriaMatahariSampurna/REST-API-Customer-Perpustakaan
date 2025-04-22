package com.example.service;

import com.example.domain.Customer;
import com.example.dto.CustomerData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public List<CustomerData> getCustomers() {
        List<Customer> customers = Customer.listAll();
        return customers.stream()
                .map(customer -> new CustomerData(
                        customer.getId(),
                        customer.getName(),
                        customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());
    }

    /**
     * Menambahkan customer dengan prosedur SQL `tambah_customer`.
     */
    @Transactional
    public void addCustomerWithProcedure(CustomerData customerData) {
        BigDecimal balance = customerData.balance() != null ? customerData.balance() : BigDecimal.ZERO;
        entityManager.createNativeQuery("CALL tambah_customer(:name, :balance)")
                .setParameter("name", customerData.name())
                .setParameter("balance", balance)
                .executeUpdate();
    }

    /**
     * Menambahkan customer menggunakan fungsi SQL `fungsi_tambah_customer`.
     * @return id customer yang baru ditambahkan.
     */
    @Transactional
    public int addCustomerWithFunction(CustomerData customerData) {
        BigDecimal balance = customerData.balance() != null ? customerData.balance() : BigDecimal.ZERO;
        Object result = entityManager.createNativeQuery("SELECT fungsi_tambah_customer(:name, :balance)")
                .setParameter("name", customerData.name())
                .setParameter("balance", balance)
                .getSingleResult();

        return result != null ? ((Number) result).intValue() : -1;
    }

    @Transactional
    public boolean deleteCustomer(Long id) {
        Optional<Customer> customer = Customer.findByIdOptional(id);
        if (customer.isPresent()) {
            customer.get().delete();
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean updateCustomer(Long id, CustomerData customerData) {
        Optional<Customer> customer = Customer.findByIdOptional(id);
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();
            if (customerData.name() != null && !customerData.name().isEmpty()) {
                existingCustomer.setName(customerData.name());
            }
            if (customerData.balance() != null) {
                existingCustomer.setBalance(customerData.balance());
            }
            existingCustomer.persist();
            return true;
        } else {
            return false;
        }
    }
}