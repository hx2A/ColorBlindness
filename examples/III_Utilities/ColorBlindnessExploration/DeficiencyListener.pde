class DeficiencyListener implements ControlListener {
  public void controlEvent(ControlEvent theEvent) {
    String newDeficiencyName = deficiencyNameMap.get((int) theEvent.getValue());

    if (deficiency == null || !deficiency.toString().equals(newDeficiencyName)) {
      println("setting deficiency to: " + newDeficiencyName);

      switch (newDeficiencyName) {
      case "Protanopia":
        deficiency = Deficiency.PROTANOPIA;
        break;
      case "Deuteranopia":
        deficiency = Deficiency.DEUTERANOPIA;
        break;
      case "Tritanopia":
        deficiency = Deficiency.TRITANOPIA;
        break;
      case "Achromatopsia":
        deficiency = Deficiency.ACHROMATOPSIA;
        break;
      case "Blue Cone Monochromacy":
        deficiency = Deficiency.BLUE_CONE_MONOCHROMACY;
        break;
      default:
        throw new RuntimeException("Unknown color deficiency");
      }

      setCurrentGenerators();
    }
  }
}