package uz.pdp.service;

import uz.pdp.enums.OrderStatus;
import uz.pdp.models.Order;
import uz.pdp.models.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private  final Map<Long, List<Order>> userOrders = new ConcurrentHashMap<>();

    private Long nextOrderId = 1L;

    public Order createOrder(Long userId, Product product){

        Order order = new Order(
                nextOrderId ++,
                userId,
                product.getName(),
                new Date().toString(),
                OrderStatus.PENDING

        );
        userOrders.computeIfAbsent(userId,k->new ArrayList<>()).add(order);
        return order;
    }

    public List<Order> getUserOrders(Long userId){
        return userOrders.getOrDefault(userId,new ArrayList<>());
    }
}
