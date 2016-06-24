import colorblind.ColorBlindness;

PGraphics label;
ColorBlindness colorBlindness;

float rot = 75f;
float rotX = rot * 0.5f;
float rotY = rot * 0.1f;
float rotZ = rot * 0.3f;

void setup() {
  size(300, 300, P3D);
  colorBlindness = new ColorBlindness(this);
  colorBlindness.simulateProtanope();

  label = createGraphics(150, 20);
  label.beginDraw();
  label.textAlign(LEFT, TOP);
  label.fill(0xFFFF0000);
  label.textSize(12);
  label.text("Color Blindness Example", 0, 0);
  label.endDraw();
}

void draw() {
  background(0xFFFFFF);

  rot += 1;

  rotX = rot * 0.5f;
  rotY = rot * 0.1f;
  rotZ = rot * 0.3f;

  strokeWeight(4);
  stroke(0);

  int boxSize = 90;
  int zDepth = -100;

  pushMatrix();
  fill(255, 0, 0);
  translate(width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 255, 0);
  translate(4 * width / 5, height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(0, 0, 255);
  translate(width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();

  pushMatrix();
  fill(128, 128, 128);
  translate(4 * width / 5, 4 * height / 5, zDepth);
  rotateX(radians(rotX));
  rotateY(radians(rotY));
  rotateZ(radians(rotZ));
  box(boxSize);
  popMatrix();
}

void postDraw() {
  copy(label, 0, 0, label.width, label.height, width - label.width,
       height - label.height, label.width, label.height);
}