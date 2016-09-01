/*
Color Blindness Exploration Tool
 ================================
 
 Use the dropdowns on the left of the sketch to explore different kinds of
 color blindness. 
 
 Transformation
 --------------
 Simulate - simulate color blindness
 Daltonize - daltonization color correction
 Daltonize and Simulate - simulate, then daltonize
 
 The last one will give you a better idea of how well the
 daltonization works.
 
 Use the picture dropdown to experiment with different images.
 
 Vary the amount slide bar to explore partial color blindness.
 
 If you like you can add new pictures to the data directory. Make sure the new
 image sizes are 400x300.
 */

import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Slider;

import colorblind.ColorBlindness;
import colorblind.Deficiency;
import colorblind.generators.ColorTranformGenerator;

enum Action {
  SIMULATE, DALTONIZE, DALTONIZE_AND_SIMULATE
}

ControlP5 cp5;
ColorBlindness colorBlindness;
ColorTranformGenerator currentSimulator;
ColorTranformGenerator currentDaltonizer;

// ControlP5 variables
Action action;
int pictureIndex;
Deficiency deficiency;
int amount;

String actionNames = "Simulate, Daltonize, Daltonize and Simulate";
String deficiencyNames = "Protanopia, Deuteranopia, Tritanopia, Achromatopsia, Blue Cone Monochromacy";

Map<Integer, String> actionNameMap;
Map<Integer, String> pictureNameMap;
Map<Integer, String> deficiencyNameMap;
PImage[] pimages;

public void setup() {
  size(570, 665);

  cp5 = new ControlP5(this);
  colorBlindness = new ColorBlindness(this);
  // deactivate() turns off automatic transformations of the entire sketch
  // window. Use this if you only want to transform a PImage or a part of
  // your sketch.
  colorBlindness.deactivate();

  actionNameMap = createDropdownMap(actionNames);
  deficiencyNameMap = createDropdownMap(deficiencyNames);

  action = null;
  pictureIndex = 0;
  deficiency = null;
  amount = 100;

  preparePictureData();
  createControls();
  setCurrentGenerators();
}

void draw() {
  background(255);

  PImage img = pimages[pictureIndex];

  image(img, 150, 20);

  /*
  Calling .transformPImage(img) creates a copy of the PImage object and transforms
   the copy.  
   */

  if (action == null || currentSimulator == null || currentDaltonizer == null) {
    // do nothing
  } else if (action == Action.SIMULATE) {
    // simulate color blindness using current color blindness settings
    img = currentSimulator.transformPImage(img);
  } else if (action == Action.DALTONIZE) {
    // perform daltonization using current color blindness settings
    img = currentDaltonizer.transformPImage(img);
  } else if (action == Action.DALTONIZE_AND_SIMULATE) {
    // perform daltonization and then simulation
    img = currentDaltonizer.transformPImage(img);
    img = currentSimulator.transformPImage(img);
  }
  image(img, 150, 345);
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
    currentSimulator = colorBlindness.simulate(deficiency).setDynamicAmount()
      .setAmount(amount / 100f);
    currentDaltonizer = colorBlindness.daltonize(deficiency)
      .setDynamicAmount().setAmount(amount / 100f);
  }
}

void createControls() {
  float yOffset = 0.5f;
  int controlSpace = 30;

  DropdownList actionDropdown = addDropdown("Transformation", 
    (controlSpace * yOffset++), actionNameMap).addListener(
    new ActionListener());
  DropdownList pictureDropdown = addDropdown("Picture", 
    (controlSpace * yOffset++), pictureNameMap).addListener(
    new PictureListener());
  DropdownList deficiencyDropdown = addDropdown("Color Deficiency", 
    (controlSpace * yOffset++), deficiencyNameMap).addListener(
    new DeficiencyListener());
  addSlider("amount", "% Amount", (controlSpace * yOffset++), 0, 100)
    .addListener(new AmountListener());

  deficiencyDropdown.bringToFront().close();
  pictureDropdown.bringToFront().close();
  actionDropdown.bringToFront().close();
}

void preparePictureData() {
  File[] files = (new File(dataPath(""))).listFiles();

  pictureNameMap = new HashMap<Integer, String>();
  List<PImage> pictures = new ArrayList<PImage>();

  for (int i = 0; i < files.length; ++i) {
    PImage picture = loadImage(files[i].getAbsolutePath());
    if (picture != null) {
      // if the picture cannot be loaded, loadImage returns null.
      // no need to print an error message as Processing does that
      // for us.
      pictures.add(picture);
      String name = files[i].getName().replace('_', ' ').split("\\.")[0];
      pictureNameMap.put(i, name);
      if (name.equals("full spectrum")) {
        pictureIndex = i;
      }
    }
  }
  pimages = pictures.toArray(new PImage[pictures.size()]);
}

Map<Integer, String> createDropdownMap(String itemList) {
  Map<Integer, String> map = new HashMap<Integer, String>();
  for (String item : itemList.split(",")) {
    map.put(map.size(), item.trim());
  }

  return map;
}

DropdownList addDropdown(String name, float y, 
  Map<Integer, String> menuItems) {
  int itemHeight = 25;
  DropdownList dropdownList = cp5.addDropdownList(name).setPosition(10, y)
    .setSize(120, (menuItems.size() + 1) * itemHeight)
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
  slider.setSize(80, 25);
  slider.setCaptionLabel(caption);
  slider.setColorCaptionLabel(color(0));
  slider.setColorValueLabel(color(255));
  slider.setColorForeground(0xFF999999);
  slider.setColorBackground(0xFF333333);
  slider.setColorActive(0xFFAAAAAA);

  return slider;
}