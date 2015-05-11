package nl.han.s478026.bram.npuzzel;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by bram on 6-4-2015.
 */
public class ImageItem {
    private int resourceId;
    private String name;
    private Bitmap bitmap;

    public ImageItem(int resourceId, String name, Bitmap bitmap) {
        this.resourceId = resourceId;
        this.name = name;
        this.bitmap = bitmap;
    }


    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
