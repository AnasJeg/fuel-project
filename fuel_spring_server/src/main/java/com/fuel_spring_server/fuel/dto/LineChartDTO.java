package com.fuel_spring_server.fuel.dto;

import java.util.Date;
import java.util.Map;


public class LineChartDTO {
    private String type;
    private double litres;
    private double totale;
    private Date date;

    public LineChartDTO(String type, double litres, double totale, Date date) {
        this.type = type;
        this.litres = litres;
        this.totale = totale;
        this.date = date;
    }

    public LineChartDTO(Map<String, Object> tuple) {
        this.type = (String) tuple.get("type");
        this.litres = (Double) tuple.get("litres");
        this.totale = (Double) tuple.get("totale");
        this.date = (Date) tuple.get("date");
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
