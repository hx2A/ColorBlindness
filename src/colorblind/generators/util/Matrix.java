package colorblind.generators.util;

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

    public Matrix(int r1c1, int r1c2, int r1c3, int r2c1, int r2c2, int r2c3,
            int r3c1, int r3c2, int r3c3) {
        this.r1c1 = r1c1 / 1000f;
        this.r1c2 = r1c2 / 1000f;
        this.r1c3 = r1c3 / 1000f;
        this.r2c1 = r2c1 / 1000f;
        this.r2c2 = r2c2 / 1000f;
        this.r2c3 = r2c3 / 1000f;
        this.r3c1 = r3c1 / 1000f;
        this.r3c2 = r3c2 / 1000f;
        this.r3c3 = r3c3 / 1000f;
    }

    public Vector rightMult(Vector v) {
        Vector out = new Vector();

        out.v1 = r1c1 * v.v1 + r1c2 * v.v2 + r1c3 * v.v3;
        out.v2 = r2c1 * v.v1 + r2c2 * v.v2 + r2c3 * v.v3;
        out.v3 = r3c1 * v.v1 + r3c2 * v.v2 + r3c3 * v.v3;

        return out;
    }

}
