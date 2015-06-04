package nl.han.s478026.bram.npuzzel;

/**
 * Created by bram on 4-6-2015.
 */
public enum Difficulty {
    VERY_EASY(R.string.difficulty_very_easy, 2, 5),
    EASY(R.string.difficulty_easy, 3, 10),
    MEDIUM(R.string.difficulty_medium, 4, 15),
    HARD(R.string.difficulty_hard, 5, 20);
    private int difficulty;
    private int numberOfTiles;
    private int minutes;

    Difficulty(int difficulty, int numberOfTiles, int minutes){
        this.difficulty = difficulty;
        this.numberOfTiles = numberOfTiles;
        this.minutes = minutes;
    }

    public int getNumberOfTiles() {
        return numberOfTiles;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getDifficulty() {
        return difficulty;
    }

}