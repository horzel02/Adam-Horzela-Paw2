package com.jsfcourse.person;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.jsf.dao.UserDAO;
import com.jsf.entities.User;

@Named
@ViewScoped
public class UserEdit implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PAGE_USER_LIST = "userList?faces-redirect=true";
    private static final String PAGE_STAY_AT_THE_SAME = null;


    private User user;
    private User loaded;

    @EJB
    private UserDAO userDAO;

    @Inject
    private Flash flash;

    @Inject
    private FacesContext context;

    public User getUser() {
        return user;
    }

    public void onLoad() {
        if (flash.containsKey("user")) {
            user = (User) flash.get("user");
            System.out.println("Załadowano użytkownika: " + (user.getId() == null ? "nowy użytkownik" : user.getId()));
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd: brak użytkownika", null));
        }
    }


    public String saveUser() {
        if (user == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Brak danych użytkownika", null));
            return PAGE_STAY_AT_THE_SAME;
        }

        // Sprawdzenie unikalności e-maila przy dodawaniu nowego użytkownika
        if (user.getId() == null && userDAO.emailExists(user.getEmail())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail już istnieje w systemie. Wprowadź inny adres.", null));
            return PAGE_STAY_AT_THE_SAME;
        }

        try {
            if (user.getId() == null) {
                userDAO.create(user);
                userDAO.assignDefaultRole(user);
            } else {
                userDAO.merge(user);
            }
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dane użytkownika zostały zapisane.", null));
        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas zapisu.", null));
            return PAGE_STAY_AT_THE_SAME;
        }

        return PAGE_USER_LIST;
    }



}
