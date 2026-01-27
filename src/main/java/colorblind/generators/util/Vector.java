package colorblind.generators.util;

/**
 * I implemented this class to be parallel to Processing's internal PMatrix
 * class. I would rather use a 1D float array here.
 * 
 * @author jim
 *
 */
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

    public Vector(Vector v) {
        this.v1 = v.v1;
        this.v2 = v.v2;
        this.v3 = v.v3;
    }

    public float get(int index) {
        switch (index) {
        case 1:
            return v1;
        case 2:
            return v2;
        case 3:
            return v3;
        default:
            throw new RuntimeException("Index must be 1, 2, or 3.");
        }
    }

    public void set(int index, float value) {
        switch (index) {
        case 1:
            v1 = value;
            break;
        case 2:
            v2 = value;
            break;
        case 3:
            v3 = value;
            break;
        default:
            throw new RuntimeException("Index must be 1, 2, or 3.");
        }
    }

    public Vector add(Vector that) {
        Vector out = new Vector(v1 + that.v1, v2 + that.v2, v3 + that.v3);

        return out;
    }

    public Vector sub(Vector that) {
        Vector out = new Vector(v1 - that.v1, v2 - that.v2, v3 - that.v3);

        return out;
    }

    public float dot(Vector that) {
        return v1 * that.v1 + v2 * that.v2 + v3 * that.v3;
    }

    @Override
    public String toString() {
        return "Vector(" + v1 + ", " + v2 + ", " + v3 + ")";
    }
}
