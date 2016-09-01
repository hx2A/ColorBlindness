/*
Second ColorBlindness tutorial.
 
In this example we are parameterizing the color deficiency to a variable. This is 
useful if you want your sketch to change color deficiency type with a dropdown.
 
    Deficiency colorDeficiency = Deficiency.PROTANOPIA;
 
This library also supports anomalous trichromacy, or partial color blindness. Call
the setAmount function with a float from 0 (no color deficiency) to 1 (full color
deficiency).
 
    colorBlindness.simulate(colorDeficiency).setAmount(0.8);
 
Note that this library pre-computes a lookup table for these specific color blindness
settings. If you want the amount to change as the sketch runs, use the 
.setDynamicAmount() method. Look in the Utilities sketches for specific examples.
 
The pre-computed lookup table is important for performance reasons. When you call
.setDynamicAmount(), it computes the lookup table in a way that allows it to be fast
without a delay every time the amount value changes.
 
You may want to use the ColorBlindness library but draw some things to the sketch
*after* the color simulation is complete. To do so, put your code in the optional
postDraw() method:
 
    void postDraw() {
      // stuff
    }
 
In this example we are adding a label to the lower right corner of the screen. If
you were using controlP5, you might want to call cp5.draw() here if you didn't want
the ColorBlindness library to alter the colors of your controlP5 elements.
 */

import colorblind.ColorBlindness;
import colorblind.Deficiency;

PGraphics label;
ColorBlindness colorBlindness;
Deficiency colorDeficiency;
int squareSize;

void setup() {
  size(500, 500, P2D);
  background(255);

  squareSize = (int) random(20, 40);

  colorBlindness = new ColorBlindness(this);
  colorDeficiency = Deficiency.PROTANOPIA;
  //colorDeficiency = Deficiency.DEUTERANOPIA;
  //colorDeficiency = Deficiency.TRITANOPIA;
  //colorDeficiency = Deficiency.ACHROMATOPSIA;
  //colorDeficiency = Deficiency.BLUE_CONE_MONOCHROMACY;
  colorBlindness.simulate(colorDeficiency).setAmount(0.8);

  label = createGraphics(200, 30);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0xFFFF0000);
  label.textSize(16);
  label.text("ColorBlindness Example", 0, 0);
  label.endDraw();
}

void draw() {
  fill(color(random(255), random(255), random(255), 128));
  rect(random(width), random(height), squareSize, squareSize);
}

/*
The ColorBlindness library does its magic *after* the draw method and *before*
an optional postDraw() method. Both are called once per frame.

Anthing that would work in the draw() method is acceptable for postDraw(). If you
don't have anything to put here, don't include it in your sketch.
 */
void postDraw() {
  copy(label, 0, 0, label.width, label.height, width - label.width, 
    height - label.height, label.width, label.height);
}