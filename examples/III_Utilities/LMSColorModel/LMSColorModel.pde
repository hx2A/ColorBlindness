import colorblind.ColorUtilities;
import colorblind.generators.util.Matrix;
import colorblind.generators.util.Vector;

import controlP5.ControlP5;
import controlP5.Slider;
import controlP5.Button;
import controlP5.ControlEvent;

ControlP5 cp5;
PImage coneResponse;

int redSliderVal;
int greenSliderVal;
int blueSliderVal;
int swatchColor;
Vector lms;
boolean allowCP5updating;

final int lmsOriginX = 520;
final int lmsOriginY = 50;
final int lmsBarWidth = 55;
final int lmsBarHeight = 275;

void setup() {
  size(700, 430);
  background(255);
  textSize(20);

  // color state information
  redSliderVal = 200;
  greenSliderVal = 200;
  blueSliderVal = 200;
  swatchColor = color(redSliderVal, greenSliderVal, blueSliderVal);
  lms = ColorUtilities.convertPColor2LMS(swatchColor);
  allowCP5updating = true;

  // cone response chart
  coneResponse = loadImage("cone_response.png");

  // controlP5 stuff
  cp5 = new ControlP5(this);
  addSlider("redSliderVal", "Red", 0, 0, 255, 0xFFFF0000);
  addSlider("greenSliderVal", "Green", 60, 0, 255, 0xFF00FF00);
  addSlider("blueSliderVal", "Blue", 120, 0, 255, 0xFF0000FF);

  addButton("SU", "UP", lmsOriginX, lmsOriginY + lmsBarHeight + 45, 0xFF0000FF);
  addButton("SD", "DOWN", lmsOriginX, lmsOriginY + lmsBarHeight + 70, 0xFF0000FF);
  addButton("MU", "UP", lmsOriginX + lmsBarWidth, lmsOriginY + lmsBarHeight + 45, 0xFF00FF00);
  addButton("MD", "DOWN", lmsOriginX + lmsBarWidth, lmsOriginY + lmsBarHeight + 70, 0xFF00FF00);
  addButton("LU", "UP", lmsOriginX + 2 * lmsBarWidth, lmsOriginY + lmsBarHeight + 45, 0xFFFF0000);
  addButton("LD", "DOWN", lmsOriginX + 2 * lmsBarWidth, lmsOriginY + lmsBarHeight + 70, 0xFFFF0000);
}

void draw() {
  background(255);

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
  text("L", lmsOriginX + 20 + 2 * lmsBarWidth, lmsOriginY + lmsBarHeight + 30);

  // Color swatch
  rectMode(CORNER);
  fill(swatchColor);
  strokeWeight(1);
  stroke(0);
  rect(10, 235, 175, 175);
  image(coneResponse, 220, 200);

  // LMS bar diagram
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
  rect(lmsOriginX, lmsOriginY, lmsBarWidth, lmsBarHeight);
  rect(lmsOriginX + lmsBarWidth, lmsOriginY, lmsBarWidth, lmsBarHeight);
  rect(lmsOriginX + 2 * lmsBarWidth, lmsOriginY, lmsBarWidth,
        lmsBarHeight);
}

void controlEvent(ControlEvent event) {
  String name = event.getController().getName();

  if (name.length() == 2) {
    updateLMSvalues(name, 0.005f);
  } else {
    updateRGBvalues(name, event.getValue());
  }
}

void keyPressed(KeyEvent event) {
  char c = event.getKey();

  if ("LMS".indexOf(c) >= 0) {
    updateLMSvalues(Character.toString(c) + "U", 0.001f);
  } else if ("lms".indexOf(c) >= 0) {
    updateLMSvalues(Character.toString(c).toUpperCase() + "D", 0.001f);
  }
}

void updateRGBvalues(String name, float value) {
  if (allowCP5updating) {
    Vector rgb = ColorUtilities.convertLMS2LinearRGB(lms);

    if (name == "redSliderVal") {
      rgb.v1 = ColorUtilities.clip(ColorUtilities
        .removeGammaCorrectionStandardRGB(value / 255f), 0.0001f, 0.9999f);
    } else if (name == "greenSliderVal") {
      rgb.v2 = ColorUtilities.clip(ColorUtilities
        .removeGammaCorrectionStandardRGB(value / 255f), 0.0001f, 0.9999f);
    } else if (name == "blueSliderVal") {
      rgb.v3 = ColorUtilities.clip(ColorUtilities
        .removeGammaCorrectionStandardRGB(value / 255f), 0.0001f, 0.9999f);
    }
    lms = ColorUtilities.convertLinearRGB2LMS(rgb);
    swatchColor = ColorUtilities.convertLinearRGB2PColor(rgb);
  }
}

