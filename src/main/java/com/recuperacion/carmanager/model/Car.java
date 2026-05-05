package com.recuperacion.carmanager.model;

import java.time.LocalDate;

public class Car {

    private int id;
    private String brand;
    private String model;
    private int horsePower;
    private String carType;
    private LocalDate registrationDate;
    private String imagePath;

    public Car(int id, String brand, String model, int horsePower, String carType, LocalDate registrationDate, String imagePath) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.horsePower = horsePower;
        this.carType = carType;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
    }

    public Car(String brand, String model, int horsePower, String carType, LocalDate registrationDate, String imagePath) {
        this.brand = brand;
        this.model = model;
        this.horsePower = horsePower;
        this.carType = carType;
        this.registrationDate = registrationDate;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }


    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }


    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFullName() {
        return brand + " " + model;
    }
}