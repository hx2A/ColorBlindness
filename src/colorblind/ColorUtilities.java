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
	public static Vector convertLinearRGB2LMS(Vector rgb) {
		return rgb2lms.rightMult(rgb);
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

		Vector c = new Vector(pow_r, pow_g, pow_b);

		return c;
	}

	public static Vector convertPColor2LMS(int color) {
		return convertLinearRGB2LMS(convertPColor2LinearRGB(color));
	}

	public static int convertLMS2PColor(Vector lmsColor) {
		Vector rgbColor = convertLMS2LinearRGB(lmsColor);

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
			float minLr1 = (0 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
					/ lms2rgb.r1c1;
			float minLr2 = (0 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
					/ lms2rgb.r2c1;
			float minLr3 = (0 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
					/ lms2rgb.r3c1;

			float maxLr1 = (1 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
					/ lms2rgb.r1c1;
			float maxLr2 = (1 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
					/ lms2rgb.r2c1;
			float maxLr3 = (1 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
					/ lms2rgb.r3c1;

			float minL = Math.max(Math.max(minLr1, minLr2), minLr3);
			float maxL = Math.min(Math.min(maxLr1, maxLr2), maxLr3);

			lms.v1 = minL + (clip(x) * (maxL - minL));

			return convertLMS2PColor(lms);

		case DEUTERANOPE:
			float minMr1 = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
					/ lms2rgb.r1c2;
			float minMr2 = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
					/ lms2rgb.r2c2;
			float minMr3 = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
					/ lms2rgb.r3c2;

			float maxMr1 = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
					/ lms2rgb.r1c2;
			float maxMr2 = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
					/ lms2rgb.r2c2;
			float maxMr3 = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
					/ lms2rgb.r3c2;

			float minM = Math.max(Math.max(minMr1, minMr2), minMr3);
			float maxM = Math.min(Math.min(maxMr1, maxMr2), maxMr3);

			lms.v2 = minM + (clip(x) * (maxM - minM));

			return convertLMS2PColor(lms);

		case TRITANOPE:
			float minSr1 = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
					/ lms2rgb.r1c3;
			float minSr2 = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
					/ lms2rgb.r2c3;
			float minSr3 = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
					/ lms2rgb.r3c3;

			float maxSr1 = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
					/ lms2rgb.r1c3;
			float maxSr2 = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
					/ lms2rgb.r2c3;
			float maxSr3 = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
					/ lms2rgb.r3c3;

			float minS = Math.max(Math.max(minSr1, minSr2), minSr3);
			float maxS = Math.min(Math.min(maxSr1, maxSr2), maxSr3);

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

	public static int confusingAchromatopeColor(int color, float x1, float x2) {
		Vector rgb = convertPColor2LinearRGB(color);

		float Z = rgb.v1 * 0.212656f + rgb.v2 * 0.715158f + rgb.v3 * 0.072186f;
		Vector rgb2 = new Vector();

		rgb2.v1 = x1;

		float minG2 = (Z - 0.212656f * rgb2.v1 - 0.072186f * 1) / 0.715158f;
		float maxG2 = (Z - 0.212656f * rgb2.v1) / 0.715158f;
		rgb2.v2 = minG2 + x2 * (maxG2 - minG2);
		rgb2.v3 = (Z - 0.212656f * rgb2.v1 - 0.072186f * rgb2.v2) / 0.072186f;

		return 0;
	}
}
