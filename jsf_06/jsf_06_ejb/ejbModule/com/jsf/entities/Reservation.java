package com.jsf.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import jakarta.persistence.Table;


/**
 * The persistent class for the reservations database table.
 * 
 */
@Entity
@Table(name="reservations")
@NamedQuery(name="Reservation.findAll", query="SELECT r FROM Reservation r")
public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="created_by_user_id")
	private Integer createdByUserId;

	@Column(name="creation_date")
	private Timestamp creationDate;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name="modification_date")
	private Timestamp modificationDate;

	@Column(name="modified_by_user_id")
	private Integer modifiedByUserId;

	@Column(name="number_of_people")
	private Integer numberOfPeople;

	private String status;

	private Time time;

	@Column(name="user_id")
	private Integer userId;

	//bi-directional many-to-one association to ReservationTable
	@OneToMany(mappedBy="reservation")
	private List<ReservationTable> reservationTables;

	public Reservation() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCreatedByUserId() {
		return this.createdByUserId;
	}

	public void setCreatedByUserId(Integer createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public Timestamp getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Timestamp getModificationDate() {
		return this.modificationDate;
	}

	public void setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
	}

	public Integer getModifiedByUserId() {
		return this.modifiedByUserId;
	}

	public void setModifiedByUserId(Integer modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	public Integer getNumberOfPeople() {
		return this.numberOfPeople;
	}

	public void setNumberOfPeople(Integer numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Time getTime() {
		return this.time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<ReservationTable> getReservationTables() {
		return this.reservationTables;
	}

	public void setReservationTables(List<ReservationTable> reservationTables) {
		this.reservationTables = reservationTables;
	}

	public ReservationTable addReservationTable(ReservationTable reservationTable) {
		getReservationTables().add(reservationTable);
		reservationTable.setReservation(this);

		return reservationTable;
	}

	public ReservationTable removeReservationTable(ReservationTable reservationTable) {
		getReservationTables().remove(reservationTable);
		reservationTable.setReservation(null);

		return reservationTable;
	}

}