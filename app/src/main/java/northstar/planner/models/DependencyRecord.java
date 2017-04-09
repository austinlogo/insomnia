package northstar.planner.models;

import android.database.Cursor;

import northstar.planner.models.tables.DependencyTable;

public class DependencyRecord extends BaseModel {
    private int dependsOn;
    private DependencyStatus dependencyStatus;

    public DependencyRecord(int id, int dependsOn, DependencyStatus dependencyStatus) {
        _id = id;
        this.dependsOn = dependsOn;
        this.dependencyStatus = dependencyStatus;
    }

    public DependencyRecord(Cursor c) {
        super(c);
        this.dependsOn = getColumnInt(c, DependencyTable.DEPENDS_ON);
        this.dependencyStatus = DependencyStatus.valueOf(getColumnString(c, DependencyTable.DEPENDENCY_STATUS));
    }

    @Override
    public String getTitle() {
        return "" + _id;
    }

    public int getDependsOn() {
        return dependsOn;
    }

    public DependencyStatus getDependencyStatus() {
        return dependencyStatus;
    }
}
