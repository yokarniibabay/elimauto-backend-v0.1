package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "specifications", catalog = "carbase")
public class Specifications {
    @Id
    @Column(name = "complectation_id")
    private String complectationId;

    @Column(name = "back-brake")
    private String backBrake;

    @Column(name = "feeding")
    private String feeding;

    @Column(name = "horse-power")
    private String horsePower;

    @Column(name = "kvt-power")
    private String kvtPower;

    @Column(name = "rpm-power")
    private String rpmPower;

    @Column(name = "engine-type")
    private String engineType;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "drive")
    private String drive;

    @Column(name = "volume")
    private String volume;

    @Column(name = "time-to-100")
    private String timeToHundred;

    @Column(name = "cylinders-order")
    private String cylindersOrder;

    @Column(name = "max-speed")
    private String maxSpeed;

    @Column(name = "compression")
    private String compression;

    @Column(name = "cylinders-value")
    private String cylindersValue;

    @Column(name = "diametr")
    private String diameter;

    @Column(name = "piston-stroke")
    private String pistonStroke;

    @Column(name = "engine-feeding")
    private String engineFeeding;

    @Column(name = "engine-order")
    private String engineOrder;

    @Column(name = "gear-value")
    private String gearValue;

    @Column(name = "moment")
    private String moment;

    @Column(name = "petrol-type")
    private String petrolType;

    @Column(name = "valves")
    private String valves;

    @Column(name = "weight")
    private String weight;

    @Column(name = "wheel-size")
    private String wheelSize;

    @Column(name = "wheel-base")
    private String wheelBase;

    @Column(name = "front-wheel-base")
    private String frontWheelBase;

    @Column(name = "back-wheel-base")
    private String backWheelBase;

    @Column(name = "front-brake")
    private String frontBrake;

    @Column(name = "front-suspension")
    private String frontSuspension;

    @Column(name = "back-suspension")
    private String backSuspension;

    @Column(name = "height")
    private String height;

    @Column(name = "width")
    private String width;

    @Column(name = "fuel-tank-capacity")
    private String fuelTankCapacity;

    @Column(name = "seats")
    private String seats;

    @Column(name = "length")
    private String length;

    @Column(name = "emission-euro-class")
    private String emissionEuroClass;

    @Column(name = "volume-litres")
    private String volumeLitres;

    @Column(name = "consumption-mixed")
    private String consumptionMixed;

    @Column(name = "clearance")
    private String clearance;

    @Column(name = "trunks-min-capacity")
    private String trunksMinCapacity;

    @Column(name = "trunks-max-capacity")
    private String trunksMaxCapacity;

    @Column(name = "consumption-hiway")
    private String consumptionHighway;

    @Column(name = "consumption-city")
    private String consumptionCity;

    @Column(name = "moment-rpm")
    private String momentRpm;

    @Column(name = "full-weight")
    private String fullWeight;

    @Column(name = "range-distance")
    private String rangeDistance;

    @Column(name = "battery-capacity")
    private String batteryCapacity;

    @Column(name = "fuel-emission")
    private String fuelEmission;

    @Column(name = "electric-range")
    private String electricRange;

    @Column(name = "charge-time")
    private String chargeTime;

    @Column(name = "safety-rating")
    private String safetyRating;

    @Column(name = "safety-grade")
    private String safetyGrade;

    @OneToOne
    @JoinColumn(name="complectation-id", referencedColumnName="complectation-id", insertable=false, updatable=false)
    private Modification modification;
}
