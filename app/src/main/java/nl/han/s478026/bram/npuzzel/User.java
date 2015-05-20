package nl.han.s478026.bram.npuzzel;

import java.util.UUID;

/**
 * Created by bram on 20-5-2015.
 */
public class User {

    private UUID uid = UUID.randomUUID();
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
