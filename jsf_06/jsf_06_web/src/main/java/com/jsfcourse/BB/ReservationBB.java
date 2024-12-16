package com.jsfcourse.BB;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

@Named
@ViewScoped
public class ReservationBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private int table;
    private String time;
    private int guests;
    private List<String> availableTimes;

    @PostConstruct
    public void init() {
        generateAvailableTimes();
    }

    private void generateAvailableTimes() {
        availableTimes = IntStream.rangeClosed(15, 23)
                .mapToObj(hour -> String.format("%02d:00", hour))
                .collect(Collectors.toList());
    }

    public LocalDate getToday() {
        return LocalDate.now();
    }

    // Gettery i settery
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public List<String> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}
