package com.jsf.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.jsf.entities.Reservation;
import com.jsf.entities.ReservationTable;
import com.jsf.entities.Tables;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO (Data Access Object) odpowiedzialne za zarządzanie operacjami związanymi z encjami
 * {@link Reservation}, {@link ReservationTable}, oraz {@link Tables}.
 * Zapewnia dostęp do bazy danych w kontekście operacji związanych z rezerwacjami.
 */
@Stateless
public class ReservationDAO {

    @PersistenceContext
    private EntityManager em; // EntityManager do interakcji z bazą danych

    /**
     * Tworzy nową rezerwację wraz z przypisaniem stolika.
     *
     * @param reservation obiekt rezerwacji do zapisania.
     * @param tableId identyfikator stolika przypisanego do rezerwacji.
     * @throws IllegalArgumentException jeśli stolik o podanym ID nie istnieje.
     */
    public void createReservation(Reservation reservation, Integer tableId) {
        Tables table = em.find(Tables.class, tableId);
        if (table == null) {
            throw new IllegalArgumentException("Nie znaleziono stolika o ID: " + tableId);
        }

        ReservationTable reservationTable = new ReservationTable();
        reservationTable.setReservation(reservation);
        reservationTable.setTable(table);

        reservation.addReservationTable(reservationTable);
        em.persist(reservation);
    }

    /**
     * Znajduje rezerwację na podstawie jej ID.
     *
     * @param id identyfikator rezerwacji.
     * @return obiekt {@link Reservation}, jeśli istnieje; w przeciwnym razie null.
     */
    public Reservation find(Integer id) {
        return em.find(Reservation.class, id);
    }

    /**
     * Sprawdza, czy istnieje rezerwacja dla danego stolika, daty i godziny.
     *
     * @param tableId identyfikator stolika.
     * @param date data rezerwacji.
     * @param time godzina rezerwacji.
     * @return true, jeśli rezerwacja istnieje; w przeciwnym razie false.
     */
    public boolean reservationExists(int tableId, Date date, Time time) {
        String jpql = "SELECT COUNT(rt) " +
                      "FROM ReservationTable rt " +
                      "WHERE rt.table.id = :tableId " +
                      "AND rt.reservation.date = :date " +
                      "AND rt.reservation.time = :time";

        Long count = em.createQuery(jpql, Long.class)
                       .setParameter("tableId", tableId)
                       .setParameter("date", date)
                       .setParameter("time", time)
                       .getSingleResult();
        return count > 0;
    }

    /**
     * Pobiera listę rezerwacji przypisanych do danego użytkownika, posortowaną malejąco po ID.
     *
     * @param userId identyfikator użytkownika.
     * @return lista obiektów {@link Reservation} przypisanych do użytkownika.
     */
    public List<Reservation> getReservationsByUserId(int userId) {
        String jpql = "SELECT DISTINCT r FROM Reservation r " +
                      "LEFT JOIN FETCH r.reservationTables " +
                      "LEFT JOIN FETCH r.reservationTables.table " +
                      "WHERE r.userId = :userId " +
                      "ORDER BY r.id DESC";
        return em.createQuery(jpql, Reservation.class)
                 .setParameter("userId", userId)
                 .getResultList();
    }

    /**
     * Pobiera wszystkie dostępne stoliki z bazy danych.
     *
     * @return lista obiektów {@link Tables}.
     */
    public List<Tables> getAllTables() {
        return em.createQuery("SELECT t FROM Tables t", Tables.class).getResultList();
    }

    /**
     * Pobiera listę zajętych godzin w formacie {@link LocalTime} dla danego stolika i daty.
     *
     * @param tableId identyfikator stolika.
     * @param date data rezerwacji.
     * @return lista zajętych godzin w formacie {@link LocalTime}.
     */
    public List<LocalTime> getOccupiedTimesForTable(int tableId, java.sql.Date date) {
        String jpql = "SELECT DISTINCT r.time FROM Reservation r " +
                      "JOIN r.reservationTables rt " +
                      "WHERE rt.table.id = :tableId AND r.date = :date";

        return em.createQuery(jpql, Time.class)
                 .setParameter("tableId", tableId)
                 .setParameter("date", date)
                 .getResultList()
                 .stream()
                 .map(Time::toLocalTime)
                 .collect(Collectors.toList());
    }
    
    public List<Reservation> getAllReservations() {
    	String jpql = "SELECT DISTINCT r FROM Reservation r " +
                "LEFT JOIN FETCH r.user " +
                "LEFT JOIN FETCH r.reservationTables " +
                "LEFT JOIN FETCH r.reservationTables.table " +
                "ORDER BY r.date DESC, r.time DESC";
        return em.createQuery(jpql, Reservation.class).getResultList();
    }
    
    public void updateReservation(Reservation reservation) {
        Reservation existing = em.find(Reservation.class, reservation.getId());
        if (existing != null) {
            existing.setStatus(reservation.getStatus());
            em.merge(existing); // Aktualizacja w bazie danych
        }
    }


}
