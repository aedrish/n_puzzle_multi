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
    private int breedte;
    private int hoogte;
    private boolean lastImage;

    public CroppedImage(Bitmap image, int x, int y, int breedte, int hoogte, int position, boolean lastImage) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.breedte = breedte;
        this.hoogte = hoogte;
        this.position = position;
        if(image != null) {
            this.croppedImage = Bitmap.createBitmap(this.image, this.x * this.breedte, this.y * this.hoogte, this.breedte, this.hoogte);
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

    public int getBreedte() {
        return breedte;
    }

    public void setBreedte(int breedte) {
        this.breedte = breedte;
    }

    public int getHoogte() {
        return hoogte;
    }

    public void setHoogte(int hoogte) {
        this.hoogte = hoogte;
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
            this.croppedImage = Bitmap.createBitmap(this.image, this.x * this.breedte, this.y * this.hoogte, this.breedte, this.hoogte);
        }
    }

}
