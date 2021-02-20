package ru.grandstep.table.jsf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.grandstep.table.model.OrderIntegrationDTO;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Named
@ApplicationScoped
@Data
public class OrdersBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int COUNT_OF_ORDERS_ON_PAGE = 3;
    private SortedSet<OrderIntegrationDTO> orders = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(OrderIntegrationDTO::getTime).reversed()));

    @Inject
    @Push
    private PushContext push;

    public void onNewOrder(@Observes OrderIntegrationDTO newOrder) {
        OrderIntegrationDTO oldOrder = getOrderByID(newOrder.getId());
        if(oldOrder != null){
            orders.remove(oldOrder);
        }
        orders.add(newOrder);
        push.send("updateOrders");
    }


    public List<OrderIntegrationDTO> getOrders() {
        return orders.stream().limit(COUNT_OF_ORDERS_ON_PAGE).collect(Collectors.toList());
    }

    @PostConstruct
    public void requestOrders() {
        HttpGet httpRequest = new HttpGet("http://localhost:8081/TSystemsSchool_1_0_SNAPSHOT_war/api/orders");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = client.execute(httpRequest)) {
            String ordersJson = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.name());
            List<OrderIntegrationDTO> orders = objectMapper.readValue(ordersJson, new TypeReference<List<OrderIntegrationDTO>>() {
            });
            orders.forEach(System.out::println);
            this.orders.addAll(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OrderIntegrationDTO getOrderByID(Integer id) {
        for (OrderIntegrationDTO dto : orders) {
            if(dto.getId().equals(id)){
                return dto;
            }
        }
        return null;
    }
}
