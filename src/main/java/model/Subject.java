package model;

public class Subject {
    private String name;
    private String day;       // "Mon", "Tue" 등
    private int startHour;    // 9, 10, 11 등
    private int endHour;

    public Subject(String name, String day, int startHour, int endHour) {
        this.name = name;
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String getName() { return name; }
    public String getDay() { return day; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
}
