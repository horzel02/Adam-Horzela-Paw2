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

public class LazyReservationDataModel extends LazyDataModel<Reservation> {
	private static final long serialVersionUID = 1L;

	private final ReservationDAO reservationDAO;
	private final int userId; // ID zalogowanego użytkownika
	private List<Reservation> datasource;

	public LazyReservationDataModel(ReservationDAO reservationDAO, int userId) {
		this.reservationDAO = reservationDAO;
		this.userId = userId;
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
		// Pobierz rezerwacje dla zalogowanego użytkownika
		List<Reservation> allReservations = reservationDAO.getReservationsByUserId(userId);

		// Filtrowanie
		datasource = allReservations.stream().filter(reservation -> filters.entrySet().stream().allMatch(entry -> {
			String filterValue = (String) entry.getValue().getFilterValue();
			if (filterValue == null || filterValue.isEmpty()) {
				return true;
			}
			switch (entry.getKey()) {
			case "id":
				return reservation.getId().toString().contains(filterValue);
			case "date":
				return reservation.getDate().toString().contains(filterValue);
			case "status":
				return reservation.getStatus().contains(filterValue);
			case "tableName":  // nowy przypadek filtrowania
                return !reservation.getReservationTables().isEmpty()
                    && reservation.getReservationTables().get(0).getTable() != null
                    && reservation.getReservationTables().get(0).getTable().getName() != null
                    && reservation.getReservationTables().get(0).getTable().getName().toLowerCase().contains(filterValue.toLowerCase());
			default:
				return true;
			}
		})).collect(Collectors.toList());

		// Sortowanie
		if (sortBy != null && !sortBy.isEmpty()) {
			Map.Entry<String, SortMeta> sortEntry = sortBy.entrySet().iterator().next();
			datasource.sort((r1, r2) -> {
				String sortField = sortEntry.getKey();
				SortOrder sortOrder = sortEntry.getValue().getOrder();
				int result = 0;
				switch (sortField) {
				case "id":
					result = r1.getId().compareTo(r2.getId());
					break;
				case "date":
					result = r1.getDate().compareTo(r2.getDate());
					break;
				case "status":
					result = r1.getStatus().compareTo(r2.getStatus());
					break;
				case "tableName":  // nowy przypadek sortowania
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

				}
				return sortOrder == SortOrder.ASCENDING ? result : -result;
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
	public int count(Map<String, FilterMeta> filterBy) {
		// TODO Auto-generated method stub
		return 0;
	}
}
