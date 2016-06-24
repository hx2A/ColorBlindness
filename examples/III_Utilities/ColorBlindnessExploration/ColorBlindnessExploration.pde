import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import colorblind.ColorBlindness;
import colorblind.Deficiency;
import colorblind.generators.ColorTranformGenerator;
import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Slider;

ControlP5 cp5;
ColorBlindness colorBlindness;
ColorTranformGenerator currentGenerator;

// ControlP5 variables
String actionName;
int pictureIndex;
Deficiency deficiency;
int amount;

String[] filenames = { "hues.jpg", "beach.jpg", "fall_trees.jpg", "sunset.jpg", "van_gogh_starry_night.jpg" };

String actionNames = "Simulate, Daltonize";
String pictureNames = "Color Hues, Beach Scene, Fall Trees, Sunset, Van Gogh's Starry Night";
String deficiencyNames = "Protanope, Deuteranope, Tritanope, Achromatope";

Map<Integer, String> actionNameMap;
Map<Integer, String> pictureNameMap;
Map<Integer, String> deficiencyNameMap;
PImage[] pimages;

void setup() {
  size(500, 625);

  cp5 = new ControlP5(this);
  colorBlindness = new ColorBlindness(this);
  colorBlindness.deactivate();

  pimages = loadPictures(filenames);
  actionNameMap = createDropdownMap(actionNames);
  pictureNameMap = createDropdownMap(pictureNames);
  deficiencyNameMap = createDropdownMap(deficiencyNames);

  actionName = null;
  pictureIndex = 0;
  deficiency = null;
  amount = 0;

  createControls();
  currentGenerator = setCurrentGenerator();
}

void draw() {
  background(255);

  image(pimages[pictureIndex], 150, 0);

  PImage dualImage = pimages[pictureIndex];
  if (currentGenerator != null) {
    dualImage = currentGenerator.transformPImage(dualImage);
  }
  image(dualImage, 150, 325);
}

ColorTranformGenerator setCurrentGenerator() {
  if (actionName == null || deficiency == null) {
    return null;
  } else if (actionName.equals("Simulate")) {
    return colorBlindness.simulate(deficiency).setDynamicAmount()
            .setAmount(amount / 100f);
  } else {
    return colorBlindness.daltonize(deficiency).setDynamicAmount()
            .setAmount(amount / 100f);
  }
}

void createControls() {
  float yOffset = 0.5f;
  int controlSpace = 23;

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

PImage[] loadPictures(String[] filenames) {
  PImage[] pictures = new PImage[filenames.length];

  int index = 0;
  for (String filename : filenames) {
    pictures[index++] = loadImage(filename);
  }

  return pictures;
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
  int itemHeight = 18;
  DropdownList dropdownList = cp5.addDropdownList(name)
            .setPosition(10, y)
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
  slider.setSize(80, 18);
  slider.setCaptionLabel(caption);
  slider.setColorCaptionLabel(color(0));
  slider.setColorValueLabel(color(255));
  slider.setColorForeground(0xFF999999);
  slider.setColorBackground(0xFF333333);
  slider.setColorActive(0xFFAAAAAA);

  return slider;
}