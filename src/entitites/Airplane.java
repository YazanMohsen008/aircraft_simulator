package entitites;

import Models.TexturedModel;
import Physics.MathVector;
import Physics.Matrix3x3;
import Physics.Quaternion;
import gui.ParametersScreenController;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import renderEngine.DisplayManager;


public class Airplane extends Entity {


    ParametersScreenController parameters;


    //Math
    final float	tol = 0.0001f;		// float type tolerance

    final float _MAXTHRUST= 3000.0f;
    final float _DTHRUST= 1.0f;
    final float _PRO_ENGINE_AREA = 10.54f;


    float	g	=  -3.174f;		// acceleration due to gravity
    float	rho = 0.0023769f;	// desity of air at sea level
    //Rigid Body
    private float      AirplaneMass = 0;
    private AirplaneElement[] AirplaneElements=new AirplaneElement[8];
    private Matrix3x3  Inertia=new Matrix3x3();
    private Matrix3x3  InertiaInverse=new Matrix3x3();
    private MathVector Velocity = new MathVector(0, 0f, 0);
    private MathVector VelocityBody = new MathVector(0, 0f, 0);
    private MathVector AngularVelocity = new MathVector(0, 0f, 0);
    private MathVector EulerAngles= new MathVector(0, 0f, 0);
    private float      Speed;
    private Quaternion Orientation=new Quaternion();
    private MathVector Forces= new MathVector(0, 0f, 0);
    private MathVector Moments= new MathVector(0, 0f, 0);
    private float ThrustForce = 4; // Wind Speed

    private float aoaAileron;
    private float aoaFlaps;
    private float aoaElevator;
    private float aoaRudder;

    MathVector Thrust=new MathVector();

    boolean		Stalling;		// Flag to let us know if we are in a stalled condition
    boolean		Flaps;			// Flag to let us know if the flaps are down

    private MathVector CG; // total moment (torque) on body

    public Airplane(TexturedModel model, MathVector position, float rotX, float rotY, float rotZ, float scale
            , AirplaneElement[] AirplaneElements) {

        super(model, position, rotX, rotY, rotZ, scale);
        for(int i=0;i<this.AirplaneElements.length;i++)
            this.AirplaneElements[i] = new AirplaneElement();


    }

