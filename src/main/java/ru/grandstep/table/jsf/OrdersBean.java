package ru.grandstep.table.jsf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Named
@ApplicationScoped
@Data
public class OrdersBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<OrderIntegrationDTO> orders = new CopyOnWriteArrayList<>();

    @Inject
    @Push
    private PushContext push;

    public void onNewOrder(@Observes OrderIntegrationDTO newOrder) {
        boolean replaced = false;
        for (OrderIntegrationDTO dto : orders) {
            if (dto.getId().equals(newOrder.getId())) {
                int i = orders.indexOf(dto);
                orders.set(i, newOrder);
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            orders.add(0, newOrder);
        }
        push.send("updateOrders");
    }

    public List<OrderIntegrationDTO> getOrders() {
        return orders;
    }

    @PostConstruct
    public void requestOrders() {
        HttpGet httpRequest = new HttpGet("http://localhost:8081/TSystemsSchool_1_0_SNAPSHOT_war/api/orders");
        ObjectMapper objectMapper = new ObjectMapper();
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
}
