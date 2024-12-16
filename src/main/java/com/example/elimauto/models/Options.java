package com.example.elimauto.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "options", catalog = "carbase")
public class Options {
    @Id
    @Column(name = "complectation_id")
    private String complectationId;

    @Column(name="alcantara")
    private String alcantara;

    @Column(name="black-roof")
    private String blackRoof;

    @Column(name="combo-interior")
    private String comboInterior;

    @Column(name="decorative-interior-lighting")
    private String decorativeInteriorLighting;

    @Column(name="door-sill-panel")
    private String doorSillPanel;

    @Column(name="driver-seat-electric")
    private String driverSeatElectric;

    @Column(name="driver-seat-memory")
    private String driverSeatMemory;

    @Column(name="driver-seat-support")
    private String driverSeatSupport;

    @Column(name="driver-seat-updown")
    private String driverSeatUpDown;

    @Column(name="eco-leather")
    private String ecoLeather;

    @Column(name="electro-rear-seat")
    private String electroRearSeat;

    @Column(name="fabric-seats")
    private String fabricSeats;

    @Column(name="folding-front-passenger-seat")
    private String foldingFrontPassengerSeat;

    @Column(name="folding-tables-rear")
    private String foldingTablesRear;

    @Column(name="front-centre-armrest")
    private String frontCentreArmrest;

    @Column(name="front-seat-support")
    private String frontSeatSupport;

    @Column(name="front-seats-heat")
    private String frontSeatsHeat;

    @Column(name="front-seats-heat-vent")
    private String frontSeatsHeatVent;

    @Column(name="hatch")
    private String hatch;

    @Column(name="leather")
    private String leather;

    @Column(name="leather-gear-stick")
    private String leatherGearStick;

    @Column(name="massage-seats")
    private String massageSeats;

    @Column(name="panorama-roof")
    private String panoramaRoof;

    @Column(name="passenger-seat-electric")
    private String passengerSeatElectric;

    @Column(name="passenger-seat-updown")
    private String passengerSeatUpDown;

    @Column(name="rear-seat-heat-vent")
    private String rearSeatHeatVent;

    @Column(name="rear-seat-memory")
    private String rearSeatMemory;

    @Column(name="rear-seats-heat")
    private String rearSeatsHeat;

    @Column(name="roller-blind-for-rear-window")
    private String rollerBlindForRearWindow;

    @Column(name="roller-blinds-for-rear-side-windows")
    private String rollerBlindsForRearSideWindows;

    @Column(name="seat-memory")
    private String seatMemory;

    @Column(name="seat-transformation")
    private String seatTransformation;

    @Column(name="sport-pedals")
    private String sportPedals;

    @Column(name="sport-seats")
    private String sportSeats;

    @Column(name="third-rear-headrest")
    private String thirdRearHeadrest;

    @Column(name="third-row-seats")
    private String thirdRowSeats;

    @Column(name="tinted-glass")
    private String tintedGlass;

    @Column(name="wheel-heat")
    private String wheelHeat;

    @Column(name="wheel-leather")
    private String wheelLeather;

    @Column(name="360-camera")
    private String threeSixtyCamera;

    @Column(name="adj-pedals")
    private String adjPedals;

    @Column(name="ashtray-and-cigarette-lighter")
    private String ashtrayAndCigaretteLighter;

    @Column(name="auto-cruise")
    private String autoCruise;

    @Column(name="auto-mirrors")
    private String autoMirrors;

    @Column(name="auto-park")
    private String autoPark;

    @Column(name="climate-control-1")
    private String climateControlOne;

    @Column(name="climate-control-2")
    private String climateControlTwo;

    @Column(name="computer")
    private String computer;

    @Column(name="condition")
    private String condition;

    @Column(name="cooling-box")
    private String coolingBox;

    @Column(name="cruise-control")
    private String cruiseControl;

    @Column(name="drive-mode-sys")
    private String driveModeSys;

    @Column(name="e-adjustment-wheel")
    private String eAdjustmentWheel;

    @Column(name="easy-trunk-opening")
    private String easyTrunkOpening;

    @Column(name="electro-mirrors")
    private String electroMirrors;

    @Column(name="electro-trunk")
    private String electroTrunk;

    @Column(name="electro-window-back")
    private String electroWindowBack;

    @Column(name="electro-window-front")
    private String electroWindowFront;

    @Column(name="electronic-gage-panel")
    private String electronicGagePanel;

    @Column(name="front-camera")
    private String frontCamera;

    @Column(name="keyless-entry")
    private String keylessEntry;

    @Column(name="multi-wheel")
    private String multiWheel;

    @Column(name="multizone-climate-control")
    private String multizoneClimateControl;

    @Column(name="park-assist-f")
    private String parkAssistF;

    @Column(name="park-assist-r")
    private String parkAssistR;

    @Column(name="power-latching-doors")
    private String powerLatchingDoors;

    @Column(name="programmed-block-heater")
    private String programmedBlockHeater;

    @Column(name="projection-display")
    private String projectionDisplay;

    @Column(name="rear-camera")
    private String rearCamera;

    @Column(name="remote-engine-start")
    private String remoteEngineStart;

    @Column(name="servo")
    private String servo;

    @Column(name="start-button")
    private String startButton;

    @Column(name="start-stop-function")
    private String startStopFunction;

    @Column(name="steering-wheel-gear-shift-paddles")
    private String steeringWheelGearShiftPaddles;

    @Column(name="wheel-configuration1")
    private String wheelConfigurationOne;

    @Column(name="wheel-configuration2")
    private String wheelConfigurationTwo;

    @Column(name="wheel-memory")
    private String wheelMemory;

    @Column(name="wheel-power")
    private String wheelPower;

