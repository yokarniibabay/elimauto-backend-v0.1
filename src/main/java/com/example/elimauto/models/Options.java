package com.example.elimauto.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Integer alcantara;

    @Column(name="black-roof")
    private Integer blackRoof;

    @Column(name="combo-interior")
    private Integer comboInterior;

    @Column(name="decorative-interior-lighting")
    private Integer decorativeInteriorLighting;

    @Column(name="door-sill-panel")
    private Integer doorSillPanel;

    @Column(name="driver-seat-electric")
    private Integer driverSeatElectric;

    @Column(name="driver-seat-memory")
    private Integer driverSeatMemory;

    @Column(name="driver-seat-support")
    private Integer driverSeatSupport;

    @Column(name="driver-seat-updown")
    private Integer driverSeatUpDown;

    @Column(name="eco-leather")
    private Integer ecoLeather;

    @Column(name="electro-rear-seat")
    private Integer electroRearSeat;

    @Column(name="fabric-seats")
    private Integer fabricSeats;

    @Column(name="folding-front-passenger-seat")
    private Integer foldingFrontPassengerSeat;

    @Column(name="folding-tables-rear")
    private Integer foldingTablesRear;

    @Column(name="front-centre-armrest")
    private Integer frontCentreArmrest;

    @Column(name="front-seat-support")
    private Integer frontSeatSupport;

    @Column(name="front-seats-heat")
    private Integer frontSeatsHeat;

    @Column(name="front-seats-heat-vent")
    private Integer frontSeatsHeatVent;

    @Column(name="hatch")
    private Integer hatch;

    @Column(name="leather")
    private Integer leather;

    @Column(name="leather-gear-stick")
    private Integer leatherGearStick;

    @Column(name="massage-seats")
    private Integer massageSeats;

    @Column(name="panorama-roof")
    private Integer panoramaRoof;

    @Column(name="passenger-seat-electric")
    private Integer passengerSeatElectric;

    @Column(name="passenger-seat-updown")
    private Integer passengerSeatUpDown;

    @Column(name="rear-seat-heat-vent")
    private Integer rearSeatHeatVent;

    @Column(name="rear-seat-memory")
    private Integer rearSeatMemory;

    @Column(name="rear-seats-heat")
    private Integer rearSeatsHeat;

    @Column(name="roller-blind-for-rear-window")
    private Integer rollerBlindForRearWindow;

    @Column(name="roller-blinds-for-rear-side-windows")
    private Integer rollerBlindsForRearSideWindows;

    @Column(name="seat-memory")
    private Integer seatMemory;

    @Column(name="seat-transformation")
    private Integer seatTransformation;

    @Column(name="sport-pedals")
    private Integer sportPedals;

    @Column(name="sport-seats")
    private Integer sportSeats;

    @Column(name="third-rear-headrest")
    private Integer thirdRearHeadrest;

    @Column(name="third-row-seats")
    private Integer thirdRowSeats;

    @Column(name="tinted-glass")
    private Integer tintedGlass;

    @Column(name="wheel-heat")
    private Integer wheelHeat;

    @Column(name="wheel-leather")
    private Integer wheelLeather;

    @Column(name="360-camera")
    private Integer threeSixtyCamera;

    @Column(name="adj-pedals")
    private Integer adjPedals;

    @Column(name="ashtray-and-cigarette-lighter")
    private Integer ashtrayAndCigaretteLighter;

    @Column(name="auto-cruise")
    private Integer autoCruise;

    @Column(name="auto-mirrors")
    private Integer autoMirrors;

    @Column(name="auto-park")
    private Integer autoPark;

    @Column(name="climate-control-1")
    private Integer climateControlOne;

    @Column(name="climate-control-2")
    private Integer climateControlTwo;

    @Column(name="computer")
    private Integer computer;

    @Column(name="condition")
    private Integer condition;

    @Column(name="cooling-box")
    private Integer coolingBox;

    @Column(name="cruise-control")
    private Integer cruiseControl;

    @Column(name="drive-mode-sys")
    private Integer driveModeSys;

    @Column(name="e-adjustment-wheel")
    private Integer eAdjustmentWheel;

    @Column(name="easy-trunk-opening")
    private Integer easyTrunkOpening;

    @Column(name="electro-mirrors")
    private Integer electroMirrors;

    @Column(name="electro-trunk")
    private Integer electroTrunk;

    @Column(name="electro-window-back")
    private Integer electroWindowBack;

    @Column(name="electro-window-front")
    private Integer electroWindowFront;

    @Column(name="electronic-gage-panel")
    private Integer electronicGagePanel;

    @Column(name="front-camera")
    private Integer frontCamera;

    @Column(name="keyless-entry")
    private Integer keylessEntry;

    @Column(name="multi-wheel")
    private Integer multiWheel;

    @Column(name="multizone-climate-control")
    private Integer multizoneClimateControl;

    @Column(name="park-assist-f")
    private Integer parkAssistF;

    @Column(name="park-assist-r")
    private Integer parkAssistR;

    @Column(name="power-latching-doors")
    private Integer powerLatchingDoors;

    @Column(name="programmed-block-heater")
    private Integer programmedBlockHeater;

    @Column(name="projection-display")
    private Integer projectionDisplay;

    @Column(name="rear-camera")
    private Integer rearCamera;

    @Column(name="remote-engine-start")
    private Integer remoteEngineStart;

    @Column(name="servo")
    private Integer servo;

    @Column(name="start-button")
    private Integer startButton;

    @Column(name="start-stop-function")
    private Integer startStopFunction;

    @Column(name="steering-wheel-gear-shift-paddles")
    private Integer steeringWheelGearShiftPaddles;

    @Column(name="wheel-configuration1")
    private Integer wheelConfigurationOne;

    @Column(name="wheel-configuration2")
    private Integer wheelConfigurationTwo;

    @Column(name="wheel-memory")
    private Integer wheelMemory;

    @Column(name="wheel-power")
    private Integer wheelPower;

    @Column(name="adaptive-light")
    private Integer adaptiveLight;

    @Column(name="automatic-lighting-control")
    private Integer automaticLightingControl;

    @Column(name="drl")
    private Integer drl;

    @Column(name="heated-wash-system")
    private Integer heatedWashSystem;

    @Column(name="high-beam-assist")
    private Integer highBeamAssist;

    @Column(name="laser-lights")
    private Integer laserLights;

    @Column(name="led-lights")
    private Integer ledLights;

    @Column(name="light-cleaner")
    private Integer lightCleaner;

    @Column(name="light-sensor")
    private Integer lightSensor;

    @Column(name="mirrors-heat")
    private Integer mirrorsHeat;

    @Column(name="ptf")
    private Integer ptf;

    @Column(name="rain-sensor")
    private Integer rainSensor;

    @Column(name="windcleaner-heat")
    private Integer windcleanerHeat;

    @Column(name="windscreen-heat")
    private Integer windscreenHeat;

    @Column(name="xenon")
    private Integer xenon;

    @Column(name="abs")
    private Integer abs;

    @Column(name="airbag-curtain")
    private Integer airbagCurtain;

    @Column(name="airbag-driver")
    private Integer airbagDriver;

    @Column(name="airbag-passenger")
    private Integer airbagPassenger;

    @Column(name="airbag-rear-side")
    private Integer airbagRearSide;

    @Column(name="airbag-side")
    private Integer airbagSide;

    @Column(name="asr")
    private Integer asr;

    @Column(name="bas")
    private Integer bas;

    @Column(name="blind-spot")
    private Integer blindSpot;

    @Column(name="collision-prevention-assist")
    private Integer collisionPreventionAssist;

    @Column(name="dha")
    private Integer dha;

    @Column(name="drowsy-driver-alert-system")
    private Integer drowsyDriverAlertSystem;

    @Column(name="esp")
    private Integer esp;

    @Column(name="feedback-alarm")
    private Integer feedbackAlarm;

    @Column(name="glonass")
    private Integer glonass;

    @Column(name="hcc")
    private Integer hcc;

    @Column(name="isofix")
    private Integer isofix;

    @Column(name="isofix-front")
    private Integer isofixFront;

    @Column(name="knee-airbag")
    private Integer kneeAirbag;

    @Column(name="laminated-safety-glass")
    private Integer laminatedSafetyGlass;

    @Column(name="lane-keeping-assist")
    private Integer laneKeepingAssist;

    @Column(name="night-vision")
    private Integer nightVision;

    @Column(name="power-child-locks-rear-doors")
    private Integer powerChildLocksRearDoors;

    @Column(name="traffic-sign-recognition")
    private Integer trafficSignRecognition;

    @Column(name="tyre-pressure")
    private Integer tyrePressure;

    @Column(name="vsm")
    private Integer vsm;

    @Column(name="alarm")
    private Integer alarm;

    @Column(name="immo")
    private Integer immo;

    @Column(name="lock")
    private Integer lock;

    @Column(name="volume-sensor")
    private Integer volumeSensor;

    @Column(name="12v-socket")
    private Integer twelveVSocket;

    @Column(name="220v-socket")
    private Integer twoTwentyVSocket;

    @Column(name="android-auto")
    private Integer androidAuto;

    @Column(name="apple-carplay")
    private Integer appleCarplay;

    @Column(name="audiopreparation")
    private Integer audioPreparation;

    @Column(name="audiosystem-cd")
    private Integer audioSystemCD;

    @Column(name="audiosystem-tv")
    private Integer audioSystemTV;

    @Column(name="aux")
    private Integer aux;

    @Column(name="bluetooth")
    private Integer bluetooth;

    @Column(name="entertainment-system-for-rear-seat-passengers")
    private Integer entertainmentSystemForRearSeatPassengers;

    @Column(name="music-super")
    private Integer musicSuper;

    @Column(name="navigation")
    private Integer navigation;

    @Column(name="usb")
    private Integer usb;

    @Column(name="voice-recognition")
    private Integer voiceRecognition;

    @Column(name="wireless-charger")
    private Integer wirelessCharger;

    @Column(name="ya-auto")
    private Integer yaAuto;

    @Column(name="activ-suspension")
    private Integer activSuspension;

    @Column(name="air-suspension")
    private Integer airSuspension;

    @Column(name="reduce-spare-wheel")
    private Integer reduceSpareWheel;

    @Column(name="spare-wheel")
    private Integer spareWheel;

    @Column(name="sport-suspension")
    private Integer sportSuspension;

    @Column(name="14-inch-wheels")
    private Integer fourteenInchWheels;

    @Column(name="15-inch-wheels")
    private Integer fifteenInchWheels;

    @Column(name="16-inch-wheels")
    private Integer sixteenInchWheels;

    @Column(name="17-inch-wheels")
    private Integer seventeenInchWheels;

    @Column(name="18-inch-wheels")
    private Integer eighteenInchWheels;

    @Column(name="19-inch-wheels")
    private Integer nineteenInchWheels;

    @Column(name="20-inch-wheels")
    private Integer twentyInchWheels;

    @Column(name="21-inch-wheels")
    private Integer twentyOneInchWheels;

    @Column(name="22-inch-wheels")
    private Integer twentyTwoInchWheels;

    @Column(name="body-kit")
    private Integer bodyKit;

    @Column(name="body-mouldings")
    private Integer bodyMouldings;

    @Column(name="duo-body-color")
    private Integer duoBodyColor;

    @Column(name="paint-metallic")
    private Integer paintMetallic;

    @Column(name="roof-rails")
    private Integer roofRails;

    @Column(name="steel-wheels")
    private Integer steelWheels;

    @Column(name = "adj-pedal")
    private Integer adjPedal;

    @Column(name = "feedback-alard")
    private Integer feedbackAlard;

    @Column(name = "lights-cleaner")
    private Integer lightsCleaner;

    @OneToOne
    @JoinColumn(name="complectation_id", referencedColumnName="complectation_id", insertable=false, updatable=false)
    @JsonBackReference
    private Modification modification;
}