    public   void   InitializeAirplane() {
        float iRoll, iPitch, iYaw;

        // Set initial velocity
        this.Velocity.x = 20.0f;
        this.Velocity.y = 0.0f;
        this.Velocity.z = 0.0f;
        this.Speed = 0.0f;

        // Set initial angular velocity
        this.AngularVelocity.x = 0.0f;
        this.AngularVelocity.y = 0.0f;
        this.AngularVelocity.z = 0.0f;

        // Set the initial thrust, forces and moments
        this.Forces.x = 0.0f;
        this.Forces.y = 0.0f;
        this.Forces.z = 0.0f;
        this.ThrustForce = 1;

        this.Moments.x = 0.0f;
        this.Moments.y = 0.0f;
        this.Moments.z = 0.0f;

        // Zero the velocity in body space coordinates
        this.VelocityBody.x = 0.0f;
        this.VelocityBody.y = 0.0f;
        this.VelocityBody.z = 0.0f;

        // Set these to false at first, you can control later using the keyboard
        Stalling = false;
        Flaps = false;

        // Set the initial orientation
        iRoll  = 0.0f;
        iPitch = 0.0f;
        iYaw   = 0.0f;
        Orientation=Orientation.MakeQFromEulerAngles(iRoll, iYaw, iPitch);

        // Now go ahead and calculate the plane's mass properties
        CalcAirplaneMassProperties();
    }
    private  void   CalculateElementsNormalVector() {
        for (AirplaneElement Element : AirplaneElements) {
            Element.CalculateNormalVector();
            //System.out.println(Element.NormalVector);
        }

    }
    private  void   CalculateAirplaneMass() {
        for (AirplaneElement Element : AirplaneElements)
            AirplaneMass += Element.Mass;
    }
    private  void   CalculateAirplaneCG(){

        MathVector InertiaMoments=new MathVector(0,0,0);
        for (int i = 0; i< 8; i++)
        {
            MathVector EPosition=AirplaneElements[i].Position.copy();
            EPosition.Mult(AirplaneElements[i].Mass);
            InertiaMoments.Add(EPosition);
        }
        InertiaMoments.Div(AirplaneMass);
        CG=InertiaMoments.copy();

    }
    private  void   CalculateElementCGCoords() {
        for (int i = 0; i< 8; i++)
        {
            MathVector EPosition= AirplaneElements[i].Position.copy();
            EPosition.Sub(CG);
            AirplaneElements[i].CGPosition = EPosition.copy();
        }
    }
    private  void   CalculateAirplaneInertiaTensor() {
        float  Ixx = 0, Iyy = 0, Izz = 0,Ixy = 0, Ixz = 0, Iyz = 0;
        for (int i = 0; i< 8; i++)
        {

            /**
             The Ixx For Element is
             its Local Inertia x Component which represent The Resistance of angular motion about x axes
             Plus
             (its mass
             mult
             the Distance between the X axes and The CG axes
             which is the Vector passing throw the Airplane CG
             )
             Ixx= Io+md^2
             Ixx[i]= E[i].Io.x+ E[i].mass * e[i].CG.Y^2 + e[i].CG.Z^2
             */

            Ixx += AirplaneElements[i].LocalInertia.getX() + AirplaneElements[i].Mass *
                    (AirplaneElements[i].CGPosition.getY() *AirplaneElements[i].CGPosition.getY()+
                     AirplaneElements[i].CGPosition.getZ()*AirplaneElements[i].CGPosition.getZ());

            //Iyy= E[i].Io.Y+ E[i].mass * e[i].CG.Z^2 + e[i].CG.X^2
            Iyy += AirplaneElements[i].LocalInertia.getY() + AirplaneElements[i].Mass *
                    (AirplaneElements[i].CGPosition.getZ()*AirplaneElements[i].CGPosition.getZ() +
                            AirplaneElements[i].CGPosition.getX()*AirplaneElements[i].CGPosition.getX());

            //Izz= E[i].Io.Z+ E[i].mass * e[i].CG.X^2 + e[i].CG.Y^2
            Izz += AirplaneElements[i].LocalInertia.getZ() + AirplaneElements[i].Mass *
                    (AirplaneElements[i].CGPosition.getX()   * AirplaneElements[i].CGPosition.getX() +
                            AirplaneElements[i].CGPosition.getY()   * AirplaneElements[i].CGPosition.getY());

            //Ixy=  E[i].mass * e[i].CG.X + e[i].CG.Y
            Ixy += AirplaneElements[i].Mass * (AirplaneElements[i].CGPosition.getX() * AirplaneElements[i].CGPosition.getY());

            //Ixz= E[i].mass * e[i].CG.X + e[i].CG.Z
            Ixz += AirplaneElements[i].Mass * (AirplaneElements[i].CGPosition.getX() * AirplaneElements[i].CGPosition.getZ());

            //Iyz=  E[i].mass * e[i].CG.Y + e[i].CG.Z
            Iyz += AirplaneElements[i].Mass * (AirplaneElements[i].CGPosition.getY() * AirplaneElements[i].CGPosition.getZ());
        }

        // Finally, set up the airplane's mass and its inertia matrix and take the
        // inverse of the inertia matrix.
        this.Inertia.setE11(Ixx);
        this.Inertia.setE12(-Ixy);
        this.Inertia.setE13(-Ixz);

        this.Inertia.setE21(-Ixy);
        this.Inertia.setE22(Iyy);
        this.Inertia.setE23(-Iyz);

        this.Inertia.setE31(-Ixz);
        this.Inertia.setE32(-Iyz);
        this.Inertia.setE33(Izz);
        // TODO: check inertia inverse
        this.InertiaInverse = this.Inertia.Inverse();
    }
    public   void   CalcAirplaneMassProperties() {


        AirplaneElements[0].Mass = 6.56f;
        AirplaneElements[0].Position =new MathVector(14.5f,  2.5f, 12.0f);
        AirplaneElements[0].LocalInertia = new MathVector(13.92f ,   10.50f, 24.00f);
        AirplaneElements[0].Incidence = -3.5f;
        AirplaneElements[0].Dihedral = 0.0f;
        AirplaneElements[0].Area = 31.2f;
        AirplaneElements[0].Flap = 0;

        AirplaneElements[1].Mass = 7.31f;
        AirplaneElements[1].Position =new MathVector(14.5f, 2.5f, 5.5f);
        AirplaneElements[1].LocalInertia = new MathVector(21.95f ,   12.22f, 33.67f);
        AirplaneElements[1].Incidence = -3.5f;
        AirplaneElements[1].Dihedral = 0.0f;
        AirplaneElements[1].Area = 36.4f;
        AirplaneElements[1].Flap = 0;

        AirplaneElements[2].Mass = 7.31f;
        AirplaneElements[2].Position = new MathVector(14.5f, 2.5f, -5.5f);
        AirplaneElements[2].LocalInertia =new MathVector(21.95f,    12.22f, 33.67f);
        AirplaneElements[2].Incidence = -3.5f;
        AirplaneElements[2].Dihedral = 0.0f;
        AirplaneElements[2].Area = 36.4f;
        AirplaneElements[2].Flap = 0;

        AirplaneElements[3].Mass = 6.56f;
        AirplaneElements[3].Position = new MathVector(14.5f, 2.5f, -12.0f);
        AirplaneElements[3].LocalInertia = new MathVector(13.92f ,   10.50f, 24.00f);
        AirplaneElements[3].Incidence = -3.5f;
        AirplaneElements[3].Dihedral = 0.0f;
        AirplaneElements[3].Area = 31.2f;
        AirplaneElements[3].Flap = 0;

        AirplaneElements[4].Mass = 2.62f;
        AirplaneElements[4].Position = new MathVector(3.03f, 3.0f, 2.5f);
        AirplaneElements[4].LocalInertia = new MathVector(0.837f,   0.385f, 1.206f);
        AirplaneElements[4].Incidence = 0.0f;
        AirplaneElements[4].Dihedral = 0.0f;
        AirplaneElements[4].Area = 10.8f;
        AirplaneElements[4].Flap = 0;

        AirplaneElements[5].Mass = 2.62f;
        AirplaneElements[5].Position = new MathVector(3.03f, 3.0f, -2.5f);
        AirplaneElements[5].LocalInertia = new MathVector(0.837f,   0.385f, 1.206f);
        AirplaneElements[5].Incidence = 0.0f;
        AirplaneElements[5].Dihedral = 0.0f;
        AirplaneElements[5].Area = 10.8f;
        AirplaneElements[5].Flap = 0;

        AirplaneElements[6].Mass = 2.93f;
        AirplaneElements[6].Position =new MathVector(2.25f, 5.0f, 0.0f);
        AirplaneElements[6].LocalInertia =new MathVector(1.262f , 1.942f ,0.718f);
        AirplaneElements[6].Incidence = 0.0f;
        AirplaneElements[6].Dihedral = 90.0f;
        AirplaneElements[6].Area = 12.0f;
        AirplaneElements[6].Flap = 0;

        AirplaneElements[7].Mass = 31.8f;
        AirplaneElements[7].Position = new MathVector(15.25f, 1.5f, 0.0f);
        AirplaneElements[7].LocalInertia = new MathVector(66.30f,  861.9f, 861.9f);
        AirplaneElements[7].Incidence = 0.0f;
        AirplaneElements[7].Dihedral = 0.0f;
        AirplaneElements[7].Area = 84.0f;
        AirplaneElements[7].Flap = 0;


        CalculateElementsNormalVector();

        //Total Mass=SumOf( AirplaneElementsMasses);
        CalculateAirplaneMass();

        //TO find  The CG For Object
        // 1.Divide it into Elements
        // 2.vR:CG For Every Element(the Position OF the SubModel)
        // 3.fM:the Element Mass
        // 4.vI:M*R
        // 5.vCG= SumOf(I)/Total Mass
        CalculateAirplaneCG();

        // these Coordinates are for Element with Considering Airplane CG is the Center OF Coordinate (Body Coordinate System)
        // vElementCGCoordinates =vElementGlobalCoordinates - vCG
        CalculateElementCGCoords();

        CalculateAirplaneInertiaTensor();

    }



