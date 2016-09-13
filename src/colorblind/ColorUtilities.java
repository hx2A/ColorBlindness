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
    public static Matrix protanopiaSim = new Matrix(0.0f, 1.05118294f,
            -0.05116099f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);

    public static Matrix deuteranopiaSim = new Matrix(1.0f, 0.0f, 0.0f,
            0.9513092f, 0.0f, 0.04866992f, 0.0f, 0.0f, 1.0f);

    public static Matrix tritanopiaSim = new Matrix(1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, -0.86744736f, 1.86727089f, 0.0f);

    public static Vector achromatopsiaSim = new Vector(0.212656f, 0.715158f,
            0.072186f);

    public static Vector blueConeMonochromacySim = new Vector(0.01775f,
            0.10945f, 0.87262f);

    public static Matrix customSim = new Matrix(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f,
            1f);

    /*
     * Daltonize Matrices
     * 
     * public so the user can change them if desired.
     */
    public static Matrix shiftTowardsVisible = new Matrix(0.0f, 0.0f, 0.0f,
            0.7f, 1.0f, 0.0f, 0.7f, 0.0f, 1.0f);

    public static Matrix rgb2lms = new Matrix(0.31399022f, 0.63951294f,
            0.04649755f, 0.15537241f, 0.75789446f, 0.08670142f, 0.01775239f,
            0.10944209f, 0.87256922f);

    public static Matrix lms2rgb = new Matrix(5.47221206f, -4.6419601f,
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
     * @param colorBlindness
     *            color deficiency
     * @param color
     *            initial color
     * @param x
     *            float [0, 1]
     * @return new color that is determined by x
     */
    public static int confusingDichromaticColor(Deficiency colorBlindness,
            int color, float x) {
        if ((color & 0xFFFFFF) == 0)
            return color;

        Vector lms = convertPColor2LMS(color);

        int index = 0;
        switch (colorBlindness) {
        case PROTANOPIA:
            index = 1;
            break;
        case DEUTERANOPIA:
            index = 2;
            break;
        case TRITANOPIA:
            index = 3;
            break;
        case ACHROMATOPSIA:
        case BLUE_CONE_MONOCHROMACY:
            throw new RuntimeException("Use confusingMonochromaticColor for "
                    + colorBlindness.toString().toLowerCase()
                    + " confusing colors.");
        case CUSTOM:
            throw new RuntimeException("Custom confusing colors not supported.");
        default:
            throw new RuntimeException(
                    "Unknown Color Deficiency - please report");
        }

        float lower = lmsFeasibleBisectionSearch(lms, index, -1);
        float upper = lmsFeasibleBisectionSearch(lms, index, 1);

        lms.set(index, lower + (clip(x) * (upper - lower)));

        return convertLMS2PColor(lms);
    }

    public static int confusingProtanopiaColor(int color, float x) {
        return confusingDichromaticColor(Deficiency.PROTANOPIA, color, x);
    }

    public static int confusingDeuteranopiaColor(int color, float x) {
        return confusingDichromaticColor(Deficiency.DEUTERANOPIA, color, x);
    }

    public static int confusingTritanopiaColor(int color, float x) {
        return confusingDichromaticColor(Deficiency.TRITANOPIA, color, x);
    }

    /**
     * Search the LMS color space for the feasibility edge.
     * 
     * If I convert a Processing color to LMS color space, how far can I adjust
     * one of the 3 values until it can no longer be converted back to a valid
     * Processing color? This function answers that question.
     * 
     * Using a bisection search, find the min or max value of one value in the
     * lms vector that is on the edge of the range that converts back to a
     * feasible vector in linear RGB space.
     * 
     * @param lms
     *            Vector of color in LMS space
     * @param index
     *            index into lms vector to change
     * @param direction
     *            1 or -1 to search in bigger or smaller direction
     * @return feasible value on edge of feasibility
     */
    public static float lmsFeasibleBisectionSearch(Vector lms, int index,
            int direction) {
        if (!(direction == 1 || direction == -1)) {
            throw new RuntimeException("direction must be 1 or -1");
        }

        Vector lmsLower = new Vector(lms);
        Vector lmsUpper = new Vector(lms); // outside feasible LMS region
        lmsUpper.set(index, Math.signum(direction) * 10);
        Vector lmsMid = new Vector(lms);
        lmsMid.set(index, (lmsLower.get(index) + lmsUpper.get(index)) / 2);

        float epsilon = 0.00001f;
        while (Math.abs(lmsUpper.get(index) - lmsLower.get(index)) > epsilon) {
            Vector test = convertLMS2LinearRGB(lmsMid);
            float max = Math.max(Math.max(test.v1, test.v2), test.v3);
            float min = Math.min(Math.min(test.v1, test.v2), test.v3);

            if (max > 1f || min < 0f) {
                // outside feasible region
                lmsMid.set(index, lmsMid.get(index) - epsilon * direction);
                test = convertLMS2LinearRGB(lmsMid);
                max = Math.max(Math.max(test.v1, test.v2), test.v3);
                min = Math.min(Math.min(test.v1, test.v2), test.v3);

                if (max > 1f || min < 0f) {
                    // still outside feasible region, so not near boundary
                    lmsUpper = lmsMid;
                    lmsMid.set(index,
                            (lmsLower.get(index) + lmsUpper.get(index)) / 2);
                } else {
                    // inside feasible region, so near boundary
                    return lmsMid.get(index);
                }
            } else {
                // inside feasible region
                lmsLower = lmsMid;
                lmsMid.set(index,
                        (lmsLower.get(index) + lmsUpper.get(index)) / 2);
            }
        }

        return lmsMid.get(index);
    }

    /**
     * Intentionally generate colors that would be confusing to someone with
     * monochromatic vision.
     * 
     * If a plot of all possible colors were a 3D space, there would be planes
     * of colors in the 3D space that would look identical to a monochromat
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
    public static int confusingMonochromaticColor(Deficiency colorBlindness,
            int color, float x1, float x2) {
        Vector sim;
        switch (colorBlindness) {
        case ACHROMATOPSIA:
            sim = achromatopsiaSim;
            break;
        case BLUE_CONE_MONOCHROMACY:
            sim = blueConeMonochromacySim;
            break;
        case PROTANOPIA:
        case DEUTERANOPIA:
        case TRITANOPIA:
            throw new RuntimeException("Use confusingDichromaticColor for "
                    + colorBlindness.toString().toLowerCase()
                    + " confusing colors.");
        case CUSTOM:
            throw new RuntimeException("Custom confusing colors not supported.");

        default:
            throw new RuntimeException(
                    "Unknown Color Deficiency - please report");
        }

        Vector rgb = convertPColor2LinearRGB(color);

        float Z = rgb.dot(sim);
        Vector rgb2 = new Vector();

        float minR2 = 0;
        float maxR2 = Math.min(1, Z / sim.v1);
        rgb2.v1 = minR2 + clip(x1) * (maxR2 - minR2);

        float minG2 = Math.max(0, (Z - sim.v1 * rgb2.v1 - sim.v3 * 1) / sim.v2);
        float maxG2 = Math.min(1, (Z - sim.v1 * rgb2.v1) / sim.v2);
        rgb2.v2 = minG2 + clip(x2) * (maxG2 - minG2);
        rgb2.v3 = (Z - sim.v1 * rgb2.v1 - sim.v2 * rgb2.v2) / sim.v3;

        return convertLinearRGB2PColor(rgb2);
    }
}
