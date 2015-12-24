package com.example.accounting.Entity;

import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * Created by Дамир on 18.12.2015.
 */
public class Operation {

    public static final int INCOME=0;
    public static final int RATE=1;
    public static final int TRANSFER=2;
    private static int incID=0;
    private int id;
    private String date;
    private int type;
    private double value;
    private int billFrom;
    private int billTo;
    private String category;
    private String description;

    public Operation(String date, int type, double value, int billFrom, String category, String description) {
        id = ++incID;
        this.date = date;
        this.type = type;
        this.value = Math.round( value * 100.0 ) / 100.0;
//        DecimalFormat df = new DecimalFormat("#.##");
//        String formate = df.format(value);
//        try {
//            this.value = (Double) df.parse(formate);
//        } catch (ParseException pe){
//            pe.printStackTrace();
//        }
        this.billFrom = billFrom;
        this.description = description;
        this.category=category;
        billTo=-1;
    }

    public Operation(String date, int type, double value, int billFrom, int billTo, String description) {
        id = ++incID;
        this.date = date;
        this.type = type;
//        DecimalFormat df = new DecimalFormat("#.##");
//        String formate = df.format(value);
//        try {
//            this.value = (Double) df.parse(formate);
//        } catch (ParseException pe){
//            pe.printStackTrace();
//        }
        this.value = Math.round( value * 100.0 ) / 100.0;
        this.billFrom = billFrom;
        this.description = description;
        this.billTo = billTo;
        category="";
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
//        DecimalFormat df = new DecimalFormat("#.##");
//        String formate = df.format(value);
//        try {
//            this.value = (Double) df.parse(formate);
//        } catch (ParseException pe){
//            pe.printStackTrace();
//        }
        this.value = Math.round( value * 100.0 ) / 100.0;
    }

    public int getBillFrom() {
        return billFrom;
    }

    public void setBillFrom(int billFrom) {
        this.billFrom = billFrom;
    }

    public int getBillTo() {
        return billTo;
    }

    public void setBillTo(int billTo) {
        this.billTo = billTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static void setIncID(int incID) {
        Operation.incID = incID;
    }

    @Override
    public String toString() {
        return "Operation[" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", type=" + type +
                ", value=" + value +
                ", billFrom=" + billFrom +
                ", billTo=" + billTo +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (id != operation.id) return false;
        if (type != operation.type) return false;
        if (Double.compare(operation.value, value) != 0) return false;
        if (billFrom != operation.billFrom) return false;
        if (billTo != operation.billTo) return false;
        if (date != null ? !date.equals(operation.date) : operation.date != null) return false;
        if (category != null ? !category.equals(operation.category) : operation.category != null)
            return false;
        return !(description != null ? !description.equals(operation.description) : operation.description != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + type;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + billFrom;
        result = 31 * result + billTo;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
