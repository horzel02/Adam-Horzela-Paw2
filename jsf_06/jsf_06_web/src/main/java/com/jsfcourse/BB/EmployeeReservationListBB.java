package com.jsfcourse.BB;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.ReservationDAO;
import com.jsf.entities.Reservation;
import com.jsf.entities.Tables;
import com.jsfcourse.BB.lazy.LazyEmployeeReservationDataModel;


import org.primefaces.model.LazyDataModel;

@Named
@ViewScoped
public class EmployeeReservationListBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations;
    private List<Reservation> filteredReservations;
    private List<String> statusList;
    private String filterStatus;
    private String filterTableName;
    private List<Tables> tableList;
    
    @Inject
    private ReservationDAO reservationDAO;
    
    private LazyDataModel<Reservation> lazyModel;

    @PostConstruct
    public void init() {
        reservations = reservationDAO.getAllReservations();
        statusList = Arrays.asList("Oczekujące", "Potwierdzone", "Zrealizowane", "Anulowane");
        lazyModel = new LazyEmployeeReservationDataModel(reservationDAO);
        tableList = reservationDAO.getAllTables();
    }
    
    public void updateReservationStatus(Reservation reservation) {
        try {
            reservationDAO.updateReservation(reservation);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Status został zaktualizowany!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas aktualizacji statusu!", null));
        }
    }
    
    public LazyDataModel<Reservation> getLazyModel() {
        return lazyModel;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Reservation> getFilteredReservations() {
        return filteredReservations;
    }

    public void setFilteredReservations(List<Reservation> filteredReservations) {
        this.filteredReservations = filteredReservations;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }
    
    public String getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }
    
    public String getFilterTableName() {
        return filterTableName;
    }

    public void setFilterTableName(String filterTableName) {
        this.filterTableName = filterTableName;
    }

    public List<Tables> getTableList() {
        return tableList;
    }

    public void setTableList(List<Tables> tableList) {
        this.tableList = tableList;
    }
}
