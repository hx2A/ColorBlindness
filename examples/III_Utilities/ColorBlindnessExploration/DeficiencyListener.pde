class DeficiencyListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    String newDeficiencyName = deficiencyNameMap.get((int) theEvent.getValue());

    if (deficiency == null || !deficiency.toString().equals(newDeficiencyName)) {
      println("setting deficiency to: " + newDeficiencyName);

      switch (newDeficiencyName) {
      case "Protanope":
        deficiency = Deficiency.PROTANOPE;
        break;
      case "Deuteranope":
        deficiency = Deficiency.DEUTERANOPE;
        break;
      case "Tritanope":
        deficiency = Deficiency.TRITANOPE;
        break;
      case "Achromatope":
        deficiency = Deficiency.ACHROMATOPE;
        break;
      default:
        throw new RuntimeException("Unknown color deficiency");
      }

      setCurrentGenerators();
    }
  }
}
