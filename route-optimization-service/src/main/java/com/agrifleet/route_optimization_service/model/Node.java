package com.agrifleet.route_optimization_service.model;

public class Node {
    private final int id;
    private final String name;
    private final double lat;
    private final double lng;

    public Node(int id, String name, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
