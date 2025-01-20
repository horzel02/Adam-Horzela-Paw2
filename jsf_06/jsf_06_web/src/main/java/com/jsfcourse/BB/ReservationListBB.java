package com.jsfcourse.BB;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.ReservationDAO;
import com.jsf.entities.Reservation;
import com.jsfcourse.BB.lazy.LazyReservationDataModel;

import org.primefaces.model.LazyDataModel;


/**
 * Managed Bean odpowiedzialny za wyświetlanie listy rezerwacji użytkownika.
 */
@Named
@ViewScoped
public class ReservationListBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations; // Lista rezerwacji użytkownika
    
    private LazyDataModel<Reservation> lazyModel;

    @Inject
    private ReservationDAO reservationDAO; // DAO do obsługi rezerwacji

    @Inject
    private LoginBB loginBB; // Bean logowania

    /**
     * Inicjalizacja komponentu. Pobiera listę rezerwacji zalogowanego użytkownika.
     */
    @PostConstruct
    public void init() {
        if (loginBB != null && loginBB.getLoggedUser() != null) {
            int userId = loginBB.getLoggedUser().getId();
            reservations = reservationDAO.getReservationsByUserId(userId);
            lazyModel = new LazyReservationDataModel(reservationDAO, userId);
        }
    }

    /**
     * Pobiera listę rezerwacji.
     * 
     * @return lista obiektów {@link Reservation}.
     */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * Ustawia listę rezerwacji.
     * 
     * @param reservations lista obiektów {@link Reservation}.
     */
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    public LazyDataModel<Reservation> getLazyModel() {
        return lazyModel;
    }
}
