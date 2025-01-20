package com.jsfcourse.BB.lazy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.jsf.dao.ReservationDAO;
import com.jsf.entities.Reservation;

public class LazyEmployeeReservationDataModel extends LazyDataModel<Reservation> {
	private static final long serialVersionUID = 1L;
	private List<Reservation> datasource;
	private ReservationDAO reservationDAO;

	public LazyEmployeeReservationDataModel(ReservationDAO reservationDAO) {
		this.reservationDAO = reservationDAO;
	}

	@Override
	public Reservation getRowData(String rowKey) {
		Integer id = Integer.valueOf(rowKey);
		if (datasource != null) {
			for (Reservation res : datasource) {
				if (res.getId().equals(id)) {
					return res;
				}
			}
		}
		return null;
	}

	@Override
	public String getRowKey(Reservation reservation) {
		return reservation.getId().toString();
	}

	@Override
	public List<Reservation> load(int first, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filters) {
		// Pobierz wszystkie rezerwacje
		List<Reservation> allReservations = reservationDAO.getAllReservations();

		// Filtrowanie
		datasource = allReservations.stream().filter(reservation -> filters.entrySet().stream().allMatch(entry -> {
			String filterValue = entry.getValue().getFilterValue() == null ? ""
					: entry.getValue().getFilterValue().toString();
			if (filterValue.isEmpty()) {
				return true;
			}
			switch (entry.getKey()) {
			case "id":
				return reservation.getId().toString().contains(filterValue);
			case "fullName":
		        String fullName = (reservation.getUser() != null) 
		                          ? reservation.getUser().getName() + " " + reservation.getUser().getSurname() 
		                          : "";
		        return fullName.toLowerCase().contains(filterValue.toLowerCase());
			case "date":
				return reservation.getDate() != null && reservation.getDate().toString().contains(filterValue);
			case "time":
				return reservation.getTime() != null && reservation.getTime().toString().contains(filterValue);
			case "tableName":
		        return !reservation.getReservationTables().isEmpty()
		            && reservation.getReservationTables().get(0).getTable() != null
		            && reservation.getReservationTables().get(0).getTable().getName() != null
		            && reservation.getReservationTables().get(0).getTable().getName().toLowerCase()
		                  .contains(filterValue.toLowerCase());
			case "numberOfPeople":
				return reservation.getNumberOfPeople() != null
						&& reservation.getNumberOfPeople().toString().contains(filterValue);
			case "status":
				return reservation.getStatus() != null && reservation.getStatus().equalsIgnoreCase(filterValue);
			default:
				return true;
			}
		})).collect(Collectors.toList());

		// Sortowanie
		if (sortBy != null && !sortBy.isEmpty()) {
			datasource.sort((r1, r2) -> {
				int result = 0;
				for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
					String sortField = entry.getKey();
					SortOrder sortOrder = entry.getValue().getOrder();

					switch (sortField) {
					case "id":
						result = r1.getId().compareTo(r2.getId());
						break;
					case "fullName":
				        String fullName1 = (r1.getUser() != null) 
				                           ? r1.getUser().getName() + " " + r1.getUser().getSurname() 
				                           : "";
				        String fullName2 = (r2.getUser() != null) 
				                           ? r2.getUser().getName() + " " + r2.getUser().getSurname() 
				                           : "";
				        result = fullName1.compareTo(fullName2);
				        break;
					case "date":
						result = r1.getDate().compareTo(r2.getDate());
						break;
					case "time":
						result = r1.getTime().compareTo(r2.getTime());
						break;
					case "tableName":
				        String name1 = (!r1.getReservationTables().isEmpty() && 
				                         r1.getReservationTables().get(0).getTable() != null &&
				                         r1.getReservationTables().get(0).getTable().getName() != null)
				                       ? r1.getReservationTables().get(0).getTable().getName() 
				                       : "";
				        String name2 = (!r2.getReservationTables().isEmpty() && 
				                         r2.getReservationTables().get(0).getTable() != null &&
				                         r2.getReservationTables().get(0).getTable().getName() != null)
				                       ? r2.getReservationTables().get(0).getTable().getName() 
				                       : "";
				        result = name1.compareTo(name2);
				        break;
					case "numberOfPeople":
						result = r1.getNumberOfPeople().compareTo(r2.getNumberOfPeople());
						break;
					case "status":
						result = r1.getStatus().compareTo(r2.getStatus());
						break;
					}

					if (sortOrder == SortOrder.DESCENDING) {
						result = -result;
					}
					if (result != 0) {
						break; // Przerwij, jeśli pierwsze sortowanie jest rozstrzygające
					}
				}
				return result;
			});
		}

		// Stronicowanie
		int dataSize = datasource.size();
		this.setRowCount(dataSize);

		if (dataSize > pageSize) {
			try {
				return datasource.subList(first, Math.min(first + pageSize, dataSize));
			} catch (IndexOutOfBoundsException e) {
				return datasource.subList(first, dataSize);
			}
		} else {
			return datasource;
		}
	}

	@Override
	public int count(Map<String, FilterMeta> filters) {
		// Oblicz liczbę rekordów po zastosowaniu filtrów
		return (int) reservationDAO.getAllReservations().stream()
				.filter(reservation -> filters.entrySet().stream().allMatch(entry -> {
					String filterValue = entry.getValue().getFilterValue() == null ? ""
							: entry.getValue().getFilterValue().toString();
					if (filterValue.isEmpty()) {
						return true;
					}
					switch (entry.getKey()) {
					case "id":
						return reservation.getId().toString().contains(filterValue);
					case "fullName":
				        String fullName = (reservation.getUser() != null) 
				                          ? reservation.getUser().getName() + " " + reservation.getUser().getSurname() 
				                          : "";
				        return fullName.toLowerCase().contains(filterValue.toLowerCase());
					case "date":
						return reservation.getDate() != null && reservation.getDate().toString().contains(filterValue);
					case "time":
						return reservation.getTime() != null && reservation.getTime().toString().contains(filterValue);
					case "tableName":
				        return !reservation.getReservationTables().isEmpty()
				            && reservation.getReservationTables().get(0).getTable() != null
				            && reservation.getReservationTables().get(0).getTable().getName() != null
				            && reservation.getReservationTables().get(0).getTable().getName().toLowerCase()
				                  .contains(filterValue.toLowerCase());
					case "numberOfPeople":
						return reservation.getNumberOfPeople() != null
								&& reservation.getNumberOfPeople().toString().contains(filterValue);
					case "status":
						return reservation.getStatus() != null && reservation.getStatus().contains(filterValue);
					default:
						return true;
					}
				})).count();
	}
}
