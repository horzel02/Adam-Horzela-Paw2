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

@Named
@RequestScoped
public class RegistrationBB {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

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

    public String register() {
    	System.out.println("Metoda Register została wywołana.");
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (!password.equals(confirmPassword)) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hasła nie są zgodne.", null));
            return null;
        }

        if (userDAO.emailExists(email)) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Podany e-mail już istnieje.", null));
            return null;
        }

        try {
            User user = new User();
            user.setName(firstName);
            user.setSurname(lastName);
            user.setEmail(email);
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            user.setPassword(hashedPassword);
            
            user.setActive((byte) 1);
            
            System.out.println("Tworzę użytkownika: " + user);

            userDAO.create(user);

            // Przypisanie domyślnej roli (Użytkownik)
            roleDAO.assignRoleToUser(user.getId(), 3);
            System.out.println("Przypisano rolę użytkownikowi: " + user.getId());

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Rejestracja zakończona sukcesem!", null));
            return "/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wystąpił błąd podczas rejestracji.", null));
            return null;
        }
    }
}
