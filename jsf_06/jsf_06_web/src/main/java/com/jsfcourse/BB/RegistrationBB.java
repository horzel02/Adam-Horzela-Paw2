package com.jsfcourse.BB;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.mindrot.jbcrypt.BCrypt;

import com.jsf.dao.UserDAO;
import com.jsf.dao.RoleDAO;
import com.jsf.entities.User;

/**
 * Managed Bean obsługujący proces rejestracji użytkownika.
 */
@Named
@RequestScoped
public class RegistrationBB {

    // Pola do przechowywania danych wejściowych użytkownika
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    // Gettery i settery
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Obsługuje proces rejestracji nowego użytkownika.
     * @return przekierowanie na stronę logowania w przypadku sukcesu lub null w przypadku błędu
     */
    public String register() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        // Sprawdzenie zgodności haseł
        if (!password.equals(confirmPassword)) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hasła nie są zgodne.", null));
            return null;
        }

        // Sprawdzenie, czy adres e-mail już istnieje w bazie
        if (userDAO.emailExists(email)) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Podany e-mail już istnieje.", null));
            return null;
        }

        try {
            // Tworzenie nowego użytkownika
            User user = new User();
            user.setName(firstName);
            user.setSurname(lastName);
            user.setEmail(email);

            // Hashowanie hasła przed zapisaniem
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            user.setPassword(hashedPassword);

            user.setActive((byte) 1);

            // Zapis użytkownika w bazie danych
            userDAO.create(user);

            // Przypisanie domyślnej roli użytkownika
            roleDAO.assignRoleToUser(user.getId(), 3);

            // Dodanie wiadomości o sukcesie
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Rejestracja zakończona sukcesem!", null));

            // Przekierowanie na stronę logowania
            return "/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            // Obsługa błędów podczas rejestracji
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas rejestracji.", null));
            return null;
        }
    }
}
