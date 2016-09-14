/*
Basic ColorBlindness tutorial.

To add color blindness simulation to your sketch, you need to do two things.

1. Import the library.

    import colorblind.ColorBlindness;

You can do this with the Sketch => Import Library... => Color Blindness menu option.

2. In your setup function, create an instance of the ColorBlindness library and
specify color deficiency:

    ColorBlindness colorBlindness = new ColorBlindness(this);
    colorBlindness.simulateProtanopia();

That's it! The library pre-computes a lookup table on startup and uses this to do a
color transformation after your draw method runs.

There are more complex settings available. Look at the other tutorial, example, and
tutorial sketches to learn more.
 */

import colorblind.ColorBlindness;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(300, 300, P3D);
  // this is how the library 'attaches' to your sketch:
  ColorBlindness colorBlindness = new ColorBlindness(this);
  // change the below line of code to experiment
  colorBlindness.simulateProtanopia();
  //colorBlindness.simulateDeuteranopia();
  //colorBlindness.simulateTritanopia();
  //colorBlindness.simulateAchromatopsia();
  //colorBlindness.simulateBlueConeMonochromacy();
  //colorBlindness.daltonizeProtanopia();
  //colorBlindness.daltonizeDeuteranopia();
  //colorBlindness.daltonizeTritanopia();
  //colorBlindness.daltonizeAchromatopsia();
  //colorBlindness.daltonizeBlueConeMonochromacy();
}

void draw() {
  background(0xFFFFFF);

  rot += 1;

  rotX = rot * 0.5f;
  rotY = rot * 0.1f;
  rotZ = rot * 0.3f;

  strokeWeight(4);
  stroke(0);

  int boxSize = 90;
  int zDepth = -100;

  pushMatrix();
  fill(255, 0, 0);
  translate(width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 255, 0);
  translate(4 * width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 0, 255);
  translate(width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(128, 128, 128);
  translate(4 * width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();
}