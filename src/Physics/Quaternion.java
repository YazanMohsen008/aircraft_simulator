package Physics;

public class Quaternion {
    final float	tol = 0.0001f;		// float type tolerance

   public float n; // number (scalar) part
    public MathVector v=new MathVector(); // vector part: v.x, v.y, v.z

    public Quaternion() {
}
    public Quaternion(float n, MathVector v) {
        this.n = n;
        this.v = v;
    }

    public Quaternion(float n, float x,float y,float z) {
        this.n = n;
        this.v.x = x;
        this.v.y = y;
        this.v.z = z;
    }

    public float Magnitude()
    {
        return (float) Math.sqrt(n*n + v.x*v.x + v.y*v.y + v.z*v.z);
    }

    public MathVector GetVector()
    {
        return new MathVector(v.x, v.y, v.z);
    }

    public float GetScalar()
    {
        return n;
    }

    public void Add(Quaternion q) {
        n += q.n;
        v.x += q.v.x;
        v.y += q.v.y;
        v.z += q.v.z;
    }

    public void Sub(Quaternion q) {
        n -= q.n;
        v.x -= q.v.x;
        v.y -= q.v.y;
        v.z -= q.v.z;
    }

    public void Mult(float q) {
        n   *= q;
        v.x *= q;
        v.y *= q;
        v.z *= q;
    }

    public void Div(float q) {
        n /= q;
        v.x /= q;
        v.y /= q;
        v.z /= q;
    }

    public Quaternion Conjugate() { return new  Quaternion( n, new MathVector( -v.x, -v.y, -v.z)); }

    public void Mult(Quaternion q2) {
        Quaternion q1=this.copy();
        n=          q1.n*q2.n - q1.v.x*q2.v.x - q1.v.y*q2.v.y - q1.v.z*q2.v.z;
        v.x=        q1.n*q2.v.x + q1.v.x*q2.n + q1.v.y*q2.v.z - q1.v.z*q2.v.y;
        v.y=        q1.n*q2.v.y + q1.v.y*q2.n + q1.v.z*q2.v.x - q1.v.x*q2.v.z;
        v.z=        q1.n*q2.v.z + q1.v.z*q2.n + q1.v.x*q2.v.y - q1.v.y*q2.v.x;
    }
    /**
     ij=−ji=k
     jk=−kj=i
     ki=−ik=j,
	*/
    public void Mult(MathVector V) {
            Quaternion q=this.copy();
            n=    -(q.v.x*V.x + q.v.y*V.y + q.v.z*V.z);
            v.x=    q.n*V.x + q.v.y*V.z - q.v.z*V.y;
            v.y=    q.n*V.y + q.v.z*V.x - q.v.x*V.z;
            v.z=    q.n*V.z + q.v.x*V.y - q.v.y*V.x;
    }

    public float QGetAngle(Quaternion q)
    {
        return (float) (2*Math.acos(q.n));
    }

    MathVector   QGetAxis(Quaternion q) {
        MathVector v;
        float m;
        v = q.GetVector();
        m = v.Magnitude();

        if (m <= tol)
            return new MathVector(0,0,0);
        else
            v.Div(m);
        return v;
    }

    public Quaternion QRotate( Quaternion q2) {
        Quaternion q1=this;
        Quaternion q;
        q=q1.copy();
        q.Mult(q2);
        q.Mult(q1.Conjugate());
        return q;
    }

    public MathVector QVRotate(MathVector v) {

        Quaternion t1 = this.copy();
        Quaternion QQ = t1.Conjugate();

        t1.Mult(v);
        t1.Mult(QQ);

        return t1.GetVector();
    }

    public Quaternion MakeQFromEulerAngles(float x, float y, float z) {
        Quaternion q=new Quaternion();
        double roll = DegreesToRadians(x);
        double pitch = DegreesToRadians(y);
        double yaw = DegreesToRadians(z);
        double cyaw, cpitch, croll, syaw, spitch, sroll;
        double cyawcpitch, syawspitch, cyawspitch, syawcpitch;
        cyaw = Math.cos(0.5f * yaw);
        cpitch = Math.cos(0.5f * pitch);
        croll = Math.cos(0.5f * roll);
        syaw = Math.sin(0.5f * yaw);
        spitch = Math.sin(0.5f * pitch);
        sroll = Math.sin(0.5f * roll);
        cyawcpitch = cyaw*cpitch;
        syawspitch = syaw*spitch;
        cyawspitch = cyaw*spitch;
        syawcpitch = syaw*cpitch;
        q.n   = (float) (cyawcpitch * croll + syawspitch * sroll);
        q.v.x = (float) (cyawcpitch * sroll - syawspitch * croll);
        q.v.y = (float) (cyawspitch * croll + syawcpitch * sroll);
        q.v.z = (float) (syawcpitch * croll - cyawspitch * sroll);
        return q;
    }

    public MathVector MakeEulerAnglesFromQ() {
        Quaternion q=this;
        double r11, r21, r31, r32, r33, r12, r13;
        double q00, q11, q22, q33;
        double tmp;
        MathVector u=new MathVector();
        q00 = q.n * q.n;
        q11 = q.v.x * q.v.x;
        q22 = q.v.y * q.v.y;
        q33 = q.v.z * q.v.z;
        r11 = q00 + q11 - q22 - q33;
        r21 = 2 * (q.v.x*q.v.y + q.n*q.v.z);
        r31 = 2 * (q.v.x*q.v.z - q.n*q.v.y);
        r32 = 2 * (q.v.y*q.v.z + q.n*q.v.x);
        r33 = q00 - q11 - q22 + q33;
        tmp = Math.abs(r31);
        if(tmp > 0.999999)
        {
            r12 = 2 * (q.v.x*q.v.y - q.n*q.v.z);
            r13 = 2 * (q.v.x*q.v.z + q.n*q.v.y);
            u.x = RadiansToDegrees(0.0f); //roll
            u.y = RadiansToDegrees((float) Math.atan2(-r12, -r31*r13)); // yaw
            u.z = RadiansToDegrees((float) (-(Math.PI/2) * r31/tmp)); // pitch
            return u;
        }
        u.x = RadiansToDegrees((float) Math.atan2(r32, r33)); // roll
        u.y = RadiansToDegrees((float) Math.atan2(r21, r11)); // yaw
        u.z = RadiansToDegrees((float) Math.asin(-r31)); // pitch
        return u;
    }
    public Quaternion copy() {

        MathVector v1 = v.copy();
        return  new Quaternion(n,v1);
    }

    public  float  DegreesToRadians(float deg) {

        return (float) (deg * Math.PI) / 180.0f;
    }

    private float  RadiansToDegrees(float rad) { return (rad * 180.0f) / (float) Math.PI; }
    public  String toString (){
        return  ("\nn: "+n+" v.x: "+v.x+" v.y: " + v.y +" v.z: "+v.z);
    }

}
