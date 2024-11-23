package com.jsf.entities;

import java.io.Serializable;
import jakarta.persistence.*;

/**
 * The persistent class for the reservation_tables database table.
 */
@Entity
@Table(name="reservation_tables")
@NamedQuery(name="ReservationTable.findAll", query="SELECT r FROM ReservationTable r")
public class ReservationTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    //bi-directional many-to-one association to Reservation
    @ManyToOne
    private Reservation reservation;

    //bi-directional many-to-one association to Tables 
    @ManyToOne
    private Tables table; 

    public ReservationTable() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return this.reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Tables getTable() { 
        return this.table;
    }

    public void setTable(Tables table) { 
        this.table = table;
    }
}