    //------------------------------------------------------------------------//
    // This function calculates all of the forces and moments acting on the
    // plane at any given time.
    //------------------------------------------------------------------------//



    private void   CalcAirplaneLoads(){

        MathVector	Fb=new MathVector();
        MathVector	Mb=new MathVector();

        g = parameters.getGravity();
        rho = parameters.getAirDensity();

        // reset forces and moments:
        this.Forces.x = 0.0f;
        this.Forces.y = 0.0f;
        this.Forces.z = 0.0f;

        this.Moments.x = 0.0f;
        this.Moments.y = 0.0f;
        this.Moments.z = 0.0f;

        Fb.x = 0.0f;	Mb.x = 0.0f;
        Fb.y = 0.0f;	Mb.y = 0.0f;
        Fb.z = 0.0f;	Mb.z = 0.0f;

        // Define the thrust vector, which acts through the plane's CG
        Thrust.x = 1.0f;
        Thrust.y = 0.0f;
        Thrust.z = 0.0f;


        float LocalSpeed;
        MathVector DragVector;
        MathVector LiftVector;
        float AttackAngle;
        float tmp;
        MathVector vtmp;
        Stalling = false;
        MathVector LocalVelocity = new MathVector();

        for(int i=0;i<8;i++) {
            MathVector Resultant = new MathVector();

            if (i == 6) // The tail/rudder is a special case since it can rotate;
                AirplaneElements[i].CalculateNormalVector();

            // Calculate local velocity at element
            // The local velocity includes the velocity due to linear
            // motion of the airplane,
            // plus the velocity at each element due to the
            // rotation of the airplane.
            // Here's the rotational part

            vtmp = this.AngularVelocity.copy();
            vtmp.CrossProduct(AirplaneElements[i].CGPosition);

            //v=v+w*r
            // velocity = local speed
            LocalVelocity = this.VelocityBody.copy();
            LocalVelocity.Add(vtmp);

            // Calculate local air speed
            LocalSpeed = LocalVelocity.Magnitude();

            // Find the direction in which drag will act.
            // Drag always acts inline with the relative
            // velocity but in the opposing direction
            DragVector = LocalVelocity.copy();
            DragVector.Mult(-1);
            if (LocalSpeed > 1.)
                DragVector.Div(LocalSpeed);
            // Find the direction in which lift will act.
            // Lift is always perpendicular to the drag vector

            LiftVector = DragVector.copy();
            LiftVector.CrossProduct(AirplaneElements[i].NormalVector);
            LiftVector.CrossProduct(DragVector);
            float temporary = LiftVector.Magnitude();
            LiftVector.Normalize();

            // Find the angle of attack.
            // The attack angle is the angle between the lift vector and the
            // element normal vector. Note, the sine of the attack angle
            // is equal to the cosine of the angle between the drag vector and
            // the normal vector.
            tmp = DragVector.DotProduct(AirplaneElements[i].NormalVector);


            if (tmp > 1.) tmp = 1;
            if (tmp < -1) tmp = -1;
            AttackAngle = RadiansToDegrees((float) Math.asin(tmp));
            if (i == 0) {
                aoaAileron = AttackAngle;
            }
            else if (i == 1) {
                aoaFlaps = AttackAngle;
            }

            else if (i == 2) {
                aoaElevator = AttackAngle;
            }

            else if (i == 3) {
                aoaRudder = AttackAngle;
            }

            // Determine the resultant force (lift and drag) on the element.
            tmp = 0.5f * rho * LocalSpeed * LocalSpeed * AirplaneElements[i].Area;
            if (i == 6) // Tail/rudder
            {
                MathVector temp1 = LiftVector.copy();
                temp1.Mult(RudderLiftCoefficient(AttackAngle));
                Resultant.Add(temp1);

                MathVector temp2 = DragVector.copy();
                temp2.Mult(RudderDragCoefficient(AttackAngle));
                Resultant.Add(temp2);

                Resultant.Mult(tmp);

            } else if (i == 7) {
                Resultant = DragVector.copy();
                Resultant.Mult(0.5f);
                Resultant.Mult(tmp); // simulate fuselage drag
            } else {
                MathVector temp1 = LiftVector.copy();
                temp1.Mult(LiftCoefficient(AttackAngle, AirplaneElements[i].Flap));
                Resultant.Add(temp1);

                MathVector temp2 = DragVector.copy();
                temp2.Mult(DragCoefficient(AttackAngle, AirplaneElements[i].Flap));
                Resultant.Add(temp2);

                Resultant.Mult(tmp);

            }
            // Check for stall.
            // We can easily determine stall by noting when the coefficient
            // of lift is 0. In reality, stall warning devices give warnings well
            // before the lift goes to 0 to give the pilot time to correct.
            if (i <= 3) {
                if (LiftCoefficient(AttackAngle, AirplaneElements[i].Flap) == 0)
                    Stalling = true;
            }

            // Keep a running total of these resultant forces (total force)
            Fb.Add(Resultant);
            // Calculate the moment about the CG of this element's force
            // and keep a running total of these moments (total moment)

            /** Mcg = r × F*/
            vtmp = AirplaneElements[i].CGPosition.copy();
            vtmp.CrossProduct(Resultant);
/*
            if (i == 0) {
             //   System.out.println(Resultant);
            }
            else
                vtmp.Mult(0);
*/

            //System.out.println(i + 1 + " "  + vtmp);
            Mb.Add(vtmp);
        }
        // Now add the thrust
        Thrust.Mult(ThrustForce);
        Fb.Add(Thrust);

        // Convert forces from model space to earth space
        this.Forces = Orientation.QVRotate(Fb);


        // Apply gravity (g is defined as -32.174 ft/s^2)
        MathVector Weight=new MathVector(0,g * this.AirplaneMass,0);
        this.Forces.Add(Weight);

        Moments.Add(Mb);
        //System.out.println("Moment = " + Moments);
        //System.out.println("Forces = " + Forces);
    }
    public  float  DegreesToRadians(float deg) {

        return (float) (deg * Math.PI) / 180.0f;
    }
    private float  RadiansToDegrees(float rad) { return (rad * 180.0f) / (float) Math.PI; }

