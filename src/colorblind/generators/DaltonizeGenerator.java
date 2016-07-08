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

public class DaltonizeGenerator extends ColorTranformGenerator {

	public DaltonizeGenerator(Deficiency colorBlindness) {
		super(colorBlindness);
	}

	public static DaltonizeGenerator createDaltonizer(Deficiency colorBlindness) {
		switch (colorBlindness) {
		case PROTANOPE:
			return new DaltonizeGenerator(Deficiency.PROTANOPE);
		case DEUTERANOPE:
			return new DaltonizeGenerator(Deficiency.DEUTERANOPE);
		case TRITANOPE:
			return new DaltonizeGenerator(Deficiency.TRITANOPE);
		case ACHROMATOPE:
			return new DaltonizeGenerator(Deficiency.ACHROMATOPE);
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

			Vector linRGB = new Vector(linRed, linGreen, linBlue);
			float simGray = ColorUtilities.clip(linRed
					* ColorUtilities.achromatopeSim.v1 + linGreen
					* ColorUtilities.achromatopeSim.v2 + linBlue
					* ColorUtilities.achromatopeSim.v3);
			Vector simRGB = new Vector(simGray, simGray, simGray);
			Vector error = linRGB.sub(simRGB);
			Vector correction = ColorUtilities.shiftTowardsVisible
					.rightMult(error);
			Vector daltonized = correction.add(linRGB);

			// Apply gamma correction using fast lookup table
			int simRed = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v1) * (MAX_ENCODED_VALUE - 1))];
			int simGreen = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v2) * (MAX_ENCODED_VALUE - 1))];
			int simBlue = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v3) * (MAX_ENCODED_VALUE - 1))];

			// Anomylize colors
			int finalRed = (int) (startRed * (1.0 - amount) + simRed * amount);
			int finalGreen = (int) (startGreen * (1.0 - amount) + simGreen
					* amount);
			int finalBlue = (int) (startBlue * (1.0 - amount) + simBlue
					* amount);

			colorMap[color] = 0xFF000000 | (finalRed << 16) | (finalGreen << 8)
					| finalBlue;
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

			Vector linRGB = new Vector(linRed, linGreen, linBlue);
			Vector lms = ColorUtilities.convertLinearRGB2LMS(linRGB);
			Vector simRGB = ColorUtilities.convertLMS2LinearRGB(sim
					.rightMult(lms));
			Vector error = linRGB.sub(simRGB);
			Vector correction = ColorUtilities.shiftTowardsVisible
					.rightMult(error);
			Vector daltonized = correction.add(linRGB);

			// Apply gamma correction using fast lookup table
			int simRed = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v1) * (MAX_ENCODED_VALUE - 1))];
			int simGreen = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v2) * (MAX_ENCODED_VALUE - 1))];
			int simBlue = applyGammaCorrectionLUT[(int) (ColorUtilities
					.clip(daltonized.v3) * (MAX_ENCODED_VALUE - 1))];

			// Anomylize colors
			int finalRed = (int) (startRed * (1.0 - amount) + simRed * amount);
			int finalGreen = (int) (startGreen * (1.0 - amount) + simGreen
					* amount);
			int finalBlue = (int) (startBlue * (1.0 - amount) + simBlue
					* amount);

			colorMap[color] = 0xFF000000 | (finalRed << 16) | (finalGreen << 8)
					| finalBlue;
		}

		return colorMap;
	}
}
