package io.abhishek.employeeaggservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.abhishek.exceptions.InvalidInputException;
import io.abhishek.exceptions.NotFoundException;
import io.abhishek.order.Order;
import io.abhishek.tracker.Tracker;
import io.abhishek.utils.http.HttpErrorInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component  
public class OrderComposer {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String orderServiceUrl;
    private final String trackerServiceUrl;
    
    @Autowired
    public OrderComposer(RestTemplate restTemplate, ObjectMapper mapper  
/*    		@Value("${app.order-service.host}") String orderServiceHost,
    		@Value("${app.order-service.port}") String orderServicePort,
            @Value("${app.tracker-service.host}") String trackerServiceHost,
    	    @Value("${app.tracker-service.port}") String trackerServicePort */) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.orderServiceUrl = "http://" + "order" + ":" + 8080 + "/orderservice/";
        this.trackerServiceUrl = "http://" + "tracker" + ":" + 8080 + "/tracker?orderId=";
    }
    
    
    public Order getOrder (int id) {
        
        String url = orderServiceUrl + id;
        log.debug("Calling getorder API on URL: {}", url);
        
        try {
            Order order = restTemplate.getForObject(url, Order.class);
            log.debug("order with id: {}", order.getOrderId());
            return order;
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(e));
                    
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(e));
                    
                default:
                    log.warn("Unexpected HTTP error: {}", e);
                    log.warn("Error Body: {}", e.getResponseBodyAsString());
                    throw e;
            }
        }
    }
    
    public List<Tracker> getTracker (int id) {
        
        String url = trackerServiceUrl + id;
        log.debug("Calling getTracker API on URL: {}", url);
        
        try {
            List<Tracker> trackers = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference <List<Tracker>> (){}).getBody();
            log.debug("Found {} tracking details for order with id: {}", trackers.size(), id);
            return trackers;
        } catch (Exception e) {
            log.warn("Exception occured {} ", e.getMessage());
            return new ArrayList<>();
        }    
    }
    
    private String getErrorMessage (HttpClientErrorException e) {
        try {
            return mapper.readValue(e.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }
    
}