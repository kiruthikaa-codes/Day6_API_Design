package com.tekion.API_Design_Demo.service;

import com.tekion.API_Design_Demo.dto.*;
import com.tekion.API_Design_Demo.dto.ShipmentDTO.ShipmentResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for demo purposes.
 * Provides centralized storage for all entities.
 */
@Component
public class DataStore {

    private final Map<String, ProductDTO> products = new ConcurrentHashMap<>();
    private final Map<String, CustomerDTO> customers = new ConcurrentHashMap<>();
    private final Map<String, OrderDTO> orders = new ConcurrentHashMap<>();
    private final Map<String, AddressDTO> addresses = new ConcurrentHashMap<>();
    private final Map<String, ReviewDTO> reviews = new ConcurrentHashMap<>();
    private final Map<String, PaymentDTO> payments = new ConcurrentHashMap<>();
    private final Map<String, InventoryDTO> inventory = new ConcurrentHashMap<>();
    private final Map<String, FulfillmentDTO> fulfillments = new ConcurrentHashMap<>();
    private final Map<String, ShipmentResponse> shipments = new ConcurrentHashMap<>();

    // Product operations
    public Map<String, ProductDTO> getProducts() {
        return products;
    }

    public ProductDTO getProduct(String productId) {
        return products.get(productId);
    }

    public void saveProduct(ProductDTO product) {
        products.put(product.getProductId(), product);
    }

    public void deleteProduct(String productId) {
        products.remove(productId);
    }

    // Customer operations
    public Map<String, CustomerDTO> getCustomers() {
        return customers;
    }

    public CustomerDTO getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public void saveCustomer(CustomerDTO customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public void deleteCustomer(String customerId) {
        customers.remove(customerId);
    }

    // Order operations
    public Map<String, OrderDTO> getOrders() {
        return orders;
    }

    public OrderDTO getOrder(String orderId) {
        return orders.get(orderId);
    }

    public void saveOrder(OrderDTO order) {
        orders.put(order.getOrderId(), order);
    }

    public void deleteOrder(String orderId) {
        orders.remove(orderId);
    }

    // Address operations
    public Map<String, AddressDTO> getAddresses() {
        return addresses;
    }

    public AddressDTO getAddress(String addressId) {
        return addresses.get(addressId);
    }

    public void saveAddress(AddressDTO address) {
        addresses.put(address.getAddressId(), address);
    }

    public void deleteAddress(String addressId) {
        addresses.remove(addressId);
    }

    // Review operations
    public Map<String, ReviewDTO> getReviews() {
        return reviews;
    }

    public ReviewDTO getReview(String reviewId) {
        return reviews.get(reviewId);
    }

    public void saveReview(ReviewDTO review) {
        reviews.put(review.getReviewId(), review);
    }

    public void deleteReview(String reviewId) {
        reviews.remove(reviewId);
    }

    // Payment operations
    public Map<String, PaymentDTO> getPayments() {
        return payments;
    }

    public PaymentDTO getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    public void savePayment(PaymentDTO payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    // Inventory operations
    public Map<String, InventoryDTO> getInventory() {
        return inventory;
    }

    public InventoryDTO getInventoryItem(String inventoryId) {
        return inventory.get(inventoryId);
    }

    public void saveInventory(InventoryDTO inv) {
        inventory.put(inv.getId(), inv);
    }

    public void deleteInventory(String inventoryId) {
        inventory.remove(inventoryId);
    }

    // Fulfillment operations
    public Map<String, FulfillmentDTO> getFulfillments() {
        return fulfillments;
    }

    public FulfillmentDTO getFulfillment(String fulfillmentId) {
        return fulfillments.get(fulfillmentId);
    }

    public void saveFulfillment(FulfillmentDTO fulfillment) {
        fulfillments.put(fulfillment.getFulfillmentId(), fulfillment);
    }

    public void deleteFulfillment(String fulfillmentId) {
        fulfillments.remove(fulfillmentId);
    }

    // Shipment operations
    public Map<String, ShipmentResponse> getShipments() {
        return shipments;
    }

    public ShipmentResponse getShipment(String shipmentId) {
        return shipments.get(shipmentId);
    }

    public void saveShipment(ShipmentResponse shipment) {
        shipments.put(shipment.getShipmentId(), shipment);
    }

    public void deleteShipment(String shipmentId) {
        shipments.remove(shipmentId);
    }
}

