/*
Simple example showing the mouse-overs for 3 different button designs.

Uncomment line 74 to simulate color blindness and explore how the button
designs would look to a color blind person.

Button 1 - mouse-over effect is invisible to a color blind person
Button 2 - color blind person can detect a change in brightness
Button 3 - Text and button outline make it very clear that the button is activated

Basic design principle is to not depend on color a color change by itself to signal
anything.
*/
import colorblind.ColorBlindness;
import colorblind.ColorUtilities;
import colorblind.Deficiency;

ColorBlindness colorBlindness;
Deficiency deficiency;

boolean button1;
int button1X1;
int button1Y1;
int button1X2;
int button1Y2;

boolean button2;
int button2X1;
int button2Y1;
int button2X2;
int button2Y2;

boolean button3;
int button3X1;
int button3Y1;
int button3X2;
int button3Y2;

color colorOff;
color colorHover;
color colorHover2;

void setup() {
  size(300, 500);
  rectMode(CORNERS);
  textSize(30);
  colorMode(HSB, 360, 100, 100);

  int buttonWidth = 200;
  int buttonHeight = 100;
  int buttonSpacing = 50;

  button1X1 = 50;
  button1Y1 = 50;
  button1X2 = button1X1 + buttonWidth;
  button1Y2 = button1Y1 + buttonHeight;

  button2X1 = button1X1;
  button2Y1 = button1Y1 + buttonHeight + buttonSpacing;
  button2X2 = button2X1 + buttonWidth;
  button2Y2 = button2Y1 + buttonHeight;

  button3X1 = button2X1;
  button3Y1 = button2Y1 + buttonHeight + buttonSpacing;
  button3X2 = button3X1 + buttonWidth;
  button3Y2 = button3Y1 + buttonHeight;

  // If you change the deficency you will also want to
  // change colorHover and colorHover2.
  deficiency = Deficiency.PROTANOPIA;
  colorBlindness = new ColorBlindness(this);
  // ******************************************************
  // uncomment the below line to simulate color blindness
  // colorBlindness.simulate(deficiency);
  // ******************************************************

  colorHover = color(0, 80, 50);
  colorHover2 = color(0, 80, 80);

  // create a new color that a color blind person would think looks
  // the same as colorHover.
  // the third parameter can be any float in the range [0, 1]. Returned
  // colors lie on the colorHover's confusion line.
  colorOff = ColorUtilities.confusingColor(deficiency, colorHover, 0.2);
}

void draw() {
  background(color(0, 0, 80));

  button1 = within(button1X1, button1Y1, button1X2, button1Y2);
  button2 = within(button2X1, button2Y1, button2X2, button2Y2);
  button3 = within(button3X1, button3Y1, button3X2, button3Y2);

  // button 1
  if (button1) {
    fill(colorHover);
  } else {
    fill(colorOff);
  }
  noStroke();
  rect(button1X1, button1Y1, button1X2, button1Y2);
  fill(0);
  text("Button 1", button1X1 + 40, button1Y1 + 60);

  // button 2
  if (button2) {
    fill(colorHover2);
  } else {
    fill(colorOff);
  }
  noStroke();
  rect(button2X1, button2Y1, button2X2, button2Y2);
  fill(0);
  text("Button 2", button2X1 + 40, button2Y1 + 60);

  // button 3
  if (button3) {
    fill(colorHover2);
    stroke(color(0, 0, 100));
  } else {
    fill(colorOff);
    noStroke();
  }
  rect(button3X1, button3Y1, button3X2, button3Y2);
  if (button3) {
    strokeWeight(5);
    fill(color(0, 0, 100));
  } else {
    fill(0);
  }
  text("Button 3", button3X1 + 40, button3Y1 + 60);
}

boolean within(int minX, int minY, int maxX, int maxY) {
  return (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY);
}