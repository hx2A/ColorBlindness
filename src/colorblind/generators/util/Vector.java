package colorblind.generators.util;

public class Vector {

    public float v1;
    public float v2;
    public float v3;

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(float v1, float v2, float v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Vector add(Vector that) {
        Vector out = new Vector(v1 + that.v1, v2 + that.v2, v3 + that.v3);

        return out;
    }

    public Vector sub(Vector that) {
        Vector out = new Vector(v1 - that.v1, v2 - that.v2, v3 - that.v3);

        return out;
    }
}
