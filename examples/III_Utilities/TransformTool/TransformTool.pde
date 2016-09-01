/*
Simple utility to allow users to upload an arbitrary image, perform color
blindness simulation or daltonization, and then save to a file.

Error messages appear in the sketch and in the console.
*/

import java.util.Map;
import java.util.Map.Entry;

import colorblind.ColorBlindness;
import colorblind.Deficiency;
import colorblind.generators.ColorTranformGenerator;

import controlP5.Button;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Slider;

private enum Action {
  SIMULATE, DALTONIZE, DALTONIZE_AND_SIMULATE
}

ControlP5 cp5;
ColorBlindness colorBlindness;
ColorTranformGenerator currentSimulator;
ColorTranformGenerator currentDaltonizer;

// ControlP5 variables
boolean ready;
Action action;
Deficiency deficiency;
int amount;
File outputFile;
boolean wroteOutput;
String status;

String actionNames = "Simulate, Daltonize, Daltonize and Simulate";
String deficiencyNames = "Protanopia, Deuteranopia, Tritanopia, Achromatopsia, Blue Cone Monochromacy";

Map<Integer, String> actionNameMap;
Map<Integer, String> deficiencyNameMap;

final int MAX_IMAGE_HEIGHT = 250;
final int MAX_IMAGE_WIDTH = 250;
PImage inputImage;
PImage scaledInputImage;
PImage outputImage;
PImage scaledOutputImage;

void setup() {
  size(720, 270);
  textSize(10);

  amount = 100;

  cp5 = new ControlP5(this);
  colorBlindness = new ColorBlindness(this);
  colorBlindness.deactivate();

  actionNameMap = createDropdownMap(actionNames);
  deficiencyNameMap = createDropdownMap(deficiencyNames);

  createControls();
  ready = true;
}

void draw() {
  background(196);

  if (scaledInputImage != null) {
    image(scaledInputImage, 180, 10);
  }
  if (scaledOutputImage != null) {
    image(scaledOutputImage, 450, 10);
  }

  if (status != null && !status.equals("")) {
    fill(0xFF333333);
    text(status, 10, 210);
  }
}

void controlEvent(ControlEvent event) {
  if (!ready)
    return;

  String name = event.getController().getName();

  if (name.equals("input")) {
    selectInput("Select Input File", "processInputFile");
  } else if (name.equals("output")) {
    selectInput("Select Output File", "processOutputFile");
  } else if (name.equals("transform")) {
    transformImage();
  }
}

void processInputFile(File selection) {
  if (selection != null) {
    println("File input: " + selection);

    if (!selection.exists()) {
      status = "That file does not exist.";
      println(status);
      return;
    }
    if (selection.exists() && !selection.canRead()) {
      status = "That file cannot be read.";
      println(status);
      return;
    }

    inputImage = loadImage(selection.getAbsolutePath());

    if (inputImage == null) {
      status = "That file could not be loaded.";
      println(status);
      return;
    }

    scaledInputImage = inputImage.get(0, 0, inputImage.width, inputImage.height);

    if (inputImage.height / (float) MAX_IMAGE_HEIGHT > inputImage.width / (float) MAX_IMAGE_WIDTH) {
      scaledInputImage.resize(0, MAX_IMAGE_HEIGHT);
    } else {
      scaledInputImage.resize(MAX_IMAGE_WIDTH, 0);
    }

    if (wroteOutput) {
      // so we don't overwrite the file again by mistake.
      scaledOutputImage = null;
      outputFile = null;
    }
    status = null;
  }
}

void processOutputFile(File selection) {
  println("File output " + selection);

  if (selection != null) {
    if (selection.exists()) {
      if (!selection.canWrite()) {
        status = "Cannot write to that file.";
        println(status);
        return;
      }
    } else { // selection does not exist yet.
      try {
        // test file creation to test file permissions.
        selection.createNewFile();
        selection.delete();
      } catch (IOException e) {
        status = "Cannot write to that file.";
        println(status);
        return;
      }
    }

    status = "";
    outputFile = selection;
    wroteOutput = false;
  }
}

