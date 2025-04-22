package com.example.resource;

import com.example.dto.ApiResponse; // Import ApiResponse
import com.example.dto.ApiResponseWithData; // Import ApiResponseWithData
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
    CustomerService customerService; // Injeksi service yang akan digunakan untuk operasi bisnis

    /**
     * Endpoint untuk mengambil semua data customer.
     * URL: GET /customers
     * @return List CustomerData dalam format JSON
     */
    @GET
    public Response getCustomers() {
        List<CustomerData> customers = customerService.getCustomers();
        ApiResponseWithData<List<CustomerData>> response = new ApiResponseWithData<>(
            true, "Customer list fetched successfully", customers
        );
        return Response.ok(response).build();
    }

    /**
     * Endpoint untuk menambahkan customer baru menggunakan prosedur.
     * URL: POST /customers/procedure
     * Body: JSON CustomerData
     * @param customer data customer yang dikirimkan oleh client
     * @return Response status OK (200) dan pesan berhasil
     */
    @POST
    @Path("/procedure")
    public Response addCustomerWithProcedure(CustomerData customer) {
        // Memanggil service untuk menambahkan customer menggunakan prosedur
        customerService.addCustomerWithProcedure(customer);

        // Buat response menggunakan ApiResponse dengan status success: true
        ApiResponse response = new ApiResponse(true, "Customer created with procedure");

        // Mengembalikan status 200 OK dan body JSON
        return Response.ok(response).build();
    }

    /**
     * Endpoint untuk menambahkan customer baru menggunakan fungsi.
     * URL: POST /customers/function
     * Body: JSON CustomerData
     * @param customer data customer yang dikirimkan oleh client
     * @return Response status CREATED (201) dan data customer jika berhasil
     */
    @POST
    @Path("/function")
    public Response addCustomerWithFunction(CustomerData customer) {
        // Memanggil service untuk menambahkan customer menggunakan fungsi
        int newId = customerService.addCustomerWithFunction(customer);

        if (newId != -1) {
            // Jika berhasil, buat object CustomerData baru dengan ID hasil dari fungsi
            CustomerData newCustomer = new CustomerData((long) newId, customer.name(), customer.balance());

            // Buat response menggunakan ApiResponseWithData
            ApiResponseWithData<CustomerData> response = new ApiResponseWithData<>(
                true, "Customer created successfully", newCustomer
            );

            return Response.ok(response).build();  // HTTP 200 OK dengan body JSON
        } else {
            // Jika gagal, kembalikan response error menggunakan ApiResponse
            ApiResponse response = new ApiResponse(false, "Failed to create customer.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(response)
                           .build();
        }
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
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            ApiResponse response = new ApiResponse(true, "Customer deleted successfully");
            return Response.ok(response).build(); // Berhasil dihapus
        } else {
            ApiResponse response = new ApiResponse(false, "Customer with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(response)
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
        boolean updated = customerService.updateCustomer(id, customerData);
        
        // Jika update berhasil
        if (updated) {
            ApiResponseWithData<CustomerData> response = new ApiResponseWithData<>(
                true, "Customer updated successfully", customerData
            );
            return Response.ok(response).build();
        } else {
            // Jika customer dengan ID tidak ditemukan
            ApiResponse response = new ApiResponse(false, "Customer with ID " + id + " not found");
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(response)
                           .build();
        }
    }
}
