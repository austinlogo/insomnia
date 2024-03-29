package northstar.planner.metrics;

public enum AnalyticsEventType {
    STARTED("STARTED"),
    ADD_THEME("ADD_THEME"),
    ADD_GOAL("ADD_GOAL"),
    ADD_TASK("ADD_TASK");

    private final String event;

    private AnalyticsEventType(final String text) {
        event = text;
    }

    @Override
    public String toString() {
        return event;
    };

    public boolean equals(String otherEvent) {
        return event.equals(otherEvent);
    }
}