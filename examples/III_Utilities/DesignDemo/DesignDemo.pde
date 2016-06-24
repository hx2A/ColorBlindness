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

int colorOff;
int colorHover;
int colorHover2;

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

  deficiency = Deficiency.PROTANOPE;
  colorBlindness = new ColorBlindness(this);
  colorBlindness.renderRegular();
  // colorBlindness.simulate(deficiency);

  colorHover = color(0, 80, 50);
  colorHover2 = color(0, 80, 80);
  colorOff = ColorUtilities.confusingColor(deficiency, colorHover, 0.2f);
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

