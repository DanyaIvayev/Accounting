package com.example.accounting.Entity;

/**
 * Created by Дамир on 18.12.2015.
 */
public class RateCategory {
    private static int incID=0;
    private int id;
    private String incomeName;

    public RateCategory(String incomeName) {
        id=++incID;
        this.incomeName = incomeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncomeName() {
        return incomeName;
    }

    public static void setIncID(int incID) {
        RateCategory.incID = incID;
    }

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }

    @Override
    public String toString() {
        return "RateCategory[" +
                "id=" + id +
                ", incomeName='" + incomeName + '\'' +
                ']';
    }
}
