package northstar.planner.persistence;


public enum TargetUnit {
    WEEKDAY("WEEKDAY"),
    MONTH("MONTH");

    private String targetUnits;
    TargetUnit(String input) {
        targetUnits = input;
    }

    public String getValue() {
        return targetUnits;
    }
}
