package colorblind.generators.util;

/**
 * I implemented this class to be parallel to Processing's internal PMatrix
 * class. I would rather use a 2D float array here.
 * 
 * @author jim
 *
 */
public class Matrix {

    public float r1c1;
    public float r1c2;
    public float r1c3;
    public float r2c1;
    public float r2c2;
    public float r2c3;
    public float r3c1;
    public float r3c2;
    public float r3c3;

    public Matrix(float r1c1, float r1c2, float r1c3, float r2c1, float r2c2,
            float r2c3, float r3c1, float r3c2, float r3c3) {
        this.r1c1 = r1c1;
        this.r1c2 = r1c2;
        this.r1c3 = r1c3;
        this.r2c1 = r2c1;
        this.r2c2 = r2c2;
        this.r2c3 = r2c3;
        this.r3c1 = r3c1;
        this.r3c2 = r3c2;
        this.r3c3 = r3c3;
    }

    public Matrix(Matrix m) {
        this.r1c1 = m.r1c1;
        this.r1c2 = m.r1c2;
        this.r1c3 = m.r1c3;
        this.r2c1 = m.r2c1;
        this.r2c2 = m.r2c2;
        this.r2c3 = m.r2c3;
        this.r3c1 = m.r3c1;
        this.r3c2 = m.r3c2;
        this.r3c3 = m.r3c3;
    }

    public Vector rightMult(Vector v) {
        Vector out = new Vector();

        out.v1 = r1c1 * v.v1 + r1c2 * v.v2 + r1c3 * v.v3;
        out.v2 = r2c1 * v.v1 + r2c2 * v.v2 + r2c3 * v.v3;
        out.v3 = r3c1 * v.v1 + r3c2 * v.v2 + r3c3 * v.v3;

        return out;
    }

}
