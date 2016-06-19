package colorblind;

import colorblind.generators.util.Matrix;
import colorblind.generators.util.Vector;

public class ColorUtilities {

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @return
     */
    public static float clip(float x) {
        return Math.min(Math.max(x, 0), 1);
    }

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @param min
     * @param max
     * @return
     */
    public static float clip(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    /*
     * Simulation Matrices
     */
    public static Matrix protanopeSim = new Matrix(0.0f, 1.05118294f, -0.05116099f,
            0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);

    public static Matrix deuteranopeSim = new Matrix(1.0f, 0.0f, 0.0f, 0.9513092f,
            0.0f, 0.04866992f, 0.0f, 0.0f, 1.0f);

    public static Matrix tritanopeSim = new Matrix(1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, -19.54614229f, 20.5465713f, 0.0f);

    public static Vector achromatopeSim = new Vector(0.212656f, 0.715158f,
            0.072186f);

    /*
     * Daltonize Matrices
     */
    public static Matrix shiftTowardsVisible = new Matrix(0.0f, 0.0f, 0.0f, 0.7f,
            1.0f, 0.0f, 0.7f, 0.0f, 1.0f);

    private static Matrix rgb2lms = new Matrix(0.31399022f, 0.63951294f,
            0.04649755f, 0.15537241f, 0.75789446f, 0.08670142f, 0.01775239f,
            0.10944209f, 0.87256922f);

    private static Matrix lms2rgb = new Matrix(5.47221206f, -4.6419601f,
            0.16963708f, -1.1252419f, 2.29317094f, -0.1678952f, 0.02980165f,
            -0.19318073f, 1.16364789f);

    /**
     * Apply Gamma Correction
     * 
     * @param s
     *            = value [0, 1]
     * @return
     */
    public static float applyGammaCorrection(float s, float gamma) {
        return (float) Math.pow(s, 1 / gamma);
    }

    /**
     * Remove Gamma Correction
     * 
     * @param s
     *            = value [0, 1]
     * @return
     */
    public static float removeGammaCorrection(float s, float gamma) {
        return (float) Math.pow(s, gamma);
    }

    /**
     * Apply Standard Gamma Correction
     * 
     * @param s
     *            = value [0, 1]
     * @return
     */
    public static float applyGammaCorrectionStandardRGB(float s) {
        if (s <= 0.0031308) {
            return 12.92f * s;
        } else {
            return (float) (1.055 * Math.pow(s, 0.41666) - 0.055);
        }
    }

    /**
     * Remove Standard Gamma Correction
     * 
     * @param s
     *            = value [0, 1]
     * @return
     */
    public static float removeGammaCorrectionStandardRGB(float s) {
        if (s <= 0.04045f) {
            return s / 12.92f;
        } else {
            return (float) Math.pow((s + 0.055) / 1.055, 2.4);
        }
    }

    /*
     * LMS <--> RGB conversion functions
     */
    public static Vector convertLinearRGB2LMS(Vector rgbColor) {
        return rgb2lms.rightMult(rgbColor);
    }

    public static Vector convertLMS2LinearRGB(Vector lmsColor) {
        return lms2rgb.rightMult(lmsColor);
    }

    public static Vector convertPColor2LinearRGB(int color) {
        int sr = (color & 0x00FF0000) >> 16;
        int sg = (color & 0x0000FF00) >> 8;
        int sb = (color & 0x000000FF);

        // Remove gamma correction.
        float pow_r = removeGammaCorrectionStandardRGB(sr / 255f);
        float pow_g = removeGammaCorrectionStandardRGB(sg / 255f);
        float pow_b = removeGammaCorrectionStandardRGB(sb / 255f);

        return new Vector(pow_r, pow_g, pow_b);
    }

    public static Vector convertPColor2LMS(int color) {
        return convertLinearRGB2LMS(convertPColor2LinearRGB(color));
    }

    public static int convertLinearRGB2PColor(Vector rgbColor) {
        int simRed = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v1)));
        int simGreen = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v2)));
        int simBlue = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v3)));

        return 0xFF000000 | (simRed << 16) | (simGreen << 8) | simBlue;
    }

    public static int convertLMS2PColor(Vector lmsColor) {
        return convertLinearRGB2PColor(convertLMS2LinearRGB(lmsColor));
    }

    public static int confusingColor(Deficiency colorBlindness, int color,
            float x) {
        Vector lms = convertPColor2LMS(color);

        float[] boundaries = new float[6];

        switch (colorBlindness) {
        case PROTANOPE:
            boundaries[0] = (0 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c1;
            boundaries[1] = (0 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c1;
            boundaries[2] = (0 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
                    / lms2rgb.r3c1;

            boundaries[3] = (1 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c1;
            boundaries[4] = (1 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c1;
            boundaries[5] = (1 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
                    / lms2rgb.r3c1;

            float minL = Float.MIN_VALUE;
            float maxL = Float.MAX_VALUE;

            for (float boundary : boundaries) {
                if (boundary < lms.v1)
                    minL = Math.max(minL, boundary);
                else
                    maxL = Math.min(maxL, boundary);
            }

            lms.v1 = minL + (clip(x) * (maxL - minL));

            return convertLMS2PColor(lms);

        case DEUTERANOPE:
            boundaries[0] = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c2;
            boundaries[1] = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c2;
            boundaries[2] = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
                    / lms2rgb.r3c2;

            boundaries[3] = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c2;
            boundaries[4] = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c2;
            boundaries[5] = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
                    / lms2rgb.r3c2;

            float minM = Float.MIN_VALUE;
            float maxM = Float.MAX_VALUE;

            for (float boundary : boundaries) {
                if (boundary < lms.v1)
                    minM = Math.max(minM, boundary);
                else
                    maxM = Math.min(maxM, boundary);
            }

            lms.v2 = minM + (clip(x) * (maxM - minM));

            return convertLMS2PColor(lms);

        case TRITANOPE:
            boundaries[0] = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
                    / lms2rgb.r1c3;
            boundaries[1] = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
                    / lms2rgb.r2c3;
            boundaries[2] = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
                    / lms2rgb.r3c3;

            boundaries[3] = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
                    / lms2rgb.r1c3;
            boundaries[4] = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
                    / lms2rgb.r2c3;
            boundaries[5] = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
                    / lms2rgb.r3c3;

            float minS = Float.MIN_VALUE;
            float maxS = Float.MAX_VALUE;

            for (float boundary : boundaries) {
                if (boundary < lms.v1)
                    minS = Math.max(minS, boundary);
                else
                    maxS = Math.min(maxS, boundary);
            }

            lms.v3 = minS + (clip(x) * (maxS - minS));

            return convertLMS2PColor(lms);

        case ACHROMATOPE:
            throw new RuntimeException(
                    "Not implemented. Use confusingAchromatopeColor instead.");

        default:
            throw new RuntimeException("Unknown Color Deficiency");
        }
    }

    public static int confusingProtanopeColor(int color, float x) {
        return confusingColor(Deficiency.PROTANOPE, color, x);
    }

    public static int confusingDeuteranopeColor(int color, float x) {
        return confusingColor(Deficiency.DEUTERANOPE, color, x);
    }

    public static int confusingTritanopeColor(int color, float x) {
        return confusingColor(Deficiency.TRITANOPE, color, x);
    }

    public static int confusingAchromatopeColor(int color, float x1, float x2) {
        Vector rgb = convertPColor2LinearRGB(color);

        float Z = rgb.dot(achromatopeSim);
        Vector rgb2 = new Vector();

        rgb2.v1 = clip(x1);

        float minG2 = (Z - 0.212656f * rgb2.v1 - 0.072186f * 1) / 0.715158f;
        float maxG2 = (Z - 0.212656f * rgb2.v1) / 0.715158f;
        rgb2.v2 = minG2 + clip(x2) * (maxG2 - minG2);
        rgb2.v3 = (Z - 0.212656f * rgb2.v1 - 0.072186f * rgb2.v2) / 0.072186f;

        return convertLinearRGB2PColor(rgb2);
    }
}