    public void StepSimulation() {
        CheckInput();
        // calculate all of the forces and moments on the airplane:
        CalcAirplaneLoads();
        // calculate the acceleration of the airplane in earth space:

        // Take care of translation first:
        // (If this body were a particle, this is all you would need to do.)
        MathVector Ae;
        float dt=DisplayManager.getFrameTimeSeconds();

        if (dt > (0.016f)) dt = (0.016f);
        if (dt < 0.001f) dt = 0.001f;

        Ae = this.Forces.copy();
        Ae.Div(AirplaneMass);
        Ae.Mult(dt);

        // calculate the velocity of the airplane in earth space:
        this.Velocity.Add(Ae);
        // calculate the position of the airplane in earth space:
        MathVector pos = Velocity.copy();
        pos.Mult(dt);


        //System.out.println("Pos = " +getPosition());

        increasePosition(pos.x, pos.y, pos.z);

        float height = super.getPosition().getY();

        if (height < 0)
            super.setHeight(0);


        //System.out.println(super.getPosition());

        // Now handle the rotations:
        float mag;

        MathVector IA= Inertia.Mult(AngularVelocity);
        MathVector AV=AngularVelocity.copy();
        AV.CrossProduct(IA);
        MathVector M=Moments.copy();
        M.Sub(AV);
        IA=InertiaInverse.Mult(M);
        IA.Mult(dt);
        AngularVelocity  =  (IA);

        // calculate the new rotation quaternion:
        Quaternion temp =this.Orientation.copy();
        temp.Mult(AngularVelocity);
        temp.Mult(0.5f);
        temp.Mult(dt);
        this.Orientation.Add(temp);

        // now normalize the orientation quaternion:
        mag = this.Orientation.Magnitude();
        if (mag != 0)
            this.Orientation.Div(mag);
        // calculate the velocity in body space:
        // (we'll need this to calculate lift and drag forces)

        Quaternion qQ = Orientation.Conjugate();
        this.VelocityBody = qQ.QVRotate(this.Velocity);

        // calculate the air speed:
        this.Speed = this.Velocity.Magnitude();
        // get the Euler angles for our information
        MathVector u;
        u = Orientation.MakeEulerAnglesFromQ();

        this.EulerAngles.x = u.x; // roll
        this.EulerAngles.y = u.y; // pitch
        this.EulerAngles.z = u.z; // yaw

       increaseRotation(
               (float) Math.toRadians(this.EulerAngles.x),
               (float) Math.toRadians(this.EulerAngles.z),
        (float) Math.toRadians(this.EulerAngles.y)
       );


       //SetCameraPosition(-this.getPosition().y, this.getPosition().z, this.getPosition().x);

       // MathVector vz = GetBodyZAxisVector(); // pointing up in our coordinate system
       // MathVector vx = GetBodyXAxisVector(); // pointing forward in our coordinate system
       // SetCameraOrientation(	-vx.y, vx.z, vx.x, -vz.y, vz.z, vz.x);

    }



