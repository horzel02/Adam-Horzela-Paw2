package com.jsfcourse.BB.lazy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.jsf.dao.UserDAO;
import com.jsf.entities.User;

public class LazyUserDataModel extends LazyDataModel<User> {
    private static final long serialVersionUID = 1L;
    private List<User> datasource;
    private UserDAO userDAO;

    public LazyUserDataModel(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User getRowData(String rowKey) {
        Integer id = Integer.valueOf(rowKey);
        if (datasource != null) {
            for (User user : datasource) {
                if (user.getId().equals(id)) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public String getRowKey(User user) {
        return user.getId().toString();
    }

    @Override
    public List<User> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filters) {
        List<User> allUsers = userDAO.getFullList();

        // Filtrowanie
        datasource = allUsers.stream()
            .filter(user -> filters.entrySet().stream().allMatch(entry -> {
                String filterValue = (String) entry.getValue().getFilterValue();
                if (filterValue == null || filterValue.isEmpty()) {
                    return true;
                }
                switch (entry.getKey()) {
                    case "id": // Filtrowanie po ID
                        return user.getId().toString().contains(filterValue);
                    case "name":
                        return user.getName().toLowerCase().contains(filterValue.toLowerCase());
                    case "surname":
                        return user.getSurname().toLowerCase().contains(filterValue.toLowerCase());
                    case "email":
                        return user.getEmail().toLowerCase().contains(filterValue.toLowerCase());
                    default:
                        return true;
                }
            }))
            .collect(Collectors.toList());

        // Sortowanie
        if (sortBy != null && !sortBy.isEmpty()) {
            Map.Entry<String, SortMeta> sortEntry = sortBy.entrySet().iterator().next();
            datasource.sort((u1, u2) -> {
                String sortField = sortEntry.getKey();
                SortOrder sortOrder = sortEntry.getValue().getOrder();
                int result = 0;
                switch (sortField) {
                    case "id": // Sortowanie po ID
                        result = u1.getId().compareTo(u2.getId());
                        break;
                    case "name":
                        result = u1.getName().compareTo(u2.getName());
                        break;
                    case "surname":
                        result = u1.getSurname().compareTo(u2.getSurname());
                        break;
                    case "email":
                        result = u1.getEmail().compareTo(u2.getEmail());
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
