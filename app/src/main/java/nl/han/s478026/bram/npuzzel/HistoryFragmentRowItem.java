package nl.han.s478026.bram.npuzzel;

import java.io.Serializable;

/**
 * Created by bram on 4-6-2015.
 */
public class HistoryFragmentRowItem {
    private String key;
    private boolean didWon;
    private long yourScore;
    private long opponentScore;
    private long resourceId;

    public HistoryFragmentRowItem(String key, boolean didWon, long yourScore, long opponentScore, long resourceId) {
        this.key = key;
        this.didWon = didWon;
        this.yourScore = yourScore;
        this.opponentScore = opponentScore;
        this.resourceId = resourceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isDidWon() {
        return didWon;
    }

    public void setDidWon(boolean didWon) {
        this.didWon = didWon;
    }

    public long getYourScore() {
        return yourScore;
    }

    public void setYourScore(long yourScore) {
        this.yourScore = yourScore;
    }

    public long getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(long opponentScore) {
        this.opponentScore = opponentScore;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }
}