    MathVector	GetBodyZAxisVector()
    {

        MathVector	v=new MathVector();

        v.x = 0.0f;
        v.y = 0.0f;
        v.z = 1.0f;

        return Orientation.QVRotate(v);
    }

    //------------------------------------------------------------------------//
    MathVector	GetBodyXAxisVector()
    {

        MathVector v=new MathVector();

        v.x = 1.0f;
        v.y = 0.0f;
        v.z = 0.0f;

        return Orientation.QVRotate(v);

    }
    void IncThrust() {
        ThrustForce += (_DTHRUST);
        if(ThrustForce > _MAXTHRUST)
            ThrustForce = _MAXTHRUST;
    }
    void DecThrust() {
        ThrustForce-=(_DTHRUST);
        if(ThrustForce < 0)
            ThrustForce = 0;
    }
    void LeftRudder()
    {
        AirplaneElements[6].Incidence = 16;
    }//16
    void RightRudder()
    {
        AirplaneElements[6].Incidence = -16;//-16
    }
    void ZeroRudder()
    {
        AirplaneElements[6].Incidence = 0;
    }
    void RollLeft() {
        AirplaneElements[0].Flap = 1;
        AirplaneElements[3].Flap = -1;
    } void RollRight() {
        AirplaneElements[0].Flap = -1;
        AirplaneElements[3].Flap = 1;
    }
    void ZeroAilerons() {
        AirplaneElements[0].Flap = 0;
        AirplaneElements[3].Flap = 0;
    }
    void PitchUp() {
        AirplaneElements[4].Flap = 1;
        AirplaneElements[5].Flap = 1;
    }
    void PitchDown() {
        AirplaneElements[4].Flap = -1;
        AirplaneElements[5].Flap = -1;
    }
    void ZeroElevators() {
        AirplaneElements[4].Flap = 0;
        AirplaneElements[5].Flap = 0;
    }

