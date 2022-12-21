package io.abhishek.employeeaggservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.abhishek.aggservice.OrderAggregate;
import io.abhishek.exceptions.NotFoundException;
import io.abhishek.order.Order;
import io.abhishek.tracker.Tracker;
import io.abhishek.utils.http.ServiceUtility;

@RestController
public class AggService {
    
    private OrderComposer composer;
    private final ServiceUtility utility;
    
    @Autowired
    public AggService(OrderComposer composer, ServiceUtility utility) {
        this.composer = composer;
        this.utility = utility;
    }

    @GetMapping (value = "/order/{orderId}", produces = "application/json")
    public OrderAggregate getOrder (@PathVariable int orderId) {
        Order order = composer.getOrder(orderId);
        if (order == null) {
            throw new NotFoundException("No Employee exists with id: " + orderId);
        }    
        List<Tracker> trackers = composer.getTracker(orderId);
        return createComposer(order, trackers, utility.getServiceAddress());
    }

    private OrderAggregate createComposer(Order order, List<Tracker> trackers, String serviceAddress) {
        int orderId = order.getOrderId();
        String orderName = order.getOrderName();         
        return new OrderAggregate(orderId, orderName, trackers);
    }
  
}
