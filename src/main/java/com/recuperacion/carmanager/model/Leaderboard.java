package com.recuperacion.carmanager.model;

public class Leaderboard {

    private int position;
    private int carId;
    private String carName;
    private String imagePath;
    private int votes;

    public Leaderboard(int position, int carId, String carName, String imagePath, int votes) {
        this.position = position;
        this.carId = carId;
        this.carName = carName;
        this.imagePath = imagePath;
        this.votes = votes;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
