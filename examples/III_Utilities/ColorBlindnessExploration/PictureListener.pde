class PictureListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    pictureIndex = (int) theEvent.getValue();

    String pictureChoice = pictureNameMap.get(pictureIndex);

    println("setting picture to: " + pictureChoice);
  }
}
