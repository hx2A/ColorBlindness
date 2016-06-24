import colorblind.ColorUtilities;
import colorblind.generators.util.Vector;
import controlP5.ControlP5;
import controlP5.Slider;

ControlP5 cp5;
PImage coneResponse;

int redVal;
int greenVal;
int blueVal;

int lmsOriginX;
int lmsOriginY;
int lmsBarWidth;
int lmsBarHeight;

void setup() {
  size(700, 430);
  background(255);

  textSize(20);

  redVal = 200;
  greenVal = 200;
  blueVal = 200;

  lmsOriginX = 520;
  lmsOriginY = 50;
  lmsBarWidth = 55;
  lmsBarHeight = 330;

  coneResponse = loadImage("cone_response.png");

  cp5 = new ControlP5(this);
  addSlider("redVal", "Red", 0, 0, 255, 0xFFFF0000);
  addSlider("greenVal", "Green", 60, 0, 255, 0xFF00FF00);
  addSlider("blueVal", "Blue", 120, 0, 255, 0xFF0000FF);
}

void draw() {
  background(255);
  color swatch = color(redVal, greenVal, blueVal);

  // Annotations
  fill(0);
  noStroke();
  textSize(30);
  text("LMS Color Model\n     Simulation", 250, 40);
  textSize(20);
  text("RGB Color Values", 10, 40);
  text("Cone Stimulation", lmsOriginX, lmsOriginY - 10);
  text("S", lmsOriginX + 20, lmsOriginY + lmsBarHeight + 30);
  text("M", lmsOriginX + 20 + lmsBarWidth, lmsOriginY + lmsBarHeight + 30);
  text("L", lmsOriginX + 20 + 2 * lmsBarWidth, lmsOriginY + lmsBarHeight
            + 30);

  // Color swatch
  rectMode(CORNER);
  fill(swatch);
  strokeWeight(5);
  stroke(0);
  rect(10, 235, 175, 175);
  image(coneResponse, 220, 200);

  // LMS diagram
  Vector lms = ColorUtilities.convertPColor2LMS(swatch);

  rectMode(CORNERS);
  noStroke();
  // S
  fill(0xFF0000FF);
  rect(lmsOriginX, lmsOriginY + lmsBarHeight * (1 - lms.v3), lmsOriginX
          + lmsBarWidth, lmsOriginY + lmsBarHeight);
  // M
  fill(0xFF00FF00);
  rect(lmsOriginX + lmsBarWidth,
          lmsOriginY + lmsBarHeight * (1 - lms.v2), lmsOriginX + 2
                  * lmsBarWidth, lmsOriginY + lmsBarHeight);
  // L
  fill(0xFFFF0000);
  rect(lmsOriginX + 2 * lmsBarWidth, lmsOriginY + lmsBarHeight
            * (1 - lms.v1), lmsOriginX + 3 * lmsBarWidth, lmsOriginY
            + lmsBarHeight);

  rectMode(CORNER);
  noFill();
  stroke(128);
  strokeWeight(1);
  rect(lmsOriginX, lmsOriginY, lmsBarWidth, lmsBarHeight);
  rect(lmsOriginX + lmsBarWidth, lmsOriginY, lmsBarWidth, lmsBarHeight);
  rect(lmsOriginX + 2 * lmsBarWidth, lmsOriginY, lmsBarWidth,
          lmsBarHeight);
}

Slider addSlider(String variable, String caption, float y, int min,
        int max, int sliderColor) {
  Slider slider = cp5.addSlider(variable);
  slider.setPosition(10, 50 + y);
  slider.setRange(min, max);
  slider.setSize(175, 50);
  slider.setCaptionLabel(caption);
  slider.setColorCaptionLabel(color(0));
  slider.setColorValueLabel(color(255));
  slider.setColorForeground(sliderColor & 0xFFDDDDDD);
  slider.setColorBackground(sliderColor & 0xFF333333);
  slider.setColorActive(sliderColor);

  return slider;
}