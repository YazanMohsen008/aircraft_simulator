package Physics;

public class Matrix3x3 {
    public float e11,e12,e13, e21,e22,e23, e31,e32,e33;

    public Matrix3x3(float e11, float e12, float e13, float e21, float e22, float e23, float e31, float e32, float e33) {
        this.e11 = e11;
        this.e12 = e12;
        this.e13 = e13;
        this.e21 = e21;
        this.e22 = e22;
        this.e23 = e23;
        this.e31 = e31;
        this.e32 = e32;
        this.e33 = e33;
    }

    public Matrix3x3() {
    }

    public float     det() {
        return  e11*e22*e33 -
                e11*e32*e23 +
                e21*e32*e13 -
                e21*e12*e33 +
                e31*e12*e23 -
                e31*e22*e13;
    }
    public Matrix3x3 Transpose()
    {
        return new Matrix3x3(e11,e21,e31,e12,e22,e32,e13,e23,e33);
    }

    public Matrix3x3 Inverse() {
           float d = e11*e22*e33 -
                     e11*e32*e23 +
                     e21*e32*e13 -
                     e21*e12*e33 +
                     e31*e12*e23 -
                     e31*e22*e13;
            if (d == 0) d = 1;
            return new Matrix3x3(
                    (e22*e33-e23*e32)/d,
                    -(e12*e33-e13*e32)/d,
                    (e12*e23-e13*e22)/d,
                    -(e21*e33-e23*e31)/d,
                    (e11*e33-e13*e31)/d,
                    -(e11*e23-e13*e21)/d,
                    (e21*e32-e22*e31)/d,
                    -(e11*e32-e12*e31)/d,
                    (e11*e22-e12*e21)/d );
        }

    public void Add(Matrix3x3 m) {
        e11 += m.e11;
        e12 += m.e12;
        e13 += m.e13;
        e21 += m.e21;
        e22 += m.e22;
        e23 += m.e23;
        e31 += m.e31;
        e32 += m.e32;
        e33 += m.e33;
    }
    public void Sub(Matrix3x3 m) {
        e11 -= m.e11;
        e12 -= m.e12;
        e13 -= m.e13;
        e21 -= m.e21;
        e22 -= m.e22;
        e23 -= m.e23;
        e31 -= m.e31;
        e32 -= m.e32;
        e33 -= m.e33;
    }
    public void Mult(float m) {
        e11 *= m;
        e12 *= m;
        e13 *= m;
        e21 *= m;
        e22 *= m;
        e23 *= m;
        e31 *= m;
        e32 *= m;
        e33 *= m;
    }
    public void Div(float m) {
        e11 /= m;
        e12 /= m;
        e13 /= m;
        e21 /= m;
        e22 /= m;
        e23 /= m;
        e31 /= m;
        e32 /= m;
        e33 /= m;
    }
    public Matrix3x3  Mult(Matrix3x3 m2) {
        Matrix3x3 m1=this;
        return new Matrix3x3(
                m1.e11*m2.e11 + m1.e12*m2.e21 + m1.e13*m2.e31,
                m1.e11*m2.e12 + m1.e12*m2.e22 + m1.e13*m2.e32,
                m1.e11*m2.e13 + m1.e12*m2.e23 + m1.e13*m2.e33,
                m1.e21*m2.e11 + m1.e22*m2.e21 + m1.e23*m2.e31,
                m1.e21*m2.e12 + m1.e22*m2.e22 + m1.e23*m2.e32,
                m1.e21*m2.e13 + m1.e22*m2.e23 + m1.e23*m2.e33,
                m1.e31*m2.e11 + m1.e32*m2.e21 + m1.e33*m2.e31,
                m1.e31*m2.e12 + m1.e32*m2.e22 + m1.e33*m2.e32,
                m1.e31*m2.e13 + m1.e32*m2.e23 + m1.e33*m2.e33 );
    }
    public MathVector Mult( MathVector u) {
        Matrix3x3 m=this;
        return new MathVector(
                m.e11 * u.x + m.e12 * u.y + m.e13 * u.z,
                m.e21 * u.x + m.e22 * u.y + m.e23 * u.z,
                m.e31 * u.x + m.e32 * u.y + m.e33 * u.z);
    }

    public float getE11() {
        return e11;
    }

    public void setE11(float e11) {
        this.e11 = e11;
    }

    public float getE12() {
        return e12;
    }

    public void setE12(float e12) {
        this.e12 = e12;
    }

    public float getE13() {
        return e13;
    }

    public void setE13(float e13) {
        this.e13 = e13;
    }

    public float getE21() {
        return e21;
    }

    public void setE21(float e21) {
        this.e21 = e21;
    }

    public float getE22() {
        return e22;
    }

    public void setE22(float e22) {
        this.e22 = e22;
    }

    public float getE23() {
        return e23;
    }

    public void setE23(float e23) {
        this.e23 = e23;
    }

    public float getE31() {
        return e31;
    }

    public void setE31(float e31) {
        this.e31 = e31;
    }

    public float getE32() {
        return e32;
    }

    public void setE32(float e32) {
        this.e32 = e32;
    }

    public float getE33() {
        return e33;
    }

    public void setE33(float e33) {
        this.e33 = e33;
    }
    public  String toString (){
        return
                ("\ne11: "+e11)+
                ("\ne12: "+e12)+
                ("\ne13: "+e13)+
                ("\ne21: "+e21)+
                ("\ne22: "+e22)+
                ("\ne23: "+e23)+
                ("\ne31: "+e31)+
                ("\ne32: "+e32)+
                ("\ne33: "+e33);
    }


}
