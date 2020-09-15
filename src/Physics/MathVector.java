package Physics;

public class MathVector {
    final float	tol = 0.0001f;		// float type tolerance

    public float x,y,z;

    public MathVector(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public MathVector () {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }
    public void Add(MathVector mMathVector){
        this.x += mMathVector.getX();
        this.y += mMathVector.getY();
        this.z += mMathVector.getZ();
    }
    public void Sub(MathVector mMathVector){
        this.x -= mMathVector.getX();
        this.y -= mMathVector.getY();
        this.z -= mMathVector.getZ();
    }

    public void Sub(float value){
        this.x -= value;
        this.y -= value;
        this.z -= value;
    }

    public void Div(float n){
        this.x /= n;
        this.y /= n;
        this.z /= n;
    }
    public void Mult(float n){
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }
    public float Magnitude(){
       return (float) Math.sqrt(x*x+y*y+z*z);
    }
    public void Normalize(){
        float m = Magnitude();
        if(m <= tol) m = 1;
        x /= m;
        y /= m;
        z /= m;

        if (Math.abs(x) < tol) x = 0.0f;
        if (Math.abs(y) < tol) y = 0.0f;
        if (Math.abs(z) < tol) z = 0.0f;
    }

     public void Reverse()
    {
        x = -x;
        y = -y;
        z = -z;
    }
    public MathVector Conjugate()
    {
       return new MathVector(-x,-y,-z);
    }

    public void CrossProduct(MathVector Vector) {
        MathVector copy = this.copy();
        x=   copy.y*Vector.z - copy.z*Vector.y;
        y=  -copy.x*Vector.z + copy.z*Vector.x;
        z=   copy.x*Vector.y - copy.y*Vector.x ;
    }

    public float DotProduct(MathVector Vector) {
        return x*Vector.x+y*Vector.y+z*Vector.z;
    }

    public Matrix3x3   MakeAngularVelocityMatrix()
    {
        MathVector u=this;
        return new Matrix3x3(
                0.0f, -u.z, u.y,
                u.z, 0.0f, -u.x,
                -u.y, u.x, 0.0f);
    }
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }


    public MathVector copy() {
        return new MathVector (this.x,this.y,this.z);
    }

    public  String toString (){
        return  ("\nx: "+x+" y: " + y +" z: "+z);
    }

}
