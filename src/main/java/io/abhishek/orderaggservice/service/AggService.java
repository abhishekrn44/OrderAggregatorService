package io.abhishek.orderaggservice.service;

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
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SecurityRequirement(name = "security_auth")
@Tag(name = "Order Aggregator", description = "RESTful API for Order information.")
public class AggService {

    private OrderComposer composer;
    private final ServiceUtility utility;

    @Autowired
    public AggService(OrderComposer composer, ServiceUtility utility) {
        this.composer = composer;
        this.utility = utility;
    }

    @Retry(name = "order-agg-service")
    @TimeLimiter(name = "order-agg-service")
    @GetMapping(value = "/order/{orderId}", produces = "application/json")
    @CircuitBreaker(name = "order-agg-service", fallbackMethod = "getOrderFallbackValue")
    @Operation(summary = "${api.product-composite.get-composite-product.description}", description = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
            @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
            @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
            @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
    })
    public OrderAggregate getOrder(@PathVariable int orderId) {
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

    private Order getProductFallbackValue(int orderId, CallNotPermittedException ex) {

        log.warn("Creating a fail-fast fallback product for productId = {} and exception = {} ",
                orderId, ex.toString());

        if (orderId == 13) {
            String errMsg = "Order Id: " + orderId + " not found in fallback cache!";
            log.warn(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new Order(orderId, "Fallback product");
    }

}