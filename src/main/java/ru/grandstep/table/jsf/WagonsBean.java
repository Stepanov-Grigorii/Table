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
import ru.grandstep.table.model.DriverIntegrationDTO;
import ru.grandstep.table.model.OrderIntegrationDTO;
import ru.grandstep.table.model.WagonIntegrationDTO;

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
public class WagonsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int COUNT_OF_WAGONS_ON_PAGE = 10;
    private long freeWagons;
    private long brokenWagons;
    private SortedSet<WagonIntegrationDTO> wagons = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(WagonIntegrationDTO::getCapacity).thenComparing(WagonIntegrationDTO::getRegistryNumber).reversed()));

    @Inject
    @Push
    private PushContext push;

    public void onNewWagon(@Observes WagonIntegrationDTO newWagon){
        WagonIntegrationDTO oldWagon = getWagonByID(newWagon.getId());
        if(oldWagon != null){
            wagons.remove(oldWagon);
        }
        wagons.add(newWagon);
        push.send("updateWagons");
    }
    public List<WagonIntegrationDTO> getWagons() {
        return wagons.stream().limit(COUNT_OF_WAGONS_ON_PAGE).collect(Collectors.toList());
    }

    @PostConstruct
    public void requestDrivers() {
        HttpGet httpRequest = new HttpGet("http://localhost:8081/TSystemsSchool_1_0_SNAPSHOT_war/api/wagons");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = client.execute(httpRequest)) {
            String wagonsJson = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.name());
            List<WagonIntegrationDTO> wagons = objectMapper.readValue(wagonsJson, new TypeReference<List<WagonIntegrationDTO>>() {
            });
            wagons.forEach(System.out::println);
            this.wagons.addAll(wagons);
            freeWagons = wagons.stream().filter(WagonIntegrationDTO::isOccupied).count();
            brokenWagons = wagons.stream().filter(w -> w.getStatus().equals("Сломан")).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private WagonIntegrationDTO getWagonByID(Integer id) {
        for (WagonIntegrationDTO dto : wagons) {
            if(dto.getId().equals(id)){
                return dto;
            }
        }
        return null;
    }
}
