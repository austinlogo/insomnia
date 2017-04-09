package northstar.planner.models.drawer;

import java.io.Serializable;

import northstar.planner.models.BaseModel;

public class ShallowModel implements Serializable {
    private long id;
    private String title;

    public <T extends BaseModel> ShallowModel(T model) {
        id = model.getId();
        title = model.getTitle();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
