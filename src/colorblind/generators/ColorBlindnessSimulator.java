package colorblind.generators;

import colorblind.ColorUtilities;
import colorblind.generators.util.Matrix;
import colorblind.generators.util.Vector;

/**
 * Optimized implementation of a Daltonize algorithm.
 * 
 * This works by precomputing a set of lookup tables for every possible color.
 * 
 * The lookup tables will use 256 MB of RAM.
 * 
 * Without these optimizations it would not be possible to use this algorithm in
 * real time.
 * 
 * @author James Schmitz
 *
 */

public class ColorBlindnessSimulator extends ColorTranformGenerator {

    public static final int PROTANOPE = 0;
    public static final int DEUTERANOPE = 1;
    public static final int TRITANOPE = 2;
    public static final int ACHROMATOPE = 3;

    private int colorBlindness;

    private float[] removeGammaCorrectionLUT;
    private int[] applyGammaCorrectionLUT;
    private final int MAX_ENCODED_VALUE = (int) Math.pow(2, 10);

    public ColorBlindnessSimulator(int colorBlindness, float amount) {
        System.out.println("precomputing lookup tables...");

        removeGammaCorrectionLUT = preComputeRemoveGammaCorrectionStandardrgbLUT();
        applyGammaCorrectionLUT = preComputeApplyGammaCorrectionStandardrgbLUT(MAX_ENCODED_VALUE);

        this.colorBlindness = colorBlindness;
        setAmount(clip(amount));
    }

    public ColorBlindnessSimulator(int colorBlindness) {
        this(colorBlindness, 1);
    }

    public ColorBlindnessSimulator setAmount(float amount) {
        switch (colorBlindness) {
        case ACHROMATOPE:
            colorMap = precalcAchromatopeColorMap(amount);
            break;
        case PROTANOPE:
            colorMap = precalcDichromaticColorMap(protanopeSim, amount);
            break;
        case DEUTERANOPE:
            colorMap = precalcDichromaticColorMap(deuteranopeSim, amount);
            break;
        case TRITANOPE:
            colorMap = precalcDichromaticColorMap(tritanopeSim, amount);
            break;
        default:
            throw new RuntimeException("ERROR: Unknown color deficiency");
        }
        return this;
    }

    private int[] precalcAchromatopeColorMap(float amount) {
        int[] colorMap = new int[256 * 256 * 256];
        for (int color = 0; color < colorMap.length; ++color) {
            int startRed = (color & 0x00FF0000) >> 16;
            int startGreen = (color & 0x0000FF00) >> 8;
            int startBlue = (color & 0x000000FF);

            // Remove gamma correction using fast lookup table.
            float linRed = removeGammaCorrectionLUT[startRed];
            float linGreen = removeGammaCorrectionLUT[startGreen];
            float linBlue = removeGammaCorrectionLUT[startBlue];

            int simRed = applyGammaCorrectionLUT[(int) (clip((float) (linRed
                    * 0.212656 + linGreen * 0.715158 + linBlue * 0.072186)) * (MAX_ENCODED_VALUE - 1))];
            int simGreen = simRed;
            int simBlue = simRed;

            // Anomylize colors
            int finalRed = (int) (startRed * (1 - amount) + simRed * amount);
            int finalGreen = (int) (startGreen * (1 - amount) + simGreen
                    * amount);
            int finalBlue = (int) (startBlue * (1 - amount) + simBlue * amount);

            colorMap[color] = (finalRed << 16) | (finalGreen << 8) | finalBlue;
        }

        return colorMap;
    }

    private int[] precalcDichromaticColorMap(Matrix sim, float amount) {
        int[] colorMap = new int[256 * 256 * 256];
        for (int color = 0; color < colorMap.length; ++color) {
            int startRed = (color & 0x00FF0000) >> 16;
            int startGreen = (color & 0x0000FF00) >> 8;
            int startBlue = (color & 0x000000FF);

            // Remove gamma correction using fast lookup table.
            float linRed = removeGammaCorrectionLUT[startRed];
            float linGreen = removeGammaCorrectionLUT[startGreen];
            float linBlue = removeGammaCorrectionLUT[startBlue];

            Vector lms = ColorUtilities.convertRGB2LMS(new Vector(linRed,
                    linGreen, linBlue));
            Vector simRGB = ColorUtilities.convertLMS2RGB(sim.rightMult(lms));

            // Apply gamma correction using fast lookup table
            int simRed = applyGammaCorrectionLUT[(int) (clip(simRGB.v1) * (MAX_ENCODED_VALUE - 1))];
            int simGreen = applyGammaCorrectionLUT[(int) (clip(simRGB.v2) * (MAX_ENCODED_VALUE - 1))];
            int simBlue = applyGammaCorrectionLUT[(int) (clip(simRGB.v3) * (MAX_ENCODED_VALUE - 1))];

            // Anomylize colors
            int finalRed = (int) (startRed * (1.0 - amount) + simRed * amount);
            int finalGreen = (int) (startGreen * (1.0 - amount) + simGreen
                    * amount);
            int finalBlue = (int) (startBlue * (1.0 - amount) + simBlue
                    * amount);

            colorMap[color] = (finalRed << 16) | (finalGreen << 8) | finalBlue;
        }

        return colorMap;
    }
}
