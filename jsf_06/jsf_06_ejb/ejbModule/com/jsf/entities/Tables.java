package com.jsf.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

/**
 * The persistent class for the tables database table.
 */
@Entity
@Table(name = "tables") // Upewnij się, że nazwa tabeli w bazie jest poprawna
@NamedQuery(name = "Tables.findAll", query = "SELECT t FROM Tables t")
public class Tables implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String location;

    private String name;

    // Bi-directional many-to-one association to ReservationTable
    @OneToMany(mappedBy = "table")
    private List<ReservationTable> reservationTables;

    public Tables() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ReservationTable> getReservationTables() {
        return this.reservationTables;
    }

    public void setReservationTables(List<ReservationTable> reservationTables) {
        this.reservationTables = reservationTables;
    }

    public ReservationTable addReservationTable(ReservationTable reservationTable) {
        getReservationTables().add(reservationTable);
        reservationTable.setTable(this);
        return reservationTable;
    }

    public ReservationTable removeReservationTable(ReservationTable reservationTable) {
        getReservationTables().remove(reservationTable);
        reservationTable.setTable(null);
        return reservationTable;
    }
}
