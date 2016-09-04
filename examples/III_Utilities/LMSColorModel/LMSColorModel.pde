/*
Simple tool for exploring the the LMS color model. Adjust the the RGB
 color bars to see how the color stimulates the the color receptors, or
 cones, in the human eye.
 
 Dichromat color blind people lack one of the L, M, or S color cones.
 
 Protanopia - lack L cones
 Deuteranopia - lack M cones
 Tritanopia - lack S cones
 
 With only 2 kinds of cones, dichromats are unable to disambiguate colors
 that would maintain the same level of stimulation to the cones they do 
 have but different levels of stimuation to the cone that they are missing.
 
 Adjust the LMS stimulations using the buttons on the lower right side
 of the sketch. Note that some combinations of L, M, and S stimulations
 are not feasible because of the overlap of the cones' spectral absorption
 functions. This sketch will keep you from making that mistake.
 
 If you set the color to pure white (255, 255, 255), you will not be able
 to use the LMS up and down buttons to adjust the color. This is due to
 tedious numerical issues that are not worth explaining. Change the color
 to something else, such as (253, 253, 253), and proceed.
 */

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
  int index = 0;
  switch (channel) {
  case 'L':
    index = 1;
    break;
  case 'M':
    index = 2;
    break;
  case 'S':
    index = 3;
    break;
  default:
    throw new RuntimeException("Unknown Channel");
  }  

  float lower = ColorUtilities.lmsFeasibleBisectionSearch(lms, index, -1);
  float upper = ColorUtilities.lmsFeasibleBisectionSearch(lms, index, 1);

  lms.set(index, constrain(lms.get(index) + amount, lower, upper));

  return lms;
}

Slider addSlider(String variable, String caption, float y, int min, int max, int sliderColor) {
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
