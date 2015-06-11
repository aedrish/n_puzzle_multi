package nl.han.s478026.bram.npuzzel;

import android.graphics.Bitmap;

/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class CroppedImage {
    private Bitmap croppedImage;
    private Bitmap image;
    private int position;
    private int x;
    private int y;
    private int imageWidth;
    private int imageHeight;
    private boolean lastImage;

    public CroppedImage(Bitmap image, int x, int y, int imageWidth, int imageHeight, int position, boolean lastImage) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.position = position;
        if(image != null) {
            this.croppedImage = Bitmap.createBitmap(this.image, this.x * this.imageWidth, this.y * this.imageHeight, this.imageWidth, this.imageHeight);
        }
        this.lastImage = lastImage;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Bitmap getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(Bitmap croppedImage) {
        this.croppedImage = croppedImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getLastImage() { return lastImage; }

    public void setLastImage(boolean lastImage) { this.lastImage = lastImage; }
    public void recreateImage() {
        if(image != null) {
            this.croppedImage = Bitmap.createBitmap(this.image, this.x * this.imageWidth, this.y * this.imageHeight, this.imageWidth, this.imageHeight);
        }
    }

}
