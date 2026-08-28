/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "point_rule")
@NamedQueries({
    @NamedQuery(name = "PointRule.findAll", query = "SELECT p FROM PointRule p"),
    @NamedQuery(name = "PointRule.findById", query = "SELECT p FROM PointRule p WHERE p.id = :id"),
    @NamedQuery(name = "PointRule.findByActionType", query = "SELECT p FROM PointRule p WHERE p.actionType = :actionType"),
    @NamedQuery(name = "PointRule.findByPoints", query = "SELECT p FROM PointRule p WHERE p.points = :points"),
    @NamedQuery(name = "PointRule.findByDescription", query = "SELECT p FROM PointRule p WHERE p.description = :description")})
public class PointRule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "action_type")
    private String actionType;
    @Basic(optional = false)
    @NotNull
    @Column(name = "points")
    private int points;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @JsonIgnore
    @OneToMany(mappedBy = "ruleId")
    private List<PointTransaction> pointTransactionList;

    public PointRule() {
    }

    public PointRule(Long id) {
        this.id = id;
    }

    public PointRule(Long id, String actionType, int points) {
        this.id = id;
        this.actionType = actionType;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PointTransaction> getPointTransactionList() {
        return pointTransactionList;
    }

    public void setPointTransactionList(List<PointTransaction> pointTransactionList) {
        this.pointTransactionList = pointTransactionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PointRule)) {
            return false;
        }
        PointRule other = (PointRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.PointRule[ id=" + id + " ]";
    }
    
}
