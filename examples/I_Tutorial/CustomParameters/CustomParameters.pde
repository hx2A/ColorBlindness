/*
 The ColorBlindness library is configured to simulate color blindness using
 empirically derived parameters from human vision experiments. These parameters
 can be changed if you wish.
 
 I did a fair amount of research into color models and how they are used to
 simulate color blindness. I discovered that many of the color blindness
 simulation applications out there are copies of the same algorithm that use
 identical parameters derived from an old color appearance model for converting
 linear RGB colors to LMS. Those applications also contain an incorrect
 assumption for Tritanopia simulations.
 
 How to do the RGB => LMS conversion can only be estimated empirically and
 there is no one 'correct' way to do it. There have been numerous research
 papers written that contain more reliable numbers to use, but those research
 papers require some math calculations to incorporate them in a simulation like
 this. I have done those calculations and have documented them online.
 
 I believe my default ColorBlindness parameters are better than the parameters
 you will find elsewhere, but you are free to disagree and set them to whatever
 you wish. In this example, I am setting them to the same values that are used
 elsewhere on the Internet. Using these settings, the ColorBlindness library
 should give the same simulation results as most other simulation libraries
 out there.
 
 [That isn't quite true: most of the color blindness simulation tools out there
 neglect to remove gamma correction before converting to LMS. This is
 incorrect. The ColorBlindness library does do the necessary gamma
 adjustment so the results won't be exactly the same as most of the tools out
 there.]
 
 [Be aware of the error for Tritanopia simulations.]
 
 The derivation of my parameters are documented online. If you can make a
 cogent argument for why your parameters are better than mine, please let me
 know: jim at ixora.io. I am not color blind so it is hard for me to evaluate
 better or worse simulations.
 */

import colorblind.ColorBlindness;
import colorblind.Deficiency;
import colorblind.ColorUtilities;
import colorblind.generators.util.Matrix;

ColorBlindness colorBlindness;
int squareSize;

void setup() {
  size(500, 500, P2D);

  background(255);

  squareSize = (int) random(20, 40);

  colorBlindness = new ColorBlindness(this);

  /*
   The lmx2rgb matrix must be the inverse of the rgb2lms matrix.
   */
  ColorUtilities.rgb2lms = new Matrix(17.8824, 43.5161, 4.11935, 
    3.45565, 27.1554, 3.86714, 0.0299566, 0.184309, 1.46709);
  ColorUtilities.lms2rgb = new Matrix(0.0809444479, -0.130504409, 
    0.116721066, -0.0102485335, 0.0540193266, -0.113614708, 
    -0.000365296938, -0.00412161469, 0.693511405);

  /*
   If you change the above two matrices you must also change the simulation
   matrix. This matrix is a function of the above matrices. Check the
   documentation for more details.
   */
  ColorUtilities.protanopiaSim = new Matrix(0.0, 2.02344, -2.52581, 
    0.0, 1.0, 0.0, 0.0, 0.0, 1.0);

  colorBlindness.simulateProtanopia();
}

void draw() {
  fill(color(random(255), random(255), random(255), 128));
  rect(random(width), random(height), squareSize, squareSize);
}