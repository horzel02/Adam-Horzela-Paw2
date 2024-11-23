package com.jsf.entities;

import java.io.Serializable;
import jakarta.persistence.*;
import java.sql.Timestamp;
import jakarta.persistence.Table;


/**
 * The persistent class for the user_roles database table.
 * 
 */
@Entity
@Table(name="user_roles")
@NamedQuery(name="UserRole.findAll", query="SELECT u FROM UserRole u")
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(name="assignment_date")
	private Timestamp assignmentDate;

	@Column(name="removal_date")
	private Timestamp removalDate;

	@Column(name="user_id")
	private Integer userId;

	//bi-directional many-to-one association to Role
	@ManyToOne
	private Role role;

	public UserRole() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getAssignmentDate() {
		return this.assignmentDate;
	}

	public void setAssignmentDate(Timestamp assignmentDate) {
		this.assignmentDate = assignmentDate;
	}

	public Timestamp getRemovalDate() {
		return this.removalDate;
	}

	public void setRemovalDate(Timestamp removalDate) {
		this.removalDate = removalDate;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}