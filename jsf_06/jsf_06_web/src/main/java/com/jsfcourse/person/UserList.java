package com.jsfcourse.person;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.UserDAO;
import com.jsf.entities.User;

@Named
@RequestScoped
public class UserList implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PAGE_USER_EDIT = "userEdit?faces-redirect=true";

    private String surnameFilter;

    @Inject
    private Flash flash;

    @EJB
    private UserDAO userDAO;

    private List<User> users;

    public String getSurnameFilter() {
        return surnameFilter;
    }

    public void setSurnameFilter(String surnameFilter) {
        this.surnameFilter = surnameFilter;
    }

    public List<User> getUsers() {
        if (users == null) {
            users = userDAO.getList(new HashMap<>());
        }
        return users;
    }


    public String newUser() {
        User user = new User();
        flash.put("user", user);
        return PAGE_USER_EDIT;
    }

    public String editUser(User user) {
        flash.put("user", user);
        return PAGE_USER_EDIT;
    }

    public String deleteUser(User user) {
        try {
            userDAO.deleteUserWithRoles(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        users = null;
        return null;
    }
    
    public void filter() {
        Map<String, Object> searchParams = new HashMap<>();
        if (surnameFilter != null && !surnameFilter.isEmpty()) {
            searchParams.put("surname", surnameFilter); // Dodaj filtr nazwiska
        }
        users = userDAO.getList(searchParams); // Odśwież listę użytkowników
    }

}
