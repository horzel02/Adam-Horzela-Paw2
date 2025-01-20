package com.jsf.dao;

import java.util.List;
import java.util.Map;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.jsf.entities.User;
import com.jsf.entities.Role;

@Stateless
public class UserDAO {
    private static final String UNIT_NAME = "jsfcourse-simplePU";

    @PersistenceContext(unitName = UNIT_NAME)
    private EntityManager em;

    // --------------------
    // CRUD Operations
    // --------------------

    /**
     * Tworzy nowego użytkownika w bazie danych.
     */
    public void create(User user) {
        em.persist(user);
    }

    /**
     * Aktualizuje dane istniejącego użytkownika.
     */
    public User merge(User user) {
        return em.merge(user);
    }

    /**
     * Usuwa użytkownika z bazy danych.
     */
    public void remove(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }

    /**
     * Znajduje użytkownika na podstawie jego identyfikatora.
     */
    public User find(Object id) {
        return em.find(User.class, id);
    }

    // --------------------
    // Queries
    // --------------------

    /**
     * Pobiera pełną listę użytkowników.
     */
    public List<User> getFullList() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Pobiera listę użytkowników na podstawie parametrów wyszukiwania.
     * 
     * @param searchParams Map parametrów wyszukiwania (np. nazwisko).
     */
    public List<User> getList(Map<String, Object> searchParams) {
        String select = "SELECT u ";
        String from = "FROM User u ";
        String where = "";
        String orderby = "ORDER BY u.surname ASC, u.name";

        String surname = (String) searchParams.get("surname");
        if (surname != null) {
            where = "WHERE u.surname LIKE :surname ";
        }

        Query query = em.createQuery(select + from + where + orderby);

        if (surname != null) {
            query.setParameter("surname", surname + "%");
        }

        return query.getResultList();
    }
    
    /**
     * Dezaktywuje użytkownika (ustawia active na 0).
     */
    public void deactivateUser(int userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setActive((byte) 0);
            em.merge(user);
            em.flush();
        }
    }
    
    public void activateUser(int userId) {
        User user = em.find(User.class, userId);
        if (user != null) {
            user.setActive((byte) 1);
            em.merge(user);
            em.flush();
        }
    }


    // --------------------
    // Role Management
    // --------------------

    /**
     * Przypisuje domyślną rolę "Użytkownik" (ID 3) nowo zarejestrowanemu użytkownikowi.
     */
    public void assignDefaultRole(User user) {
        try {
            Query query = em.createNativeQuery("INSERT INTO user_roles (user_id, role_id, assignment_date) VALUES (:userId, :roleId, CURRENT_TIMESTAMP)");
            query.setParameter("userId", user.getId());
            query.setParameter("roleId", 3);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pobiera role przypisane do użytkownika na podstawie jego identyfikatora.
     */
    public List<Role> getRolesByUserId(int userId) {
        return em.createQuery(
                "SELECT ur.role FROM UserRole ur WHERE ur.userId = :userId", Role.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // --------------------
    // Utility Methods
    // --------------------

    /**
     * Sprawdza, czy adres e-mail jest już zarejestrowany w bazie danych.
     */
    public boolean emailExists(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();
        return count > 0;
    }

    /**
     * Znajduje użytkownika na podstawie jego adresu e-mail.
     */
    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
