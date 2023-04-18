package com.example.mad_cw;

public class Topic {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Topic(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Add getter and setter for the name field
}