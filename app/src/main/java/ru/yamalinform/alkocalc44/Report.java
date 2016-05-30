package ru.yamalinform.alkocalc44;


import java.sql.Date;
import java.util.Calendar;

public class Report {
    private int id;
    private int BottleId;
    private Date date;
    private String alkach;
    private int stars;
    private String report;

    public Report() {
        super();
        setDate(new Date(Calendar.getInstance().getTime().getTime()));
    }

    public Report(int BottleId) {
        super();
        setBottleId(BottleId);
        setDate(new Date(Calendar.getInstance().getTime().getTime()));
    }

    public Report(int BottleId, String alkach, int stars, String report) {
        super();
        setBottleId(BottleId);
        setDate(new Date(Calendar.getInstance().getTime().getTime()));
        setAlkach(alkach);
        setStars(stars);
        setReport(report);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBottleId() {
        return BottleId;
    }

    public void setBottleId(int bottleId) {
        BottleId = bottleId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAlkach() {
        return alkach;
    }

    public void setAlkach(String alkach) {
        this.alkach = alkach;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
