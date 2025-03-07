package com.ghoul.AmanaFund.entity;

public class PersonDTO {
    private Long idGarantie;
    private String CIN;
    private String name;
    private String last_name;
    private int age;
    private Double revenue;
    private Long policeId; // Include the policeId

    // Constructors
    public PersonDTO() {
    }

    public PersonDTO(Person person) {
        this.idGarantie = person.getIdGarantie();
        this.CIN = person.getCIN();
        this.name = person.getName();
        this.last_name = person.getLast_name();
        this.age = person.getAge();
        this.revenue = person.getRevenue();
        this.policeId = person.getPolice().getIdPolice();
    }

    // Getters and setters
    public Long getIdGarantie() {
        return idGarantie;
    }

    public void setIdGarantie(Long idGarantie) {
        this.idGarantie = idGarantie;
    }

    // ... (Getters and setters for other fields) ...

    public Long getPoliceId() {
        return policeId;
    }

    public void setPoliceId(Long policeId) {
        this.policeId = policeId;
    }
}