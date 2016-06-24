import colorblind.ColorBlindness;
import colorblind.ColorUtilities;
import colorblind.Deficiency;

ColorBlindness colorBlindness;
Deficiency deficiency;

void setup() {
  size(600, 400);
  colorMode(HSB, 360, 100, 100);

  deficiency = Deficiency.PROTANOPE;

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
      fill(ColorUtilities.confusingColor(deficiency, baseColor, i / (float) width));
      rect(i, y * 50 + 25, boxSize, boxSize);
    }
  }
}