package colorblind.generators;

import colorblind.ColorUtilities;
import colorblind.generators.util.Matrix;

/**
 * 
 * Base class for all of the color transform generators.
 * 
 * This class provides some default matrix values and implementations of some
 * useful functions for color blindness simulations and daltonization.
 * 
 * @author James Schmitz
 *
 */
public abstract class ColorTranformGenerator extends Generator {

    protected int[] colorMap;

    public void generateTransformedFrame(int[] pixels) {
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = colorMap[pixels[i] & 0x00FFFFFF];
        }
    }

    /**
     * Simple utility function that is used in a couple of places.
     * 
     * @param x
     * @return
     */
    protected float clip(float x) {
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
    protected float clip(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    /*
     * Functions for pre-calculating look-up-tables
     */
    protected int[] preComputeApplyGammaCorrectionStandardrgbLUT(
            int maxEncodedValue) {
        int[] gammaCorrectionLUT = new int[maxEncodedValue];
        for (int s = 0; s < maxEncodedValue; ++s) {
            gammaCorrectionLUT[s] = (int) (255 * ColorUtilities
                    .applyGammaCorrectionStandardRGB(s
                            / (float) maxEncodedValue));
        }
        return gammaCorrectionLUT;
    }

    protected float[] preComputeRemoveGammaCorrectionStandardrgbLUT() {
        float[] removeGammaCorrectionLUT = new float[256];

        for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
            removeGammaCorrectionLUT[c] = ColorUtilities
                    .removeGammaCorrectionStandardRGB(c / 255f);
        }

        return removeGammaCorrectionLUT;
    }

    protected int[] preComputeApplyGammaCorrectionLUT(int maxEncodedValue) {
        int[] gammaCorrectionLUT = new int[maxEncodedValue];
        for (int s = 0; s < maxEncodedValue; ++s) {
            gammaCorrectionLUT[s] = (int) (255 * ColorUtilities
                    .applyGammaCorrection(s / (float) maxEncodedValue, 2.2f));
        }
        return gammaCorrectionLUT;
    }

    protected float[] preComputeRemoveGammaCorrectionLUT() {
        float[] removeGammaCorrectionLUT = new float[256];

        for (int c = 0; c < removeGammaCorrectionLUT.length; ++c) {
            removeGammaCorrectionLUT[c] = ColorUtilities.removeGammaCorrection(
                    c / 255f, 2.2f);
        }

        return removeGammaCorrectionLUT;
    }

    /*
     * Daltonize Matrices
     */
    protected Matrix protanopeSim = new Matrix(0.0f, 2.02344f, -2.52581f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f, 1.0f);

    protected Matrix deuteranopeSim = new Matrix(1.0f, 0.0f, 0.0f, 0.494207f,
            0.0f, 1.24827f, 0.0f, 0.0f, 1.0f);

    protected Matrix tritanopeSim = new Matrix(1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, -0.395913f, 0.801109f, 0.0f);

    protected Matrix shiftTowardsVisible = new Matrix(0.0f, 0.0f, 0.0f, 0.7f,
            1.0f, 0.0f, 0.7f, 0.0f, 1.0f);

}
