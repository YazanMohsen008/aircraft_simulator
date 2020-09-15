package entitites;

import Physics.MathVector;

public class AirplaneElement {

    float Mass ;

    //Element position (Its Center Of Gravity)
    MathVector Position;

    //The Vector from Airplane CG To Element CG
    MathVector CGPosition;


    /**
     * Considering  it A rectangle Cylinder
     Ixx=(1/12)*m(Width*Width+Height*Height).
     Iyy=(1/12)*m(Length*Length+Height*Height)
     Izz=(1/12)*m(Length*Length+Width*Width)
     these are The principal axes and The product of inertia Ixy,Iyz,Ixz are 0
     LocalInertia=(Ixx,Iyy,Izz)
     */
    MathVector LocalInertia;

    //angle about y axis
    float Incidence;

    //angle about x axis
    float Dihedral;

    MathVector NormalVector;

    float Area;

    //Element Situation 0 not used , 1 up, -1 down
    int Flap ;



    public float getMass() {
        return Mass;
    }

    public void setMass(float mass) {
        Mass = mass;
    }

    public MathVector getPosition() {
        return Position;
    }

    public void setPosition(MathVector position) {
        Position = position;
    }

    public MathVector getLocalInertia() {
        return LocalInertia;
    }

    public void setLocalInertia(MathVector localInertia) {
        LocalInertia = localInertia;
    }

    public float getIncidence() {
        return Incidence;
    }

    public void setIncidence(float incidence) {
        Incidence = incidence;
    }

    public float getDihedral() {
        return Dihedral;
    }

    public void setDihedral(float dihedral) {
        Dihedral = dihedral;
    }

    public float getArea() {
        return Area;
    }

    public void setArea(float area) {
        Area = area;
    }

    public int getFlap() {
        return Flap;
    }

    public void setFlap(int flap) {
        this.Flap = flap;
    }

    public MathVector getNormalVector() {
        return NormalVector;
    }

    public void setNormalVector(MathVector normalVector) {
        NormalVector = normalVector;
    }

    public MathVector getCGPosition() {
        return CGPosition;
    }

    public void setCGPosition(MathVector CGPosition) {
        this.CGPosition = CGPosition;
    }

    public void CalculateNormalVector() {
        float in = DegreesToRadians(this.Incidence);
        float di = DegreesToRadians(this.Dihedral);

        this.NormalVector =new MathVector((float)Math.sin(in),
        (float)(Math.cos(in)*Math.cos(di)),
        (float)(Math.cos(in)*Math.sin(di))
        );
        this.NormalVector.Normalize();

    }
    public  float  DegreesToRadians(float deg) {

        return (float) (deg * Math.PI) / 180.0f;
    }

    private float  RadiansToDegrees(float rad) { return (rad * 180.0f) / (float) Math.PI; }

}

