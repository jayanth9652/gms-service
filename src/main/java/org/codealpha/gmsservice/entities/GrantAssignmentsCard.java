package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "grant_assignments")
public class GrantAssignmentsCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    @JsonIgnore
    private GrantCard grant;

    @Column
    private Long stateId;

    @Column
    private Long assignments;

    @Column
    private boolean anchor = false;

    @Transient
    private List<GrantAssignmentHistory> history;

    @Column
    private Date assignedOn;

    @Column
    private Long updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrantCard getGrant() {
        return grant;
    }

    public void setGrant(GrantCard grant) {
        this.grant = grant;
    }

    public Long getAssignments() {
        return assignments;
    }

    public void setAssignments(Long assignments) {
        this.assignments = assignments;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public boolean isAnchor() {
        return anchor;
    }

    public void setAnchor(boolean anchor) {
        this.anchor = anchor;
    }

    public List<GrantAssignmentHistory> getHistory() {
        return history;
    }

    public void setHistory(List<GrantAssignmentHistory> history) {
        this.history = history;
    }

    public Date getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(Date assignedOn) {
        this.assignedOn = assignedOn;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }



}
