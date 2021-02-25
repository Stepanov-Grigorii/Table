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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ApplicationScoped
@Data
public class DriversBean {
    private static final long serialVersionUID = 1L;
    private static final int COUNT_OF_DRIVERS_ON_PAGE = 23;
    private long freeDrivers;
    private SortedSet<DriverIntegrationDTO> drivers = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparing(DriverIntegrationDTO::getHoursInCurrentMonth).thenComparing(DriverIntegrationDTO::getIdentityNumber).reversed()));

    @Inject
    @Push
    private PushContext push;

    public void onNewDriver(@Observes DriverIntegrationDTO newDriver){
        DriverIntegrationDTO oldDriver = getDriverByID(newDriver.getId());
        if(oldDriver != null){
            drivers.remove(oldDriver);
        }
        drivers.add(newDriver);
        push.send("updateDrivers");
    }

    public List<DriverIntegrationDTO> getDrivers() {
        return drivers.stream().limit(COUNT_OF_DRIVERS_ON_PAGE).collect(Collectors.toList());
    }

    @PostConstruct
    public void requestDrivers() {
        HttpGet httpRequest = new HttpGet("http://localhost:8081/TSystemsSchool_1_0_SNAPSHOT_war/api/drivers");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = client.execute(httpRequest)) {
            String driversJson = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8.name());
            List<DriverIntegrationDTO> drivers = objectMapper.readValue(driversJson, new TypeReference<List<DriverIntegrationDTO>>() {
            });
            drivers.forEach(System.out::println);
            this.drivers.addAll(drivers);
            freeDrivers = drivers.stream().filter(DriverIntegrationDTO::isOccupied).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private DriverIntegrationDTO getDriverByID(Integer id) {
        for (DriverIntegrationDTO dto : drivers) {
            if(dto.getId().equals(id)){
                return dto;
            }
        }
        return null;
    }
}
