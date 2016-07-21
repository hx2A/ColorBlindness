import java.util.Map;
import java.util.Map.Entry;

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
String deficiencyNames = "Protanope, Deuteranope, Tritanope, Achromatope";

Map<Integer, String> actionNameMap;
Map<Integer, String> pictureNameMap;
Map<Integer, String> deficiencyNameMap;
PImage[] pimages;

public void setup() {
  size(500, 625);

  cp5 = new ControlP5(this);
  colorBlindness = new ColorBlindness(this);
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

  image(img, 150, 0);

  if (action == null || currentSimulator == null || currentDaltonizer == null) {
    // do nothing
  } else if (action == Action.SIMULATE) {
    img = currentSimulator.transformPImage(img);
  } else if (action == Action.DALTONIZE) {
    img = currentDaltonizer.transformPImage(img);
  } else if (action == Action.DALTONIZE_AND_SIMULATE) {
    img = currentDaltonizer.transformPImage(img);
    img = currentSimulator.transformPImage(img);
  }
  image(img, 150, 325);
}

void setCurrentGenerators() {
  if (deficiency == null) {
    return;
  } else {
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
  File[] files = (new File("data")).listFiles();

  pictureNameMap = new HashMap<Integer, String>();
  List<PImage> pictures = new ArrayList<PImage>();

  for (int i = 0; i < files.length; ++i) {
    PImage picture = loadImage(files[i].getAbsolutePath());
    if (picture != null) {
      // if the picture cannot be loaded, loadImage returns null.
      // no need to print an error message as Processing does that
      // for us.
      pictures.add(picture);
      pictureNameMap.put(i, files[i].getName().replace('_', ' ').split("\\.")[0]);
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