    void ElevatorsUP() {
        boolean Flaps=false;
        AirplaneElements[4].Flap = -1;
        AirplaneElements[5].Flap = -1;
        Flaps = true;
    }

    void ElevatorsDown() {
        boolean Flaps=false;
        AirplaneElements[4].Flap = 1;
        AirplaneElements[5].Flap = 1;
        Flaps = true;
    }
    void FlapsDown() {
        boolean Flaps=false;
        AirplaneElements[1].Flap = -1;
        AirplaneElements[2].Flap = -1;
        Flaps = true;
    }
    void ZeroFlaps() {
        boolean Flaps=false;
        AirplaneElements[1].Flap = 0;
        AirplaneElements[2].Flap = 0;
        Flaps = false;
    }
    public void CheckInput() {
        MathVector vz, vx;
        char[] buf;
        char[] s;

        ZeroRudder();
        ZeroAilerons();
        ZeroElevators();
        ZeroFlaps();

        // pitch down
        if (Keyboard.isKeyDown(Keyboard.KEY_U))
            PitchDown();

        // pitch up
        if (Keyboard.isKeyDown(Keyboard.KEY_J))
            PitchUp();

        // roll left
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            RollLeft();

        // roll right
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            RollRight();

        //  Increase thrust
/*
        System.out.println(getRotX());
        System.out.println(getRotY());
        System.out.println(getRotZ());
        System.out.println("-------------");
*/
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            IncThrust();

/*
            System.out.println("Mass = " + AirplaneMass);
            System.out.println("Inertia =" + Inertia.toString());
            System.out.println("Velocity = " + Velocity);
            System.out.println("Velocity Body" + VelocityBody);
            System.out.println("Angular Velocity" + AngularVelocity);
            System.out.println("Angular Velocity" + AngularVelocity);
            System.out.println("Forces" + Forces);
            System.out.println("Moments" + Moments);
            System.out.println("Rot x" + EulerAngles.x);
            System.out.println("Rot y" + EulerAngles.y);
            System.out.println("Rot z" + EulerAngles.z);
            System.out.println("-------------");
*/

        }

