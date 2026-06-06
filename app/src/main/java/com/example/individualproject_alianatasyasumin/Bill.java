package com.example.individualproject_alianatasyasumin;

public class Bill {
    private int id;
    private String month;
    private double units;
    private double rebate;
    private double totalCharges;
    private double finalCost;

    // Empty constructor
    public Bill() {}

    // Full constructor
    public Bill(int id, String month, double units, double rebate, double totalCharges, double finalCost) {
        this.id = id;
        this.month = month;
        this.units = units;
        this.rebate = rebate;
        this.totalCharges = totalCharges;
        this.finalCost = finalCost;
    }

    // Getters
    public int getId() { return id; }
    public String getMonth() { return month; }
    public double getUnits() { return units; }
    public double getRebate() { return rebate; }
    public double getTotalCharges() { return totalCharges; }
    public double getFinalCost() { return finalCost; }
}