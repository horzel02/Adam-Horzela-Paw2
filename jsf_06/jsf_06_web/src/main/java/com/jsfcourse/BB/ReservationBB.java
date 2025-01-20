package com.jsfcourse.BB;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.ReservationDAO;
import com.jsf.entities.Reservation;
import com.jsf.entities.Tables;

/**
 * Managed Bean odpowiedzialny za obsługę rezerwacji.
 */
@Named
@ViewScoped
public class ReservationBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date date;
    private String time;
    private Integer tableId;
    private int guests;
    private List<String> availableTimes;
    private List<Tables> tables;

    @Inject
    private ReservationDAO reservationDAO;

    @Inject
    private LoginBB loginBB;
    
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

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
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
    
    private void loadTables() {
        tables = reservationDAO.getAllTables();
    }

    public List<Tables> getTables() {
        return tables;
    }
    

    /**
     * Inicjalizacja komponentu i wygenerowanie listy dostępnych godzin.
     */
    @PostConstruct
    public void init() {
        tableId = null; // Domyślnie brak wybranego stolika
        generateAvailableTimes();
        loadTables(); // Wczytanie stolików z bazy
    }

    /**
     * Metoda wywoływana przy zmianie daty w formularzu.
     * Regeneruje listę dostępnych godzin.
     */
    public void onDateChange() {
        generateAvailableTimes();
    }
    
    public void onTableChange() {
        if (tableId > 0) {
            generateAvailableTimes();
        }
    }

    /**
     * Generowanie dostępnych godzin rezerwacji w zależności od wybranej daty.
     */
    public void generateAvailableTimes() {
        if (tableId == null || date == null) {
            availableTimes = List.of();
            return;
        }

        // Pobierz listę zajętych godzin dla wybranego stolika i daty
        List<LocalTime> occupiedTimes = reservationDAO.getOccupiedTimesForTable(tableId, new java.sql.Date(date.getTime()));

        availableTimes = IntStream.rangeClosed(12, 23)
                .filter(hour -> {
                    LocalDate selectedDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate today = LocalDate.now();
                    int currentHour = LocalTime.now().getHour();

                    // Jeśli data to dzisiaj, odfiltruj godziny wcześniejsze niż obecna
                    if (selectedDate.equals(today) && hour <= currentHour) {
                        return false;
                    }

                    // Sprawdź, czy godzina jest zajęta
                    LocalTime currentHourTime = LocalTime.of(hour, 0);
                    return !occupiedTimes.contains(currentHourTime);
                })
                .mapToObj(hour -> String.format("%02d:00", hour))
                .collect(Collectors.toList());
    }

    /**
     * Zwraca dzisiejszą datę.
     * @return Dzisiejsza data jako LocalDate.
     */
    public LocalDate getToday() {
        return LocalDate.now();
    }

    /**
     * Obsługa procesu dodawania rezerwacji.
     * @return Przekierowanie na stronę główną w przypadku sukcesu, null w przypadku błędu.
     */
    public String addReservation() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        try {
            // Sprawdzanie, czy użytkownik jest zalogowany
            if (loginBB == null || loginBB.getLoggedUser() == null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Musisz być zalogowany, aby dokonać rezerwacji.", null));
                return null;
            }

            // Walidacja liczby gości
            if (guests <= 0) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Liczba gości musi być większa niż 0!", null));
                return null;
            }
            
            // Sprawdzenie czy stolik jest wybrany
            if (tableId == null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Wybierz stolik.", null));
                return null;
            }

            // Konwersja daty i godziny na format SQL
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            Time reservationTime = Time.valueOf(time + ":00");

            // Sprawdzenie, czy istnieje już rezerwacja dla danego stołu, daty i godziny
            if (reservationDAO.reservationExists(tableId, sqlDate, reservationTime)) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                    "Rezerwacja dla tego stołu, daty i godziny już istnieje!", null));
                return null;
            }

            // Przygotowanie i zapisanie rezerwacji
            Reservation reservation = prepareReservation(sqlDate, reservationTime);
            reservationDAO.createReservation(reservation, tableId);

            // Powiadomienie o sukcesie
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                "Rezerwacja została dodana!", null));
            return "/main.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Nieprawidłowe dane: " + e.getMessage(), null));
            return null;
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                "Błąd podczas dodawania rezerwacji.", null));
            return null;
        }
    }

    /**
     * Przygotowanie obiektu rezerwacji na podstawie wprowadzonych danych.
     * @param sqlDate Data rezerwacji w formacie SQL.
     * @param reservationTime Godzina rezerwacji w formacie SQL.
     * @return Obiekt rezerwacji gotowy do zapisania w bazie danych.
     */
    private Reservation prepareReservation(java.sql.Date sqlDate, Time reservationTime) {
        Reservation reservation = new Reservation();
        reservation.setDate(sqlDate);
        reservation.setTime(reservationTime);
        reservation.setNumberOfPeople(guests);
        reservation.setStatus("Oczekujące");
        reservation.setCreatedByUserId(loginBB.getLoggedUser().getId());
        reservation.setModifiedByUserId(loginBB.getLoggedUser().getId());
        reservation.setCreationDate(new Timestamp(System.currentTimeMillis()));
        reservation.setModificationDate(new Timestamp(System.currentTimeMillis()));
        reservation.setUserId(loginBB.getLoggedUser().getId());
        return reservation;
    }
}