        //  Decrease thrust
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) // Z
            DecThrust();

        // yaw left
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {// x
            LeftRudder();
        }

        // yaw right
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) // c
            RightRudder();

        // landing flaps down
        if (Keyboard.isKeyDown(Keyboard.KEY_F)) //f
            FlapsDown();

        // landing flaps up
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) // d
            ZeroFlaps();

        if(Stalling)
        {
            System.out.println("Warning!!! ");
        }

    }


    float	LiftCoefficient(float angle, int flaps)
    {
        float []clf0 = {-0.54f, -0.2f, 0.2f, 0.57f, 0.92f, 1.21f, 1.43f, 1.4f, 1.0f};
        float []clfd = {0.0f, 0.45f, 0.85f, 1.02f, 1.39f, 1.65f, 1.75f, 1.38f, 1.17f};
        float []clfu = {-0.74f, -0.4f, 0.0f, 0.27f, 0.63f, 0.92f, 1.03f, 1.1f, 0.78f};
        float []a	 = {-8.0f, -4.0f, 0.0f, 4.0f, 8.0f, 12.0f, 16.0f, 20.0f, 24.0f};
        float cl;
        int	  i;

        cl = 0;
        for (i=0; i<8; i++)
        {
            if( (a[i] <= angle) && (a[i+1] > angle) )
            {
                switch(flaps)
                {
                    case 0:// flaps not deflected
                        cl = clf0[i] - (a[i] - angle) * (clf0[i] - clf0[i+1]) / (a[i] - a[i+1]);
                        break;
                    case -1: // flaps down
                        cl = (clfd[i] - (a[i] - angle) * (clfd[i] - clfd[i+1]) / (a[i] - a[i+1])) ;
                        break;
                    case 1: // flaps up
                        cl = clfu[i] - (a[i] - angle) * (clfu[i] - clfu[i+1]) / (a[i] - a[i+1]);
                        break;
                }
                break;
            }
        }

        return cl;
    }

    //------------------------------------------------------------------------//
    //  Given the attack angle and the status of the flaps, this function
    //  returns the appropriate drag coefficient for a cambered airfoil with
    //  a plain trailing edge flap (+/- 15 degree deflection).
    //------------------------------------------------------------------------//
    float	DragCoefficient(float angle, int flaps)
    {
        float []cdf0 = {0.01f, 0.0074f, 0.004f, 0.009f, 0.013f, 0.023f, 0.05f, 0.12f, 0.21f};
        float []cdfd = {0.0065f, 0.0043f, 0.0055f, 0.0153f, 0.0221f, 0.0391f, 0.1f, 0.195f, 0.3f};
        float []cdfu = {0.005f, 0.0043f, 0.0055f, 0.02601f, 0.03757f, 0.06647f, 0.13f, 0.18f, 0.25f};
        float []a	 = {-8.0f, -4.0f, 0.0f, 4.0f, 8.0f, 12.0f, 16.0f, 20.0f, 24.0f};
        float cd;
        int	  i;

        cd = 0.75f;
        for (i=0; i<8; i++)
        {
            if( (a[i] <= angle) && (a[i+1] > angle) )
            {
                switch(flaps)
                {
                    case 0:// flaps not deflected
                        cd = cdf0[i] - (a[i] - angle) * (cdf0[i] - cdf0[i+1]) / (a[i] - a[i+1]);
                        break;
                    case -1: // flaps down
                        cd = cdfd[i] - (a[i] - angle) * (cdfd[i] - cdfd[i+1]) / (a[i] - a[i+1]);
                        break;
                    case 1: // flaps up
                        cd = cdfu[i] - (a[i] - angle) * (cdfu[i] - cdfu[i+1]) / (a[i] - a[i+1]);
                        break;
                }
                break;
            }
        }

        return cd;

    }

    //------------------------------------------------------------------------//
    //  Given the attack angle this function returns the proper lift coefficient
    //  for a symmetric (no camber) airfoil without flaps.
    //------------------------------------------------------------------------//
    float	RudderLiftCoefficient(float angle)
    {
        float []clf0= {0.16f, 0.456f, 0.736f, 0.968f, 1.144f, 1.12f, 0.8f};
        float []a	 = {0.0f, 4.0f, 8.0f, 12.0f, 16.0f, 20.0f, 24.0f};
        float cl;
        int	  i;
        float	aa = Math.abs(angle);

        cl = 0;
        for (i=0; i<6; i++)
        {
            if( (a[i] <= aa) && (a[i+1] > aa) )
            {
                cl = clf0[i] - (a[i] - aa) * (clf0[i] - clf0[i+1]) / (a[i] - a[i+1]);
                if (angle < 0) cl = -cl;
                break;
            }
        }
        return cl;
    }

    //------------------------------------------------------------------------//
    //  Given the attack angle this function returns the proper drag coefficient
    //  for a symmetric (no camber) airfoil without flaps.
    //------------------------------------------------------------------------//
    float	RudderDragCoefficient(float angle)
    {
        float []cdf0 = {0.0032f, 0.0072f, 0.0104f, 0.0184f, 0.04f, 0.096f, 0.168f};
        float []a = {0.0f, 4.0f, 8.0f, 12.0f, 16.0f, 20.0f, 24.0f};
        float cd;
        int	  i;
        float	aa = Math.abs(angle);

        cd = 0.75f;
        for (i=0; i<6; i++)
        {
            if( (a[i] <= aa) && (a[i+1] > aa) )
            {
                cd = cdf0[i] - (a[i] - aa) * (cdf0[i] - cdf0[i+1]) / (a[i] - a[i+1]);
                break;
            }
        }
        return cd;
    }

    public float getRho() {
        return rho;
    }
    public float getGravity() {
        return this.g;
    }

    public MathVector getVelocityBody() {
        return VelocityBody;
    }

    public MathVector getAngularVelocity() {
        return AngularVelocity;
    }

    public MathVector getEulerAngles() {
        return EulerAngles;
    }

    public MathVector getForces() {
        return Forces;
    }

    public MathVector getPosision() {
        return super.getPosition();
    }

    public float getAoaAileron() {
        return aoaAileron;
    }

    public float getAoaFlaps() {
        return aoaFlaps;
    }

    public float getAoaElevator() {
        return aoaElevator;
    }

    public float getAoaRudder() {
        return aoaRudder;
    }
    public void setParametersController(ParametersScreenController controller) {
        this.parameters = controller;
    }
    public float getAirSpeed() {
        return this.Speed;
    }
    public MathVector getThrust() {
        return this.Thrust;
    }
}



