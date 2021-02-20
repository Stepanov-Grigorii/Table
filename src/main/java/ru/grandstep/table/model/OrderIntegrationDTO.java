package ru.grandstep.table.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderIntegrationDTO {
    private Integer id;
    private LocalDateTime time;
    private String orderNumber;
    private String orderStatus;
    private List<String> driverIdentityNumbers;
    private String wagonRegistryNumber;
    private String departure;
    private String destination;
    private String cargoName;
    private String cargoNumber;
}