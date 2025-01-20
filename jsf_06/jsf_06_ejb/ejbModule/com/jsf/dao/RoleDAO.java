package com.jsf.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class RoleDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * Przypisuje rolę użytkownikowi w tabeli user_roles.
     *
     * @param userId ID użytkownika
     * @param roleId ID roli
     */
    public void assignRoleToUser(int userId, int roleId) {
        em.createNativeQuery("INSERT INTO user_roles (user_id, role_id, assignment_date) VALUES (:userId, :roleId, CURRENT_TIMESTAMP)")
          .setParameter("userId", userId)
          .setParameter("roleId", roleId)
          .executeUpdate();
    }

    /**
     * Pobiera nazwę roli dla użytkownika.
     *
     * @param userId ID użytkownika
     * @return Nazwa roli
     */
    public String getRoleNameByUserId(int userId) {
        try {
            return em.createQuery(
                    "SELECT r.roleName FROM Role r JOIN UserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId", 
                    String.class)
                     .setParameter("userId", userId)
                     .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
