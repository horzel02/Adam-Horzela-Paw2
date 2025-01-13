package com.jsf.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.jsf.entities.Reservation;
import com.jsf.entities.ReservationTable;
import com.jsf.entities.Tables;

@Stateless
public class ReservationDAO {

    @PersistenceContext
    private EntityManager em;

    public void createReservation(Reservation reservation, Integer tableId) {
        // Pobranie obiektu Tables na podstawie tableId
        Tables table = em.find(Tables.class, tableId);
        if (table == null) {
            throw new IllegalArgumentException("Nie znaleziono stolika o ID: " + tableId);
        }

        // Utworzenie i ustawienie powiązania ReservationTable
        ReservationTable reservationTable = new ReservationTable();
        reservationTable.setReservation(reservation);
        reservationTable.setTable(table);

        // Dodanie ReservationTable do Reservation
        reservation.addReservationTable(reservationTable);

        // Persist rezerwacji wraz z powiązaniem
        em.persist(reservation);
    }

    public Reservation find(Integer id) {
        return em.find(Reservation.class, id);
    }
}
