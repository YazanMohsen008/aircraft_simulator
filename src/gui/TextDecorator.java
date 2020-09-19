package gui;

import entitites.Airplane;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TextDecorator {

    Airplane airplane;

    String mForces;
    String mVelocity;
    String mAngularVelocity;
    String mAOA1;
    String mAOA2;
    String mPosition;
    String mRotations;
    String mDensity;
    String mAirVelocity;
    String mLift;
    String mDrag;
    String mThrust;
    String mGravity;


    public TextDecorator(Airplane airplane) {
        this.airplane = airplane;
    }



    public String getForces() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mForces = "Forces: \tx = " +  df.format(airplane.getForces().getX()) + "  y = "
                + df.format(airplane.getForces().getY()) + " z = "
                + df.format(airplane.getForces().getZ());

        return mForces;
    }


    public String getThrust() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mThrust = "Thrust: \tx = " +  df.format(airplane.getThrust().getX()) + "  y = "
                + df.format(airplane.getThrust().getY()) + " z = "
                + df.format(airplane.getThrust().getZ());

        return mThrust;
    }

    public String getVelocity() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mVelocity = "Velocity : \tx = " +  df.format(airplane.getVelocityBody().getX()) + "  y = "
                + df.format(airplane.getVelocityBody().getY()) + " z = "
                + df.format(airplane.getVelocityBody().getZ());

        return mVelocity;
    }

    public String getAirVelocity() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mAirVelocity = "Air speed: " +  df.format(airplane.getAirSpeed());

        return mAirVelocity;
    }

    public String getAngularVelocity() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mAngularVelocity = "Ang Vel: \tx = " +  df.format(airplane.getAngularVelocity().getX()) + "  y = "
                + df.format(airplane.getAngularVelocity().getY()) + " z = "
                + df.format(airplane.getAngularVelocity().getZ());

        return mAngularVelocity;
    }


    public String getAOA1() {
        String patern = "##.##";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mAOA1 = "Aileron AOA = " +  df.format(airplane.getAoaAileron()) + "Flaps AOA = "
                + df.format(airplane.getAoaFlaps());

        return mAOA1;
    }

    public String getAOA2() {

        String patern = "##.##";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mAOA2 = "Elevator AOA = " +  df.format(airplane.getAoaElevator()) + "Rudder AOA = "
                + df.format(airplane.getAoaRudder());

        return mAOA2;

    }

    public String getPosition() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mPosition = "Position : \tx = " +  df.format(airplane.getPosision().getX()) + "  y = "
                + df.format(airplane.getPosision().getY()) + " z = "
                + df.format(airplane.getPosision().getZ());

        return mPosition;
    }


    public String getRotations() {
        String patern = "###.###";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mRotations = "Pitch = " +  df.format(airplane.getEulerAngles().getZ()) + "  Yaw = "
                + df.format(airplane.getEulerAngles().getY()) + " Roll = "
                + df.format(airplane.getEulerAngles().getX());

        return mRotations;
    }

    public String getAirDensity() {
        String patern = "##.######";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mDensity = "Air Density = " +  df.format(airplane.getRho());

        return mDensity;
    }

    public String getGravity() {
        String patern = "##.######";
        NumberFormat nf = NumberFormat.getNumberInstance();
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(patern);

        mGravity = "Gravity = " +  df.format(airplane.getGravity());

        return mGravity;
    }

}
