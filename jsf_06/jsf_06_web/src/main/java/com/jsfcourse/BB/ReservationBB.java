package com.jsfcourse.BB;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.jsf.dao.ReservationDAO;
import com.jsf.entities.Reservation;

@Named
@ViewScoped
public class ReservationBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date date; // Obsługuje `p:calendar`
    private String time; // String do wyboru godziny
    private int table;
    private int guests;
    private List<String> availableTimes;

    @Inject
    private ReservationDAO reservationDAO;

    @PostConstruct
    public void init() {
        generateAvailableTimes();
    }

    // Generowanie dostępnych godzin rezerwacji
    private void generateAvailableTimes() {
        availableTimes = IntStream.rangeClosed(15, 23)
                .mapToObj(hour -> String.format("%02d:00", hour))
                .collect(Collectors.toList());
    }

    public LocalDate getToday() {
        return LocalDate.now();
    }

    // Gettery i settery
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
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

    public void addReservation() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        try {
            // Tworzenie obiektu rezerwacji
            Reservation reservation = new Reservation();

            // Ustawianie pól
            reservation.setDate(date);
            Time reservationTime = Time.valueOf(time + ":00");
            reservation.setTime(reservationTime);
            reservation.setNumberOfPeople(guests);
            reservation.setStatus("Oczekujące");
            reservation.setCreatedByUserId(1); // Przykładowy ID użytkownika
            reservation.setModifiedByUserId(1);
            reservation.setCreationDate(new Timestamp(System.currentTimeMillis()));
            reservation.setModificationDate(new Timestamp(System.currentTimeMillis()));
            reservation.setUserId(1); // Przykładowy ID użytkownika

            // Debug
            System.out.println("Rezerwacja przed zapisem: " + reservation);

            // Zapis do bazy danych wraz z powiązaniem stolika
            System.out.println("Przed wywołaniem create()");
            reservationDAO.createReservation(reservation, table);
            System.out.println("Po wywołaniu create()");

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Rezerwacja została dodana!", null));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas dodawania rezerwacji.", null));
        }
    }


}
