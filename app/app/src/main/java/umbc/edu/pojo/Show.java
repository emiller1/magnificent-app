package umbc.edu.pojo;

import android.graphics.drawable.Drawable;

/**
 * Created by milleej1 on 4/26/17.
 */

public class Show {

    String title;
    String description;
    Drawable drawable;

    public Show(){
    }

    public Show(String title, String description, Drawable drawable ){
        this.title = title;
        this.description = description;
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
