/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.mycompany.electronmontecarlosimulation3d.stinky.SettingsPP;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author sgershaft
 */
public class SettingsPP {
    // singleton class --> can only have one instance of settings
    // velocity should be beta, in fraction of speed of light
    // to convert to SI units: multiply by C
    // x is m, dx/dt is unitless 
    // ONLY UNITS ARE: volts, eV, and m (if we use beta for velocity)
    // 1/2 m v^2 --> eV is unit

    public static SettingsPP instance = null;

    private double lambda; // in m
    private double lambda_i; // in m 
    private double d; // in m 
    private double voltage; // in volts
    private double Ui; // in electronvolts
    private double Nc; // unitless
    private double Ni; // unitless
//    private double E; // electric field in V/m 
//    private double a_x; // acceleration in x direction in m/s^2
    private int count; // unitless
    private double delta_t; // in seconds

    private SettingsPP() {
    }

    public static SettingsPP getInstance() {
        if (instance == null) {
            instance = new SettingsPP();
        }
        return instance;
    }

    public double getD() {
        return d;
    }

    public double getVoltage() {
        voltage = Ni*Ui / Constants.e;
        return voltage;
    }

    public double getLambda() {
        lambda = d / Nc;
        return lambda;
    }

    public double getLambdaI() {
        lambda_i = d / Ni;
        return lambda_i;
    }

    public int getCount() {
        return count;
    }

    public double getDeltaT() {
        return delta_t;
    }

    public double getNi() {
        return Ni;
    }

    public double getNc() {
        return Nc;
    }

    public double getUI() {
        return Ui;
    }

//    public double getAx() {
//        a_x = (Constants.e * getE()) / Constants.m;
//        return a_x;
//    }

    public void setD(double value) {
        this.d = value;
    }

    public void setUI(double value) {
        this.Ui = value;
    }

    public void setNc(double value) {
        this.Nc = value;
    }
    
    public void setNi(double value) {
        this.Ni = value;
    }

    public void setCount(int value) {
        this.count = value;
    }

    // read settings from json 
    public static void fromJSON(String fileName) throws IOException {
        // get json gson 
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // Automatically close reader so that files don't stay open if broken
        try ( BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // deserialization (reading from file into objects)
            instance = gson.fromJson(reader, SettingsPP.class);
        }
    }
}

/*

    SYSTEM OF UNITS

    3 fundamental variables: d (in meters), V (voltage in system, in volts), Nc, and Ni
    derivative quantities: lambda_c = d/Nc, lambda_i = d/Ni, E = V/d, Ui = e*E*lambda_i (an electric potential energy in eV)

    

 */