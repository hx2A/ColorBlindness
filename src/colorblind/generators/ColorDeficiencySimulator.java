package colorblind.generators;

import colorblind.ColorUtilities;
import colorblind.Deficiency;
import colorblind.generators.util.Matrix;
import colorblind.generators.util.Vector;

/**
 * Optimized implementation of a Daltonize algorithm.
 * 
 * This works by precomputing a set of lookup tables for every possible color.
 * 
 * The lookup tables will use 64 MB of RAM.
 * 
 * Without these optimizations it would not be possible to use this algorithm in
 * real time.
 * 
 * @author James Schmitz
 *
 */

public class ColorDeficiencySimulator extends ColorTranformGenerator {

    public ColorDeficiencySimulator(Deficiency colorBlindness) {
        super(colorBlindness);
    }

    public static ColorDeficiencySimulator createSimulator(Deficiency colorBlindness) {
        switch (colorBlindness) {
        case PROTANOPE:
            return new ColorDeficiencySimulator(Deficiency.PROTANOPE);
        case DEUTERANOPE:
            return new ColorDeficiencySimulator(Deficiency.DEUTERANOPE);
        case TRITANOPE:
            return new ColorDeficiencySimulator(Deficiency.TRITANOPE);
        case ACHROMATOPE:
            return new ColorDeficiencySimulator(Deficiency.ACHROMATOPE);
        default:
            throw new RuntimeException("Unknown color blindness deficiency");
        }
    }

    protected int[] precalcAchromatopeColorMap(float amount) {
        int[] colorMap = new int[256 * 256 * 256];
        for (int color = 0; color < colorMap.length; ++color) {
            int startRed = (color & 0x00FF0000) >> 16;
            int startGreen = (color & 0x0000FF00) >> 8;
            int startBlue = (color & 0x000000FF);

            // Remove gamma correction using fast lookup table.
            float linRed = removeGammaCorrectionLUT[startRed];
            float linGreen = removeGammaCorrectionLUT[startGreen];
            float linBlue = removeGammaCorrectionLUT[startBlue];

            int simRed = applyGammaCorrectionLUT[(int) (ColorUtilities
                    .clip(linRed * ColorUtilities.achromatopeSim.v1 + linGreen
                            * ColorUtilities.achromatopeSim.v2 + linBlue
                            * ColorUtilities.achromatopeSim.v3) * (MAX_ENCODED_VALUE - 1))];
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

    protected int[] precalcDichromaticColorMap(Matrix sim, float amount) {
        int[] colorMap = new int[256 * 256 * 256];
        for (int color = 0; color < colorMap.length; ++color) {
            int startRed = (color & 0x00FF0000) >> 16;
            int startGreen = (color & 0x0000FF00) >> 8;
            int startBlue = (color & 0x000000FF);

            // Remove gamma correction using fast lookup table.
            float linRed = removeGammaCorrectionLUT[startRed];
            float linGreen = removeGammaCorrectionLUT[startGreen];
            float linBlue = removeGammaCorrectionLUT[startBlue];

            Vector lms = ColorUtilities.convertLinearRGB2LMS(new Vector(linRed,
                    linGreen, linBlue));
            Vector simRGB = ColorUtilities.convertLMS2LinearRGB(sim
                    .rightMult(lms));

            // Apply gamma correction using fast lookup table
            int simRed = applyGammaCorrectionLUT[(int) (ColorUtilities
                    .clip(simRGB.v1) * (MAX_ENCODED_VALUE - 1))];
            int simGreen = applyGammaCorrectionLUT[(int) (ColorUtilities
                    .clip(simRGB.v2) * (MAX_ENCODED_VALUE - 1))];
            int simBlue = applyGammaCorrectionLUT[(int) (ColorUtilities
                    .clip(simRGB.v3) * (MAX_ENCODED_VALUE - 1))];

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
