class AmountListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    float newAmount = theEvent.getValue();

    if (currentSimulator != null && currentDaltonizer != null) {
      currentSimulator.setAmount(newAmount / 100f);
      currentDaltonizer.setAmount(newAmount / 100f);
    }

    scaledOutputImage = null;
  }
}
