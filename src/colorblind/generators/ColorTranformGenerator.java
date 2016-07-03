package colorblind.generators;

import processing.core.PImage;
import colorblind.ColorUtilities;
import colorblind.Deficiency;
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

    protected Deficiency deficiency;

    protected int[] colorMap;
    protected float amount;
    protected float amountComplement;
    protected boolean dynamicAmount;

    protected final int MAX_ENCODED_VALUE = (int) Math.pow(2, 10);
    protected float[] removeGammaCorrectionLUT;
    protected int[] applyGammaCorrectionLUT;

    /**
     * Constructor.
     * 
     * Compute gamma lookup tables. This is fast and the memory is small.
     * 
     * @param colorBlindness
     */
    public ColorTranformGenerator(Deficiency colorBlindness) {
        this.deficiency = colorBlindness;
        this.amount = 1;
        this.amountComplement = 0;
        this.dynamicAmount = false;

        removeGammaCorrectionLUT = preComputeRemoveGammaCorrectionStandardrgbLUT();
        applyGammaCorrectionLUT = preComputeApplyGammaCorrectionStandardrgbLUT(MAX_ENCODED_VALUE);
    }

    protected abstract int[] precalcAchromatopeColorMap(float amount);

    protected abstract int[] precalcDichromaticColorMap(Matrix sim, float amount);

    private int[] computeColorMapLookup(float amount) {
        switch (deficiency) {
        case ACHROMATOPE:
            return precalcAchromatopeColorMap(amount);
        case PROTANOPE:
            return precalcDichromaticColorMap(ColorUtilities.protanopeSim,
                    amount);
        case DEUTERANOPE:
            return precalcDichromaticColorMap(ColorUtilities.deuteranopeSim,
                    amount);
        case TRITANOPE:
            return precalcDichromaticColorMap(ColorUtilities.tritanopeSim,
                    amount);
        default:
            throw new RuntimeException("ERROR: Unknown color deficiency");
        }
    }

    /**
     * Set % of colorblindness. This defaults to 100%.
     * 
     * Better to call setDynamicAmount() before calling this.
     * 
     * @param amount
     *            : in range [0, 1]
     * @return
     */
    public ColorTranformGenerator setAmount(float amount) {
        if (dynamicAmount == false && colorMap != null) {
            System.err
                    .println("Please don't change amount without also calling setDynamicAmount()."
                            + "Call it in your setup() function or before calling setAmount().");
            colorMap = null;
        }

        this.amount = ColorUtilities.clip(amount);
        this.amountComplement = 1 - this.amount;

        return this;
    }

    /**
     * Indicate to the generator that the amount may change. This enables it to
     * improve performance considerably.
     * 
     * @return
     */
    public ColorTranformGenerator setDynamicAmount() {
        if (dynamicAmount == false && colorMap != null && amount < 1)
            colorMap = null;

        dynamicAmount = true;

        return this;
    }

    public int transformColor(int color) {
        if (dynamicAmount) {
            if (amount == 0) {
                return color;
            } else if (amount == 1) {
                return colorMap[color & 0x00FFFFFF];
            } else { // 0 < amount < 1
                int map = colorMap[color & 0x00FFFFFF];

                int r = (color & 0x00FF0000) >> 16;
                int g = (color & 0x0000FF00) >> 8;
                int b = (color & 0x000000FF);

                int r2 = (map & 0x00FF0000) >> 16;
                int g2 = (map & 0x0000FF00) >> 8;
                int b2 = (map & 0x000000FF);

                int fr = (int) (r * amountComplement + r2 * amount);
                int fg = (int) (g * amountComplement + g2 * amount);
                int fb = (int) (b * amountComplement + b2 * amount);

                return (fr << 16) | (fg << 8) | fb;
            }
        } else {
            return colorMap[color & 0x00FFFFFF];
        }
    }

    public void transformPixels(int[] pixels) {
        if (colorMap == null) {
            System.out.println("Pre-computing lookup table...");
            // Important: if dynamicAmount is true, then compute colorMap with
            // amount 1 and do an interpolation later. If dynamicAmount is
            // false, calculate colorMap with the fixed amount.
            if (dynamicAmount) {
                colorMap = computeColorMapLookup(1);
            } else {
                colorMap = computeColorMapLookup(amount);
            }
        }

        if (dynamicAmount) {
            if (amount == 0) {
                // do nothing. return pixels unchanged.
            } else if (amount == 1) {
                // since we know colorMap was calculated with amount == 1, we
                // can just do the lookup.
                for (int i = 0; i < pixels.length; ++i) {
                    pixels[i] = colorMap[pixels[i] & 0x00FFFFFF];
                }
            } else { // 0 < amount < 1
                for (int i = 0; i < pixels.length; ++i) {
                    // inline transformColor for better performance
                    // pixels[i] = transformColor(pixels[i]);

                    int color = pixels[i];
                    int map = colorMap[color & 0x00FFFFFF];

                    int r = (color & 0x00FF0000) >> 16;
                    int g = (color & 0x0000FF00) >> 8;
                    int b = (color & 0x000000FF);

                    int r2 = (map & 0x00FF0000) >> 16;
                    int g2 = (map & 0x0000FF00) >> 8;
                    int b2 = (map & 0x000000FF);

                    int fr = (int) (r * amountComplement + r2 * amount);
                    int fg = (int) (g * amountComplement + g2 * amount);
                    int fb = (int) (b * amountComplement + b2 * amount);

                    pixels[i] = (fr << 16) | (fg << 8) | fb;
                }
            }
        } else {
            for (int i = 0; i < pixels.length; ++i) {
                pixels[i] = colorMap[pixels[i] & 0x00FFFFFF];
            }
        }
    }

    public PImage transformPImage(PImage img) {
        // use get so this works in P2 and P3
        PImage copy = img.get(0, 0, img.width, img.height);
        copy.loadPixels();
        transformPixels(copy.pixels);
        copy.updatePixels();

        return copy;
    }

    /*
     * Functions for pre-calculating look-up-tables
     */
    private int[] preComputeApplyGammaCorrectionStandardrgbLUT(
            int maxEncodedValue) {
        int[] gammaCorrectionLUT = new int[maxEncodedValue];
        for (int s = 0; s < maxEncodedValue; ++s) {
            gammaCorrectionLUT[s] = (int) (255 * ColorUtilities
                    .applyGammaCorrectionStandardRGB(s
                            / (float) maxEncodedValue));
        }
        return gammaCorrectionLUT;
    }

    private float[] preComputeRemoveGammaCorrectionStandardrgbLUT() {
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
}
