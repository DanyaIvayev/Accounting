package com.example.accounting.Entity;

/**
 * Created by Дамир on 18.12.2015.
 */
public class IncomeCategory {
    private static int incID=0;
    private int id;
    private String incomeName;

    public IncomeCategory(String incomeName) {
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

    public void setIncomeName(String incomeName) {
        this.incomeName = incomeName;
    }

    public static void setIncID(int incID) {
        IncomeCategory.incID = incID;
    }

    @Override
    public String toString() {
        return "IncomeCategory[" +
                "id=" + id +
                ", incomeName='" + incomeName + '\'' +
                ']';
    }
}
