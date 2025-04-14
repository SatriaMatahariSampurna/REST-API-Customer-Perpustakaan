package com.example.service;

import com.example.domain.Customer;
import com.example.dto.CustomerData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class yang menangani logika bisnis untuk entitas Customer.
 * Class ini digunakan untuk mengelola data customer seperti mengambil daftar, menambah, menghapus, dan memperbarui data customer.
 */
@ApplicationScoped
public class CustomerService {

    /**
     * Mengambil seluruh data customer dari database dan mengubahnya ke dalam bentuk DTO (CustomerData).
     * @return List dari CustomerData berisi semua data customer.
     */
    @Transactional
    public List<CustomerData> getCustomers() {
        List<Customer> customers = Customer.listAll();
        return customers.stream()
                .map(customer -> new CustomerData(
                        customer.getId(),
                        customer.getName(),
                        customer.getBalance() != null ? customer.getBalance() : BigDecimal.ZERO // Menghindari null pada balance
                ))
                .collect(Collectors.toList());
    }

    /**
     * Menambahkan customer baru ke database menggunakan data dari DTO.
     * @param customerData Data customer yang akan ditambahkan.
     */
    @Transactional
    public void addCustomer(CustomerData customerData) {
        Customer customer = new Customer();
        customer.setName(customerData.name());
        // Jika balance tidak diberikan, di-set ke 0
        customer.setBalance(customerData.balance() != null ? customerData.balance() : BigDecimal.ZERO);
        customer.persist();
    }

    /**
     * Menghapus customer berdasarkan ID.
     * @param id ID customer yang akan dihapus.
     * @return true jika berhasil dihapus, false jika customer tidak ditemukan.
     */
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

    /**
     * Memperbarui data customer berdasarkan ID dengan data baru dari DTO.
     * @param id ID customer yang akan diperbarui.
     * @param customerData Data baru untuk memperbarui customer.
     * @return true jika update berhasil, false jika customer tidak ditemukan.
     */
    @Transactional
    public boolean updateCustomer(Long id, CustomerData customerData) {
        Optional<Customer> customer = Customer.findByIdOptional(id);
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();

            // Perbarui nama jika tidak null dan tidak kosong
            if (customerData.name() != null && !customerData.name().isEmpty()) {
                existingCustomer.setName(customerData.name());
            }

            // Perbarui balance jika tidak null
            if (customerData.balance() != null) {
                existingCustomer.setBalance(customerData.balance());
            }

            // Simpan perubahan ke database
            existingCustomer.persist();
            return true;
        } else {
            return false;  // Customer tidak ditemukan
        }
    }
}
