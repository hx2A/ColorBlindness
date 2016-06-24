class AmountListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    float newAmount = theEvent.getValue();

    if (currentGenerator != null)
      currentGenerator.setAmount(newAmount / 100f);
  }
}
