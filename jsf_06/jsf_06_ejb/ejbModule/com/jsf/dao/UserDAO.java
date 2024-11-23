package com.jsf.dao;

import java.util.List;
import java.util.Map;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.jsf.entities.User;

@Stateless
public class UserDAO {
    private static final String UNIT_NAME = "jsfcourse-simplePU";

    @PersistenceContext(unitName = UNIT_NAME)
    private EntityManager em;

    public void create(User user) {
        em.persist(user);
    }

    public User merge(User user) {
        return em.merge(user);
    }

    public void remove(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }

    public User find(Object id) {
        return em.find(User.class, id);
    }

    public List<User> getFullList() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

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

        System.out.println("Zapytanie: " + query.toString()); // Debug
        return query.getResultList();
    }

    public void deleteUserWithRoles(User user) {
        em.createNativeQuery("DELETE FROM user_roles WHERE user_id = :userId")
                .setParameter("userId", user.getId())
                .executeUpdate();
        remove(user);
    }
    
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

    public boolean emailExists(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();
        return count > 0;
    }

    
}
