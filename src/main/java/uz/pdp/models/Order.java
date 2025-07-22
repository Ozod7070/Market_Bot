package uz.pdp.models;



import uz.pdp.enums.OrderStatus;


public class Order {
    private final Long id;
    private final Long orderId;
    private final String productName;
    private final String orderDate;
    private final OrderStatus status;

    public Order(Long id, Long orderId, String productName, String orderDate, OrderStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.productName = productName;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
