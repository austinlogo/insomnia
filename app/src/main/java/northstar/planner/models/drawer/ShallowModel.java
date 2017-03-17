package northstar.planner.models.drawer;

import northstar.planner.models.BaseModel;

/**
 * Created by Austin on 3/16/2017.
 */
public class ShallowModel {
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
