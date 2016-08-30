class RGBtoGrayscale extends Generator {

  private int[] colorMap;

  public RGBtoGrayscale() {
    colorMap = buildColorMap();
  }

  private int[] buildColorMap() {
    int[] colorMap = new int[256 * 256 * 256];

    for (int c = 0; c < colorMap.length; ++c) {
      int red = (c & 0xFF0000) >> 16;
      int green = (c & 0x00FF00) >> 8;
      int blue = (c & 0x0000FF);

      int avg = (red + green + blue) / 3;

      colorMap[c] = 0xFF000000 | (avg << 16) | (avg << 8) | avg;
    }

    return colorMap;
  }

  public void transformPixels(int[] pixels) {
    for (int i = 0; i < pixels.length; ++i) {
      pixels[i] = colorMap[pixels[i] & 0x00FFFFFF];
    }
  }
}
