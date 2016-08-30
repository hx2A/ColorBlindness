package colorblind;

import colorblind.generators.util.Matrix;
import colorblind.generators.util.Vector;

public class ColorUtilities {

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @return x constrained to [0, 1]
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
     * @return x constrained to [min, max]
     */
    public static float clip(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    /*
     * Simulation Matrices
     * 
     * public so the user can change them if desired.
     */
    public static Matrix protanopiaSim = new Matrix(0.0f, 0.90822864f,
            0.008192f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);

    public static Matrix deuteranopiaSim = new Matrix(1.0f, 0.0f, 0.0f,
            1.10104433f, 0.0f, -0.00901975f, 0.0f, 0.0f, 1.0f);

    public static Matrix tritanopiaSim = new Matrix(1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, -0.15773032f, 1.19465634f, 0.0f);

    public static Vector achromatopsiaSim = new Vector(0.212656f, 0.715158f,
            0.072186f);

    /*
     * Daltonize Matrices
     * 
     * public so the user can change them if desired.
     */
    public static Matrix shiftTowardsVisible = new Matrix(0.0f, 0.0f, 0.0f,
            0.7f, 1.0f, 0.0f, 0.7f, 0.0f, 1.0f);

    public static Matrix rgb2lms = new Matrix(0.3904725f, 0.54990437f,
            0.00890159f, 0.07092586f, 0.96310739f, 0.00135809f, 0.02314268f,
            0.12801221f, 0.93605194f);

    public static Matrix lms2rgb = new Matrix(2.85831110f, -1.62870796f,
            -0.0248186967f, -0.210434776f, 1.15841493f, 0.000320463334f,
            -0.0418895045f, -0.118154333f, 1.06888657f);

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
     * Processing color <--> LMS <--> linear RGB conversion functions
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

    /**
     * Intentionally generate colors that would be confusing to a colorblind
     * person.
     * 
     * If a plot of all possible colors were a 3D space, there would be lines of
     * colors in the 3D space that would look identical to a colorblind person.
     * This code attempts to pick multiple colors along those lines.
     * 
     * @param color
     *            initial color
     * @param x
     *            float [0, 1]
     * @return new color that is determined by x
     */
    public static int confusingColor(Deficiency colorBlindness, int color,
            float x) {
        Vector lms = convertPColor2LMS(color);

        float[] boundaries = new float[6];

        switch (colorBlindness) {
        case PROTANOPIA:
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

        case DEUTERANOPIA:
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
                if (boundary < lms.v2)
                    minM = Math.max(minM, boundary);
                else
                    maxM = Math.min(maxM, boundary);
            }

            lms.v2 = minM + (clip(x) * (maxM - minM));

            return convertLMS2PColor(lms);

        case TRITANOPIA:
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
                if (boundary < lms.v3)
                    minS = Math.max(minS, boundary);
                else
                    maxS = Math.min(maxS, boundary);
            }

            lms.v3 = minS + (clip(x) * (maxS - minS));

            return convertLMS2PColor(lms);

        case ACHROMATOPSIA:
            throw new RuntimeException(
                    "Not implemented. Use confusingAchromatopsiaColor instead.");

        default:
            throw new RuntimeException("Unknown Color Deficiency");
        }
    }

    public static int confusingProtanopiaColor(int color, float x) {
        return confusingColor(Deficiency.PROTANOPIA, color, x);
    }

    public static int confusingDeuteranopiaColor(int color, float x) {
        return confusingColor(Deficiency.DEUTERANOPIA, color, x);
    }

    public static int confusingTritanopiaColor(int color, float x) {
        return confusingColor(Deficiency.TRITANOPIA, color, x);
    }

    /**
     * Intentionally generate colors that would be confusing to an achromatope
     * person.
     * 
     * If a plot of all possible colors were a 3D space, there would be planes
     * of colors in the 3D space that would look identical to a colorblind
     * person. This code attempts to pick multiple colors on those planes.
     * 
     * @param color
     *            initial color
     * @param x1
     *            float [0, 1]
     * @param x2
     *            float [0, 1]
     * @return new color that is determined by x1 and x2
     */
    public static int confusingAchromatopsiaColor(int color, float x1, float x2) {
        Vector rgb = convertPColor2LinearRGB(color);

        float Z = rgb.dot(achromatopsiaSim);
        Vector rgb2 = new Vector();

        float minR2 = 0;
        float maxR2 = Math.min(1, Z / achromatopsiaSim.v1);
        rgb2.v1 = minR2 + clip(x1) * (maxR2 - minR2);

        float minG2 = Math.max(0,
                (Z - achromatopsiaSim.v1 * rgb2.v1 - achromatopsiaSim.v3 * 1)
                        / achromatopsiaSim.v2);
        float maxG2 = Math.min(1, (Z - achromatopsiaSim.v1 * rgb2.v1)
                / achromatopsiaSim.v2);
        rgb2.v2 = minG2 + clip(x2) * (maxG2 - minG2);
        rgb2.v3 = (Z - achromatopsiaSim.v1 * rgb2.v1 - achromatopsiaSim.v2
                * rgb2.v2)
                / achromatopsiaSim.v3;

        return convertLinearRGB2PColor(rgb2);
    }
}
