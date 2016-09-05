/*
 Generate a grid of color swatches that would be confusing to a monochromatic
 color blind person.
 
 Each animation frame starts with a base color using hue and brightness values 
 (according to the HSB color model) that are different from the other frames.
 
 Colors in each frame should look (approximately) the same to a monochromatic
 color blind person.
 
 Press the space bar to simulate color blindness.
 */

import colorblind.ColorBlindness;
import colorblind.ColorUtilities;
import colorblind.Deficiency;

ColorBlindness colorBlindness;
Deficiency deficiency;
int brightness;
int hue;

void setup() {
  size(500, 500);
  colorMode(HSB, 360, 100, 100);

  deficiency = Deficiency.ACHROMATOPSIA;
  //deficiency = Deficiency.BLUE_CONE_MONOCHROMACY;
  brightness = 0;
  hue = 0;

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

  brightness = (int) ((Math.sin(frameCount / 53f) + 1) * 50);
  hue = (int) ((Math.sin(frameCount / 47f) + 1) * 180);
  int boxSize = 40;
  int baseColor = color(hue, 80, brightness);

  rectMode(CENTER);
  for (int x = 25; x < width; x += boxSize + 10) {
    for (int y = 25; y < height; y += boxSize + 10) {
      try {
        Thread.sleep(1);
      } 
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      fill(ColorUtilities.confusingMonochromaticColor(deficiency, 
        baseColor, x / (float) width, y / (float) height));
      rect(x, y, boxSize, boxSize);
    }
  }
}
