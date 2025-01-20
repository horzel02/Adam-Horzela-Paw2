package com.jsfcourse.BB;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.primefaces.model.LazyDataModel;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.UserDAO;
import com.jsf.entities.User;
import com.jsfcourse.BB.lazy.LazyUserDataModel;

@Named
@RequestScoped
public class UserListBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PAGE_USER_EDIT = "userEdit?faces-redirect=true";

    private String surnameFilter;
    
    private LazyDataModel<User> lazyModel;
    private List<String> statusList;

    @Inject
    private Flash flash;

    @EJB
    private UserDAO userDAO;

    private List<User> users;

    public String getSurnameFilter() {
        return surnameFilter;
    }
    
    @PostConstruct
    public void init() {
        lazyModel = new LazyUserDataModel(userDAO);
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
    
    public void toggleUserActivation(User user) {
        if (user.getActive() == 1) {
            userDAO.deactivateUser(user.getId());
            System.out.println("Użytkownik zdezaktywowany: " + user.getId());
        } else {
            userDAO.activateUser(user.getId());
            System.out.println("Użytkownik aktywowany: " + user.getId());
        }
        users = null;
    }
    
    public LazyDataModel<User> getLazyModel() {
        return lazyModel;
    }

    public List<String> getStatusList() {
        return statusList;
    }

}