void updateLMSvalues(String name, float magnitude) {
  lms = adjustLMS(name.charAt(0), lms,
        (name.charAt(1) == 'U') ? magnitude : -magnitude);

  swatchColor = ColorUtilities.convertLMS2PColor(lms);
  redSliderVal = (swatchColor & 0x00FF0000) >> 16;
  greenSliderVal = (swatchColor & 0x0000FF00) >> 8;
  blueSliderVal = swatchColor & 0x000000FF;

  allowCP5updating = false;
  cp5.getController("redSliderVal").setValue(redSliderVal);
  cp5.getController("greenSliderVal").setValue(greenSliderVal);
  cp5.getController("blueSliderVal").setValue(blueSliderVal);
  allowCP5updating = true;
}

Vector adjustLMS(char channel, Vector lms, float amount) {
  Matrix lms2rgb = ColorUtilities.lms2rgb;

  float[] boundaries = new float[6];

  switch (channel) {
  case 'L':
    boundaries[0] = (0 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
        / lms2rgb.r1c1;
    boundaries[1] = (0 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
        / lms2rgb.r2c1;
    boundaries[2] = (0 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
        / lms2rgb.r3c1;

    boundaries[3] = (1 - lms2rgb.r1c2 * lms.v2 - lms2rgb.r1c3 * lms.v3)
        / lms2rgb.r1c1;
    boundaries[4] = (1 - lms2rgb.r2c2 * lms.v2 - lms2rgb.r2c3 * lms.v3)
        / lms2rgb.r2c1;
    boundaries[5] = (1 - lms2rgb.r3c2 * lms.v2 - lms2rgb.r3c3 * lms.v3)
        / lms2rgb.r3c1;

    float minL = Float.MIN_VALUE;
    float maxL = Float.MAX_VALUE;

    for (float boundary : boundaries) {
      if (boundary < lms.v1)
        minL = Math.max(minL, boundary);
      else
        maxL = Math.min(maxL, boundary);
    }
    lms.v1 = ColorUtilities.clip(lms.v1 + amount, minL + 0.0001f,
        maxL - 0.0001f);

    return lms;

  case 'M':
    boundaries[0] = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
        / lms2rgb.r1c2;
    boundaries[1] = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
        / lms2rgb.r2c2;
    boundaries[2] = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
        / lms2rgb.r3c2;

    boundaries[3] = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c3 * lms.v3)
        / lms2rgb.r1c2;
    boundaries[4] = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c3 * lms.v3)
        / lms2rgb.r2c2;
    boundaries[5] = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c3 * lms.v3)
        / lms2rgb.r3c2;

    float minM = Float.MIN_VALUE;
    float maxM = Float.MAX_VALUE;

    for (float boundary : boundaries) {
      if (boundary < lms.v2)
        minM = Math.max(minM, boundary);
      else
        maxM = Math.min(maxM, boundary);
    }

    lms.v2 = ColorUtilities.clip(lms.v2 + amount, minM + 0.0001f,
        maxM - 0.0001f);

    return lms;

  case 'S':
    boundaries[0] = (0 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
        / lms2rgb.r1c3;
    boundaries[1] = (0 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
        / lms2rgb.r2c3;
    boundaries[2] = (0 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
        / lms2rgb.r3c3;

    boundaries[3] = (1 - lms2rgb.r1c1 * lms.v1 - lms2rgb.r1c2 * lms.v2)
        / lms2rgb.r1c3;
    boundaries[4] = (1 - lms2rgb.r2c1 * lms.v1 - lms2rgb.r2c2 * lms.v2)
        / lms2rgb.r2c3;
    boundaries[5] = (1 - lms2rgb.r3c1 * lms.v1 - lms2rgb.r3c2 * lms.v2)
        / lms2rgb.r3c3;

    float minS = Float.MIN_VALUE;
    float maxS = Float.MAX_VALUE;

    for (float boundary : boundaries) {
      if (boundary < lms.v3)
        minS = Math.max(minS, boundary);
      else
        maxS = Math.min(maxS, boundary);
    }

    lms.v3 = ColorUtilities.clip(lms.v3 + amount, minS + 0.0001f, maxS - 0.0001f);

    return lms;

  default:
    throw new RuntimeException("Unknown Color Deficiency");
  }
}

private Slider addSlider(String variable, String caption, float y, int min, int max, int sliderColor) {
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

Button addButton(String name, String label, int x, int y, int buttonColor) {
  Button button = cp5.addButton(name);
  button.setCaptionLabel(label);
  button.setValue(0);
  button.setPosition(x, y);
  button.setSize(lmsBarWidth, 20);
  button.setColorActive(buttonColor & 0xFFDDDDDD);
  button.setColorForeground(buttonColor & 0xFF333333);
  button.setColorBackground(buttonColor);

  return button;
}