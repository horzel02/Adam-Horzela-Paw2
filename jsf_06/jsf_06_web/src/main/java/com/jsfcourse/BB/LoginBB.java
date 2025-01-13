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

@Named
@SessionScoped
public class LoginBB implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private User loggedUser;
    private String loggedUserRole;

    @Inject
    private UserDAO userDAO;

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
        System.out.println("Pobieram rolę użytkownika: " + this.loggedUserRole);
        return this.loggedUserRole;
    }

    public User getLoggedUser() {
        if (this.loggedUser != null) {
            System.out.println("Pobrano zalogowanego użytkownika: " + loggedUser.getName() + " " + loggedUser.getSurname());
        } else {
            System.out.println("Brak zalogowanego użytkownika w getLoggedUser()");
        }
        return this.loggedUser;
    }

    public String doLogin() {
        FacesContext ctx = FacesContext.getCurrentInstance(); // Kontekst przeniesiony na początek metody

        try {
            System.out.println("Rozpoczęto logowanie: " + email);

            // Wyszukaj użytkownika na podstawie e-maila
            User user = userDAO.findByEmail(email);
            if (user == null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Niepoprawny e-mail lub hasło.", null));
                return null;
            }

            // Weryfikacja hasła
            if (!BCrypt.checkpw(password, user.getPassword())) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Niepoprawny e-mail lub hasło.", null));
                return null;
            }

            System.out.println("Zalogowano użytkownika: " + user.getEmail());

            // Tworzenie obiektu RemoteClient
            RemoteClient<User> client = new RemoteClient<>();
            client.setDetails(user);
            client.setLogin(user.getEmail());

            // Pobieranie ról użytkownika
            List<Role> roles = userDAO.getRolesByUserId(user.getId());
            if (!roles.isEmpty()) {
                this.loggedUserRole = roles.get(0).getRoleName();
                for (Role role : roles) {
                    client.getRoles().add(role.getRoleName());
                }
            } else {
                this.loggedUserRole = "Brak roli";
            }

            HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
            client.store(request);

            this.loggedUser = user; // Aktualizacja zalogowanego użytkownika w sesji
            return "/main.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas logowania.", null));
            return null;
        }
    }


    public String doLogout() {
        this.loggedUser = null;
        this.loggedUserRole = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public LoginBB() {
        System.out.println("LoginBB initialized");
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            try {
                HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
                RemoteClient<User> client = RemoteClient.load(request.getSession());
                if (client != null) {
                    this.loggedUser = client.getDetails();
                    if (loggedUser != null) {
                        System.out.println("Zalogowany użytkownik: " + loggedUser.getName() + " " + loggedUser.getSurname());

                        // Pobieranie ról użytkownika
                        if (!client.getRoles().isEmpty()) {
                            this.loggedUserRole = client.getRoles().iterator().next(); // Pobierz pierwszą rolę
                        } else {
                            this.loggedUserRole = "Brak roli";
                        }
                    } else {
                        System.out.println("Brak zalogowanego użytkownika.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Błąd w konstruktorze LoginBB: " + e.getMessage());
            }
        }
    }
}
