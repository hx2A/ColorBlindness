class ActionListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    String newActionName = actionNameMap.get((int) theEvent.getValue());

    if (action == null || !action.toString().equals(newActionName)) {
      println("setting action to: " + newActionName);

      switch (newActionName) {
      case "Simulate":
        action = Action.SIMULATE;
        break;
      case "Daltonize":
        action = Action.DALTONIZE;
        break;
      case "Daltonize and Simulate":
        action = Action.DALTONIZE_AND_SIMULATE;
        break;
      default:
        throw new RuntimeException("Unknown action");
      }
    }

    scaledOutputImage = null;
    status = null;
  }
}
