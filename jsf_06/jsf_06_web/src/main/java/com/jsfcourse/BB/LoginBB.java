package com.jsfcourse.BB;

import java.io.Serializable;
import java.util.List;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.mindrot.jbcrypt.BCrypt;

import com.jsf.dao.UserDAO;
import com.jsf.entities.User;
import com.jsf.entities.Role;
import jakarta.faces.simplesecurity.RemoteClient;

/**
 * Managed Bean obsługujący proces logowania i wylogowania użytkownika.
 */
@Named
@SessionScoped
public class LoginBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private User loggedUser;
    private String loggedUserRole;

    @Inject
    private UserDAO userDAO; // DAO obsługujące operacje na użytkownikach

    // Gettery i settery dla pól email i password
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoggedUserRole() {
        return this.loggedUserRole;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }

    /**
     * Metoda obsługująca proces logowania użytkownika.
     * @return przekierowanie do strony głównej po zalogowaniu lub null w przypadku błędu.
     */
    public String doLogin() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        try {
            // Wyszukaj użytkownika na podstawie e-maila
            User user = userDAO.findByEmail(email);
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Niepoprawny e-mail lub hasło.", null));
                return null;
            }
            
            // Sprawdź, czy konto jest aktywne
            if (user.getActive() == 0) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Twoje konto jest nieaktywne. Skontaktuj się z administratorem.", null));
                return null;
            }

            // Tworzenie obiektu RemoteClient i przypisywanie ról
            RemoteClient<User> client = new RemoteClient<>();
            client.setDetails(user);
            client.setLogin(user.getEmail());

            List<Role> roles = userDAO.getRolesByUserId(user.getId());
            if (!roles.isEmpty()) {
                this.loggedUserRole = roles.get(0).getRoleName();
                for (Role role : roles) {
                    client.getRoles().add(role.getRoleName());
                }
            } else {
                this.loggedUserRole = "Brak roli";
            }

            // Przechowywanie informacji o zalogowanym użytkowniku w sesji
            HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
            client.store(request);

            this.loggedUser = user;
            return "/main.xhtml?faces-redirect=true";

        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas logowania.", null));
            return null;
        }
    }

    /**
     * Metoda obsługująca proces wylogowania użytkownika.
     * @return przekierowanie do strony logowania.
     */
    public String doLogout() {
        this.loggedUser = null;
        this.loggedUserRole = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    /**
     * Konstruktor inicjalizujący zalogowanego użytkownika na podstawie sesji.
     */
    public LoginBB() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            try {
                HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
                RemoteClient<User> client = RemoteClient.load(request.getSession());
                if (client != null) {
                    this.loggedUser = client.getDetails();
                    this.loggedUserRole = client.getRoles().isEmpty() ? "Brak roli" : client.getRoles().iterator().next();
                }
            } catch (Exception e) {
            }
        }
    }
}