    @Column(name="adaptive-light")
    private String adaptiveLight;

    @Column(name="automatic-lighting-control")
    private String automaticLightingControl;

    @Column(name="drl")
    private String drl;

    @Column(name="heated-wash-system")
    private String heatedWashSystem;

    @Column(name="high-beam-assist")
    private String highBeamAssist;

    @Column(name="laser-lights")
    private String laserLights;

    @Column(name="led-lights")
    private String ledLights;

    @Column(name="light-cleaner")
    private String lightCleaner;

    @Column(name="light-sensor")
    private String lightSensor;

    @Column(name="mirrors-heat")
    private String mirrorsHeat;

    @Column(name="ptf")
    private String ptf;

    @Column(name="rain-sensor")
    private String rainSensor;

    @Column(name="windcleaner-heat")
    private String windcleanerHeat;

    @Column(name="windscreen-heat")
    private String windscreenHeat;

    @Column(name="xenon")
    private String xenon;

    @Column(name="abs")
    private String abs;

    @Column(name="airbag-curtain")
    private String airbagCurtain;

    @Column(name="airbag-driver")
    private String airbagDriver;

    @Column(name="airbag-passenger")
    private String airbagPassenger;

    @Column(name="airbag-rear-side")
    private String airbagRearSide;

    @Column(name="airbag-side")
    private String airbagSide;

    @Column(name="asr")
    private String asr;

    @Column(name="bas")
    private String bas;

    @Column(name="blind-spot")
    private String blindSpot;

    @Column(name="collision-prevention-assist")
    private String collisionPreventionAssist;

    @Column(name="dha")
    private String dha;

    @Column(name="drowsy-driver-alert-system")
    private String drowsyDriverAlertSystem;

    @Column(name="esp")
    private String esp;

    @Column(name="feedback-alarm")
    private String feedbackAlarm;

    @Column(name="glonass")
    private String glonass;

    @Column(name="hcc")
    private String hcc;

    @Column(name="isofix")
    private String isofix;

    @Column(name="isofix-front")
    private String isofixFront;

    @Column(name="knee-airbag")
    private String kneeAirbag;

    @Column(name="laminated-safety-glass")
    private String laminatedSafetyGlass;

    @Column(name="lane-keeping-assist")
    private String laneKeepingAssist;

    @Column(name="night-vision")
    private String nightVision;

    @Column(name="power-child-locks-rear-doors")
    private String powerChildLocksRearDoors;

    @Column(name="traffic-sign-recognition")
    private String trafficSignRecognition;

    @Column(name="tyre-pressure")
    private String tyrePressure;

    @Column(name="vsm")
    private String vsm;

    @Column(name="alarm")
    private String alarm;

    @Column(name="immo")
    private String immo;

    @Column(name="lock")
    private String lock;

    @Column(name="volume-sensor")
    private String volumeSensor;

    @Column(name="12v-socket")
    private String twelveVSocket;

    @Column(name="220v-socket")
    private String twoTwentyVSocket;

    @Column(name="android-auto")
    private String androidAuto;

    @Column(name="apple-carplay")
    private String appleCarplay;

    @Column(name="audiopreparation")
    private String audioPreparation;

    @Column(name="audiosystem-cd")
    private String audioSystemCD;

    @Column(name="audiosystem-tv")
    private String audioSystemTV;

    @Column(name="aux")
    private String aux;

    @Column(name="bluetooth")
    private String bluetooth;

    @Column(name="entertainment-system-for-rear-seat-passengers")
    private String entertainmentSystemForRearSeatPassengers;

    @Column(name="music-super")
    private String musicSuper;

    @Column(name="navigation")
    private String navigation;

    @Column(name="usb")
    private String usb;

    @Column(name="voice-recognition")
    private String voiceRecognition;

    @Column(name="wireless-charger")
    private String wirelessCharger;

    @Column(name="ya-auto")
    private String yaAuto;

    @Column(name="activ-suspension")
    private String activSuspension;

    @Column(name="air-suspension")
    private String airSuspension;

    @Column(name="reduce-spare-wheel")
    private String reduceSpareWheel;

    @Column(name="spare-wheel")
    private String spareWheel;

    @Column(name="sport-suspension")
    private String sportSuspension;

    @Column(name="14-inch-wheels")
    private String fourteenInchWheels;

    @Column(name="15-inch-wheels")
    private String fifteenInchWheels;

    @Column(name="16-inch-wheels")
    private String sixteenInchWheels;

    @Column(name="17-inch-wheels")
    private String seventeenInchWheels;

    @Column(name="18-inch-wheels")
    private String eighteenInchWheels;

    @Column(name="19-inch-wheels")
    private String nineteenInchWheels;

    @Column(name="20-inch-wheels")
    private String twentyInchWheels;

    @Column(name="21-inch-wheels")
    private String twentyOneInchWheels;

    @Column(name="22-inch-wheels")
    private String twentyTwoInchWheels;

    @Column(name="body-kit")
    private String bodyKit;

    @Column(name="body-mouldings")
    private String bodyMouldings;

    @Column(name="duo-body-color")
    private String duoBodyColor;

    @Column(name="paint-metallic")
    private String paintMetallic;

    @Column(name="roof-rails")
    private String roofRails;

    @Column(name="steel-wheels")
    private String steelWheels;

    @Column(name = "adj-pedal")
    private String adjPedal;

    @Column(name = "feedback-alard")
    private String feedbackAlard;

    @Column(name = "lights-cleaner")
    private String lightsCleaner;

    @OneToOne
    @JoinColumn(name="complectation_id", referencedColumnName="complectation-id", insertable=false, updatable=false)
    private Modification modification;
}
