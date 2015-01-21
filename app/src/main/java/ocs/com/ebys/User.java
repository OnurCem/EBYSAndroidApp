package ocs.com.ebys;

/**
 * Created by Onur Cem on 1/21/2015.
 */
public class User {
    private String name;
    private String number;

    public User(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
