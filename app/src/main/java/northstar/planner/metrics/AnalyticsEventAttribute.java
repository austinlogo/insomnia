package northstar.planner.metrics;

public enum AnalyticsEventAttribute {
    VISITED_PAGE("VISITED_PAGE");

    private final String event;

    private AnalyticsEventAttribute(final String text) {
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