/*
 Game of Life, but only properly visible to color blind individuals.
 
 This implementation calculates two sets of colors to use for alive and dead
 cells. Colors within each set look identical to a color blind person. It
 randomly picks a colors from the alive or dead sets for each stage of the
 game.
 
 I added a color change inbetween game-of-life updates to (maybe) be more
 confusing to trichromats.
 
 Hit the space bar to simulate color blindness. This will enable trichromats
 to see what a color blind person sees.
 */

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

  /*
  If you change the colorDeficiency you will also need to adjust
   the alive and dead base colors. Use the ConfusingColorSquares to
   help you pick colors that are roughly similar to a trichromat.
   */
  Deficiency colorDeficiency = Deficiency.PROTANOPIA;

  int colorCount = 20;
  int aliveBaseColor = color(150, 80, 50);
  int deadBaseColor = color(200, 80, 50);
  aliveColors = new int[colorCount];
  deadColors = new int[colorCount];
  for (int i = 0; i < colorCount; ++i) {
    aliveColors[i] = ColorUtilities.confusingDichromaticColor(colorDeficiency,
      aliveBaseColor, i / (float) (colorCount - 1));
    deadColors[i] = ColorUtilities.confusingDichromaticColor(colorDeficiency,
      deadBaseColor, i / (float) (colorCount - 1));
  }

  // prep the simulation but deactivate it until the user hits the space bar.
  colorBlindness = new ColorBlindness(this);
  colorBlindness.simulate(colorDeficiency);
  colorBlindness.deactivate();
  /* Useful code for evaluating performance. Frame Rate is the usual Processing
  frameRate variable. If the Generator render times are more than a few milliseconds
  it might impact the frameRate.
  */
  colorBlindness.reportStats();

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