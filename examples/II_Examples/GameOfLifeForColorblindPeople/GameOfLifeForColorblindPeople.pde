import colorblind.ColorBlindness;
import colorblind.ColorUtilities;
import colorblind.Deficiency;

ColorBlindness colorBlindness;
int[] aliveColors;
int[] deadColors;
int[] colors;

GameOfLife life;
int cellSize;

void setup() {
  size(1000, 500);
  frameRate(30);
  colorMode(HSB, 360, 100, 100);

  Deficiency colorDeficiency = Deficiency.PROTANOPE;
  int colorCount = 20;
  int aliveBaseColor = color(150, 80, 50);
  int deadBaseColor = color(200, 80, 50);
  aliveColors = new int[colorCount];
  deadColors = new int[colorCount];
  for (int i = 0; i < colorCount; ++i) {
    aliveColors[i] = ColorUtilities.confusingColor(colorDeficiency,
            aliveBaseColor, i / (float) (colorCount - 1));
    deadColors[i] = ColorUtilities.confusingColor(colorDeficiency,
            deadBaseColor, i / (float) (colorCount - 1));
  }

  colorBlindness = new ColorBlindness(this);
  colorBlindness.simulate(colorDeficiency);
  colorBlindness.deactivate();

  cellSize = 10;
  life = new GameOfLife(height / cellSize, width / cellSize);
  rectMode(CORNER);
  stroke(0, 0, 0);

  colors = new int[life.getRows() * life.getCols()];
  updateColors();
}

void draw() {
  colorBlindness.setActive(keyPressed && key == ' ');

  if (frameCount % 30 == 0) {
    life.update();
  }
  if (frameCount % 15 == 0) {
    updateColors();
  }

  for (int i = 0; i < life.getRows(); ++i) {
    for (int j = 0; j < life.getCols(); ++j) {
      fill(colors[i * life.getCols() + j]);
      rect(j * cellSize, i * cellSize, cellSize, cellSize);
    }
  }
}

void updateColors() {
  for (int i = 0; i < life.getRows(); ++i) {
    for (int j = 0; j < life.getCols(); ++j) {
      if (life.getState(i, j)) {
        colors[i * life.getCols() + j] = aliveColors[(int) random(aliveColors.length)];
      } else {
        colors[i * life.getCols() + j] = deadColors[(int) random(deadColors.length)];
      }
    }
  }
}