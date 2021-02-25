package ru.grandstep.table.model;

import lombok.Data;

import java.util.List;

@Data
public class DriverIntegrationDTO {
    private Integer id;
    private String name;
    private String surname;
    private String login;
    private String email;
    private String identityNumber;
    private Integer hoursInCurrentMonth;
    private String status;
    private String city;
    private String wagon;
    private boolean occupied;
    private List<String> orderNumbers;
    private List<String> driverIdentityNumbers;
}
