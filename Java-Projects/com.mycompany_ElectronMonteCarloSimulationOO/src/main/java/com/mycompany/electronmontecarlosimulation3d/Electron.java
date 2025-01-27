//@@ -0,0 +1,212 @@
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.electronmontecarlosimulation3d;

import java.util.ArrayList;
import org.apache.commons.math3.analysis.function.Asinh;

/**
 *
 * @author sgershaft
 */
public class Electron {

    Vector position; // in meters or is it cm ??
    Vector velocity; // number, fraction of C (just like beta)
    IGeometry geometry;

//    boolean showPath;// showPath enables printing positions of collisions TO DO
    public static final double e = 1.0; // 1 electron charge (charge of electron)
    public static final double m = 511000; // in eV bc m_e = 511,000 eV
    public Vector Efield;
    public double E;
    public double delta_t;

    public Electron(Vector startPosition, Vector startVelocity, IGeometry geometry) {
        this.position = startPosition;
        this.velocity = startVelocity;
        this.geometry = geometry;
        this.delta_t = geometry.getDeltaT();
    }

    public void printPosVel() {
//        System.out.format("position: %12.8f %12.8f %12.8f    velocity: %12.8f %12.8f %12.8f \n", position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
    }

    public double travel(double sTarget) {
        printPosVel();
        // save old things
        Vector p0 = position;
        double E0 = -geometry.getEfield(position).getNorm();
        double K_i = 0.5 * this.m * velocity.Square();

        double s0 = 0;
        double s1 = 0;
        
        double scalar = (-1.0 / m) * delta_t;
        // Euler's method
        while (s0 < sTarget) {
            // System.out.format("path: %10.5f \n", path);
            // increment position and velocity
            // Electric field: find and get magnitude (norm)
            Efield = geometry.getEfield(position);
            // System.out.format("Efield: %12.8f %12.8f %12.8f \n", Efield.x, Efield.y, Efield.z);
            Vector deltaPosition = velocity.multiplyByScalar(delta_t);
            double scalarDeltaPosition = deltaPosition.getNorm();
            // System.out.format("delta: %10.5f \n", scalarDeltaPosition);
            Vector deltaVelocity = Efield.multiplyByScalar(scalar);
            s1 += scalarDeltaPosition;
            
            if (s1 > sTarget) {
                break;
            }
            
            s0 = s1;
            position = position.addVectors(deltaPosition);
            velocity = velocity.addVectors(deltaVelocity);
            printPosVel();

        }

        // last step --> correct deltaPosition
        // correct by decreasing s by rest of path over distance computed
        double dtLast = (sTarget - s0) / (s1 - s0) * delta_t;
        Vector deltaPosition = velocity.multiplyByScalar(dtLast);
        double scalarDeltaPosition = deltaPosition.getNorm();
        Efield = geometry.getEfield(position);
        Vector deltaVelocity = Efield.multiplyByScalar((e / m) * dtLast);

        position = position.addVectors(deltaPosition);
        velocity = velocity.addVectors(deltaVelocity);
        printPosVel();

        // find total energy
        Vector p1 = this.position;
        // Electric field
        Efield = geometry.getEfield(position);
        double E1 = -Efield.getNorm();

        // should be diff between U when working in diff geometry
        // double deltaU = e * SettingsFresh.getInstance().getE() * (x1 - x0);
        double K_f = 0.5 * m * this.velocity.Square();
        double delta_energy = ((E1 + K_f) - (E0 + K_i));

//        System.out.format("K_i: %.3f, K_f: %.3f, deltaU: %.3f \n", K_i, K_f, deltaU);
//        System.out.println("delta energy (should be ~0)" + delta_energy);
        // NEW!!!
        // return a delta_energy for RMS
        return delta_energy;
    }

