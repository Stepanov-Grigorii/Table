package ru.grandstep.table.model;

import lombok.Data;

@Data
public class WagonIntegrationDTO {
    private Integer id;
    private String registryNumber;
    private Integer driverNumber;
    private Integer capacity;
    private String brand;
    private String status;
    private String city;
    private boolean occupied;
}
