package nl.han.s478026.bram.npuzzel;

import java.util.UUID;

/**
 * Created by bram on 20-5-2015.
 */
public class Room {
    private String uid = UUID.randomUUID().toString();
    private int imageId;
    private User player1;
    private User player2;

    public Room(User player1, User player2, int imageId) {
        this.player1 = player1;
        this.player2 = player2;
        this.imageId = imageId;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
