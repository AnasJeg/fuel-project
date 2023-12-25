package com.fuel_spring_server.fuel.dto;

import java.util.Map;

public class PieChartDTO {
    private String type;
    private double litres;
    private double totale;

    public PieChartDTO(String type, double litres, double totale) {
        this.type = type;
        this.litres = litres;
        this.totale = totale;
    }
    public PieChartDTO(Map<String, Object> tuple) {
        this.type = (String) tuple.get("type");
        this.litres = (Double) tuple.get("litres");
        this.totale = (Double) tuple.get("totale");
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public double getTotale() {
        return totale;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }
}