    public double setNewPositionsV2(double s) {

        printPosVel();
        // save old things
        double x0 = position.x;
        double K_i = 0.5 * this.m * velocity.Square();

        double path = 0;
        // Euler's method
        while (path < s) {
//           System.out.format("path: %10.5f \n", path);
            // increment position and velocity
            Vector deltaPosition = velocity.multiplyByScalar(delta_t);
            double scalarDeltaPosition = deltaPosition.getNorm();
//            System.out.format("delta: %10.5f \n", scalarDeltaPosition);
            double correctionFactor = 1;
            // if next step will overshoot (last step of Euler's method)
            if (path + scalarDeltaPosition > s) {
                // last step --> correct deltaPosition
                // correct by decreasing s by rest of path over distance computed
                correctionFactor = (s - path) / scalarDeltaPosition;
                deltaPosition = deltaPosition.multiplyByScalar(correctionFactor);
            }
            path += scalarDeltaPosition;

            // Electric field: find and get magnitude (norm)
            Efield = geometry.getEfield(position);;
//            System.out.format("Efield: %12.8f %12.8f %12.8f \n", Efield.x, Efield.y, Efield.z);
            Vector deltaVelocity = Efield.multiplyByScalar((e / m) * delta_t * correctionFactor);

            position = position.addVectors(deltaPosition);
            velocity = velocity.addVectors(deltaVelocity);
            printPosVel();
        }

        printPosVel();

        // find total energy
        double x1 = this.position.x;
        // Electric field
        Efield = geometry.getEfield(position);
        E = Efield.getNorm();

        // should be diff between U when working in diff geometry
//        double deltaU = e * SettingsFresh.getInstance().getE() * (x1 - x0);
        double K_f = 0.5 * m * this.velocity.Square();
        double delta_energy = ((K_f - K_i) - (e * E * (x1 - x0)));

//        System.out.format("K_i: %.3f, K_f: %.3f, deltaU: %.3f \n", K_i, K_f, deltaU);
//        System.out.println("delta energy (should be ~0)" + delta_energy);
        // NEW!!!
        // return a delta_energy for RMS
        return delta_energy;
    }

    public void forwardScatter(double energyLoss, double minCos) {
        double Ki = 0.5 * m * velocity.Square();
        double Kf = Ki - energyLoss;
//        System.out.println("energy after forward scatter: " + Kf);
        if (Kf < 0.0) {
            velocity.x = 0;
            velocity.y = 0;
            velocity.z = 0;
            return;
        }

        // make a set of basis vectors where basis1 is the original velocity direction
        Vector basis1 = velocity.getUnitVector();
        Vector basis2 = basis1.constructPerpendicular();
        Vector basis3 = basis1.getCrossProduct(basis2);

        // DEBUG VERSION:
        double ran1 = Main.random.nextDouble();
        double ran2 = Main.random.nextDouble();
//        System.out.format("%20.15f %20.15f \n", ran1, ran2);
        Main.randomNums.add(ran1);
        Main.randomNums.add(ran2);

        double cosTheta = (1.0 - minCos) * ran1 + minCos;
        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
        double phi = 2.0 * Math.PI * ran2;

//        double cosTheta = (1.0 - minCos) * Math.random() + minCos; // random in range minCos to 1.0
//        double sinTheta = Math.sqrt(1.0 - cosTheta * cosTheta);
//        double phi = 2.0 * Math.PI * Math.random(); // random from 0 to 2pi
        // v magnitude
        double v1 = Math.sqrt((2.0 * Kf) / m);
        Vector v_b1 = basis1.multiplyByScalar(cosTheta * v1);
        Vector v_b2 = basis2.multiplyByScalar(sinTheta * Math.cos(phi) * v1);
        Vector v_b3 = basis3.multiplyByScalar(sinTheta * Math.sin(phi) * v1);

        Vector v_b1_b2 = v_b1.addVectors(v_b2);
        Vector velocity1 = v_b1_b2.addVectors(v_b3);
        velocity = velocity1;
    }

    // check ionization
    boolean checkIonization() {
        double energy = 0.5 * this.m * this.velocity.Square(); // in eV
        // FOR DEBUGGING
//        System.out.println("energy: " + energy);
        double Ui = geometry.getUI();
        if (energy >= Ui) {
            return true;
        }
        return false;
    }
}
