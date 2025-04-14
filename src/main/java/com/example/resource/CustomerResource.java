package com.example.resource;

import com.example.dto.CustomerData;
import com.example.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Resource class yang menangani permintaan HTTP (REST API) untuk entitas Customer.
 * Berfungsi sebagai jembatan antara client dan service (CustomerService).
 * Semua endpoint berada di path: /customers
 */
@Path("customers")
@Consumes(MediaType.APPLICATION_JSON)  // Menerima data dalam format JSON
@Produces(MediaType.APPLICATION_JSON)  // Mengembalikan data dalam format JSON
public class CustomerResource {

    @Inject
    CustomerService customerservice; // Injeksi service yang akan digunakan untuk operasi bisnis

    /**
     * Endpoint untuk mengambil semua data customer.
     * URL: GET /customers
     * @return List CustomerData dalam format JSON
     */
    @GET
    public List<CustomerData> getCustomers() {
        return customerservice.getCustomers();
    }

    /**
     * Endpoint untuk menambahkan customer baru.
     * URL: POST /customers
     * Body: JSON CustomerData
     * @param customer data customer yang dikirimkan oleh client
     * @return Response status CREATED (201) jika berhasil
     */
    @POST
    public Response addCustomer(CustomerData customer) {
        customerservice.addCustomer(customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    /**
     * Endpoint untuk menghapus customer berdasarkan ID.
     * URL: DELETE /customers/{id}
     * @param id ID customer yang akan dihapus
     * @return Response OK jika berhasil, atau NOT_FOUND jika tidak ditemukan
     */
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        boolean deleted = customerservice.deleteCustomer(id);
        if (deleted) {
            return Response.ok().build(); // Berhasil dihapus
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Customer with ID " + id + " not found")
                           .build(); // Tidak ditemukan
        }
    }

    /**
     * Endpoint untuk memperbarui data customer berdasarkan ID.
     * URL: PUT /customers/{id}
     * Body: JSON CustomerData
     * @param id ID customer yang akan diupdate
     * @param customerData data baru yang dikirimkan
     * @return Response OK jika berhasil, atau NOT_FOUND jika customer tidak ditemukan
     */
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, CustomerData customerData) {
        // Memanggil service untuk update customer
        boolean updated = customerservice.updateCustomer(id, customerData);
        
        // Jika update berhasil
        if (updated) {
            return Response.ok().entity(customerData).build();
        } else {
            // Jika customer dengan ID tidak ditemukan
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Customer with ID " + id + " not found")
                           .build();
        }
    }
}
