package northstar.planner.models;

public enum DependencyStatus {
    BLOCKED("BLOCKED"),
    UNBLOCKED("UNBLOCKED");

    private String status;

    private DependencyStatus(String status) {
        this.status = status;
    }

    public String value() {
        return status;
    }
}