/**


 AirplaneElements[0].Mass = 6.56f;
 AirplaneElements[0].Position =new MathVector(14.5f, 12.0f, 2.5f);
 AirplaneElements[0].LocalInertia = new MathVector(13.92f ,  10.50f, 24.00f);
 AirplaneElements[0].Incidence = -3.5f;
 AirplaneElements[0].Dihedral = 0.0f;
 AirplaneElements[0].Area = 31.2f;
 AirplaneElements[0].Flap = 0;

 AirplaneElements[1].Mass = 7.31f;
 AirplaneElements[1].Position =new MathVector(14.5f, 5.5f, 2.5f);
 AirplaneElements[1].LocalInertia = new MathVector(21.95f ,  12.22f, 33.67f);
 AirplaneElements[1].Incidence = -3.5f;
 AirplaneElements[1].Dihedral = 0.0f;
 AirplaneElements[1].Area = 36.4f;
 AirplaneElements[1].Flap = 0;

 AirplaneElements[2].Mass = 7.31f;
 AirplaneElements[2].Position = new MathVector(14.5f, -5.5f, 2.5f);
 AirplaneElements[2].LocalInertia =new MathVector(21.95f,   12.22f, 33.67f);
 AirplaneElements[2].Incidence = -3.5f;
 AirplaneElements[2].Dihedral = 0.0f;
 AirplaneElements[2].Area = 36.4f;
 AirplaneElements[2].Flap = 0;

 AirplaneElements[3].Mass = 6.56f;
 AirplaneElements[3].Position = new MathVector(14.5f, -12.0f, 2.5f);
 AirplaneElements[3].LocalInertia = new MathVector(13.92f,  10.50f, 24.00f);
 AirplaneElements[3].Incidence = -3.5f;
 AirplaneElements[3].Dihedral = 0.0f;
 AirplaneElements[3].Area = 31.2f;
 AirplaneElements[3].Flap = 0;

 AirplaneElements[4].Mass = 2.62f;
 AirplaneElements[4].Position = new MathVector(3.03f, 2.5f, 3.0f);
 AirplaneElements[4].LocalInertia = new MathVector(0.837f,   0.385f, 1.206f);
 AirplaneElements[4].Incidence = 0.0f;
 AirplaneElements[4].Dihedral = 0.0f;
 AirplaneElements[4].Area = 10.8f;
 AirplaneElements[4].Flap = 0;

 AirplaneElements[5].Mass = 2.62f;
 AirplaneElements[5].Position = new MathVector(3.03f, -2.5f, 3.0f);
 AirplaneElements[5].LocalInertia = new MathVector(0.837f,   0.385f, 1.206f);
 AirplaneElements[5].Incidence = 0.0f;
 AirplaneElements[5].Dihedral = 0.0f;
 AirplaneElements[5].Area = 10.8f;
 AirplaneElements[5].Flap = 0;

 AirplaneElements[6].Mass = 2.93f;
 AirplaneElements[6].Position =new MathVector(2.25f, 0.0f, 5.0f);
 AirplaneElements[6].LocalInertia =new MathVector(1.262f ,  1.942f, 0.718f);
 AirplaneElements[6].Incidence = 0.0f;
 AirplaneElements[6].Dihedral = 90.0f;
 AirplaneElements[6].Area = 12.0f;
 AirplaneElements[6].Flap = 0;

 AirplaneElements[7].Mass = 31.8f;
 AirplaneElements[7].Position = new MathVector(15.25f, 0.0f, 1.5f);
 AirplaneElements[7].LocalInertia = new MathVector(66.30f,  861.9f, 861.9f);
 AirplaneElements[7].Incidence = 0.0f;
 AirplaneElements[7].Dihedral = 0.0f;
 AirplaneElements[7].Area = 84.0f;
 AirplaneElements[7].Flap = 0;
 */

