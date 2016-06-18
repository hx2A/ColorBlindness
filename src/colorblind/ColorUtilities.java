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
    public static Vector convertRGB2LMS(Vector rgb) {
        return rgb2lms.rightMult(rgb);
    }

    public static Vector convertLMS2RGB(Vector lmsColor) {
        return lms2rgb.rightMult(lmsColor);
    }

    public static Vector convertPColor2LMS(int color) {
        int sr = (color & 0x00FF0000) >> 16;
        int sg = (color & 0x0000FF00) >> 8;
        int sb = (color & 0x000000FF);

        // Remove gamma correction.
        float pow_r = removeGammaCorrectionStandardRGB(sr / 255f);
        float pow_g = removeGammaCorrectionStandardRGB(sg / 255f);
        float pow_b = removeGammaCorrectionStandardRGB(sb / 255f);

        Vector c = new Vector(pow_r, pow_g, pow_b);

        return convertRGB2LMS(c);
    }

    public static int convertLMS2PColor(Vector lmsColor) {
        Vector rgbColor = convertLMS2RGB(lmsColor);

        int simRed = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v1)));
        int simGreen = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v2)));
        int simBlue = (int) (255 * applyGammaCorrectionStandardRGB(ColorUtilities
                .clip(rgbColor.v3)));

        return 0xFF000000 | (simRed << 16) | (simGreen << 8) | simBlue;
    }

    public static int confusingColor(Deficiency colorBlindness, int color,
            float x) {
        Vector lms = convertPColor2LMS(color);

        switch (colorBlindness) {
        case PROTANOPE:
            float minL = (0 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c1;
            float maxL = (1 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
                    / lms2rgb.r1c1;
            lms.v1 = minL + (clip(x) * (maxL - minL));

            return convertLMS2PColor(lms);

        case DEUTERANOPE:
            float minM = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c2;
            float maxM = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
                    / lms2rgb.r2c2;
            lms.v2 = minM + (clip(x) * (maxM - minM));

            return convertLMS2PColor(lms);

        case TRITANOPE:
            float minS = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
                    / lms2rgb.r3c3;
            float maxS = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
                    / lms2rgb.r3c3;
            lms.v3 = minS + (clip(x) * (maxS - minS));

            return convertLMS2PColor(lms);

        case ACHROMATOPE:
            throw new RuntimeException("Not yet implemented.");

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
}