void transformImage() {
  // validation check
  status = "";
  if (inputImage == null) {
    status += "Please select an input file.\n";
  }
  if (outputFile == null) {
    status += "Please select an output file.\n";
  }
  if (action == null) {
    status += "Please select a transformation.\n";
  }
  if (currentSimulator == null || currentDaltonizer == null) {
    status += "Please select a color deficiency.\n";
  }
  if (!status.equals("")) {
    println(status.trim());
    return;
  }

  if (action == Action.SIMULATE) {
    outputImage = currentSimulator.transformPImage(inputImage);
  } else if (action == Action.DALTONIZE) {
    outputImage = currentDaltonizer.transformPImage(inputImage);
  } else if (action == Action.DALTONIZE_AND_SIMULATE) {
    outputImage = currentDaltonizer.transformPImage(inputImage);
    outputImage = currentSimulator.transformPImage(outputImage);
  }
  outputImage.save(outputFile.getAbsolutePath());

  scaledOutputImage = outputImage.get(0, 0, outputImage.width, outputImage.height);

  if (outputImage.height / (float) MAX_IMAGE_HEIGHT > outputImage.width
        / (float) MAX_IMAGE_WIDTH) {
    scaledOutputImage.resize(0, MAX_IMAGE_HEIGHT);
  } else {
    scaledOutputImage.resize(MAX_IMAGE_WIDTH, 0);
  }

  status = "Saved to " + outputFile.getName();
  wroteOutput = true;
  println(status);
}

Map<Integer, String> createDropdownMap(String itemList) {
  Map<Integer, String> map = new HashMap<Integer, String>();
  for (String item : itemList.split(",")) {
    map.put(map.size(), item.trim());
  }

  return map;
}

Button addButton(String name, String label, float y) {
  Button button = cp5.addButton(name);
  button.setCaptionLabel(label);
  button.setValue(0);
  button.setPosition(10, y);
  button.setSize(150, 25);
  button.setColorActive(0xFFAAAAAA);
  button.setColorForeground(0xFF999999);
  button.setColorBackground(0xFF333333);

  return button;
}

DropdownList addDropdown(String name, float y, Map<Integer, String> menuItems) {
  int itemHeight = 25;
  DropdownList dropdownList = cp5.addDropdownList(name)
        .setPosition(10, y)
        .setSize(150, (menuItems.size() + 1) * itemHeight)
        .setItemHeight(itemHeight).setBarHeight(itemHeight);
  for (Entry<Integer, String> entry : menuItems.entrySet()) {
    dropdownList.addItem(entry.getValue(), entry.getKey());
  }
  dropdownList.setColorValueLabel(color(255));
  dropdownList.setColorForeground(0xFF999999);
  dropdownList.setColorBackground(0xFF333333);
  dropdownList.setColorActive(0xFFAAAAAA);

  return dropdownList;
}

Slider addSlider(String variable, String caption, float y, int min, int max) {
  Slider slider = cp5.addSlider(variable);
  slider.setPosition(10, y);
  slider.setRange(min, max);
  slider.setSize(105, 25);
  slider.setCaptionLabel(caption);
  slider.setColorCaptionLabel(color(0));
  slider.setColorValueLabel(color(255));
  slider.setColorForeground(0xFF999999);
  slider.setColorBackground(0xFF333333);
  slider.setColorActive(0xFFAAAAAA);

  return slider;
}

void createControls() {
  float yOffset = 0.5f;
  int controlSpace = 30;

  DropdownList actionDropdown = addDropdown("Transformation",
        (controlSpace * yOffset++), actionNameMap).addListener(
        new ActionListener());
  DropdownList deficiencyDropdown = addDropdown("Color Deficiency",
        (controlSpace * yOffset++), deficiencyNameMap).addListener(
        new DeficiencyListener());
  addSlider("amount", "% Amount", (controlSpace * yOffset++), 0, 100)
        .addListener(new AmountListener());

  addButton("input", "Input file", (controlSpace * yOffset++));
  addButton("output", "Output file", (controlSpace * yOffset++));
  addButton("transform", "Transform", (controlSpace * yOffset++));

  deficiencyDropdown.bringToFront().close();
  actionDropdown.bringToFront().close();
}

void setCurrentGenerators() {
  if (deficiency == null) {
    return;
  } else {
    /*
     * create generators for color blindness simulation and daltonization.
     *
     * for both, use .setDynamicAmount() to indicate that the Amount value may
     * change in the future. This enables the library to optimize its
     * performance. 
     */
    currentSimulator = colorBlindness.simulate(deficiency)
        .setDynamicAmount().setAmount(amount / 100f);
    currentDaltonizer = colorBlindness.daltonize(deficiency)
        .setDynamicAmount().setAmount(amount / 100f);
  }
}