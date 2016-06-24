class ActionListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    String newActionName = actionNameMap.get((int) theEvent.getValue());

    if (actionName == null || !actionName.equals(newActionName)) {
      println("setting action to: " + newActionName);

      actionName = newActionName;

      currentGenerator = setCurrentGenerator();
    }
  }
}
