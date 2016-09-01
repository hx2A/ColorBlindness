/*
Generate a grid of color swatches that would be confusing to a color blind person.

Each row starts with a base color using a saturation value (according to the HSB
color model) that is different from the other rows.

Moving from left to right along any row moves along the confusion line for the base
color and the selected color deficiency. Colors on the same row should look
(approximately) the same to a color blind person.

Press the space bar to simulate color blindness.
*/

import colorblind.ColorBlindness;
import colorblind.ColorUtilities;
import colorblind.Deficiency;

ColorBlindness colorBlindness;
Deficiency deficiency;

void setup() {
  size(600, 400);
  colorMode(HSB, 360, 100, 100);

  deficiency = Deficiency.PROTANOPIA;
  //deficiency = Deficiency.DEUTERANOPIA;
  //deficiency = Deficiency.TRITANOPIA;

  // prep the simulation but deactivate it until the user hits the space bar.
  colorBlindness = new ColorBlindness(this);
  colorBlindness.simulate(deficiency);
  colorBlindness.deactivate();
}

void draw() {
  colorBlindness.setActive(keyPressed && key == ' ');

  background(0, 0, 100);
  noStroke();
  fill(0);

  int boxSize = 40;

  rectMode(CENTER);
  for (int y = 0; y < 8; ++y) {
    for (int i = 25; i < width; i += boxSize + 10) {
      color baseColor = color(200, y * 10 + 20, 80);
      fill(ColorUtilities.confusingDichromaticColor(deficiency, baseColor, 
        i / (float) width));
      rect(i, y * 50 + 25, boxSize, boxSize);
    }
  }
}