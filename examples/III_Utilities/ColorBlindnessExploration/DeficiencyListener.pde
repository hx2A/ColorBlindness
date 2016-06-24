class DeficiencyListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    int newDeficiencyNumber = (int) theEvent.getValue();
    String newDeficiencyName = deficiencyNameMap.get(newDeficiencyNumber);

    if (deficiency == null
            || !deficiency.toString().equals(newDeficiencyName)) {
      println("setting deficiency to: " + newDeficiencyName);

      switch (newDeficiencyNumber) {
      case 0:
        deficiency = Deficiency.PROTANOPE;
        break;
      case 1:
        deficiency = Deficiency.DEUTERANOPE;
        break;
      case 2:
        deficiency = Deficiency.TRITANOPE;
        break;
      case 3:
        deficiency = Deficiency.ACHROMATOPE;
        break;
      default:
        throw new RuntimeException("Unknown color deficiency");
      }

      currentGenerator = setCurrentGenerator();
    }
  }
}