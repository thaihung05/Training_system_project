/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "point_transaction")
@NamedQueries({
    @NamedQuery(name = "PointTransaction.findAll", query = "SELECT p FROM PointTransaction p"),
    @NamedQuery(name = "PointTransaction.findById", query = "SELECT p FROM PointTransaction p WHERE p.id = :id"),
    @NamedQuery(name = "PointTransaction.findByPoints", query = "SELECT p FROM PointTransaction p WHERE p.points = :points"),
    @NamedQuery(name = "PointTransaction.findByReason", query = "SELECT p FROM PointTransaction p WHERE p.reason = :reason"),
    @NamedQuery(name = "PointTransaction.findByCreatedAt", query = "SELECT p FROM PointTransaction p WHERE p.createdAt = :createdAt")})
public class PointTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "points")
    private int points;
    @Size(max = 255)
    @Column(name = "reason")
    private String reason;
    @Column(name = "created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "rule_id", referencedColumnName = "id")
    @ManyToOne
    private PointRule ruleId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public PointTransaction() {
    }

    public PointTransaction(Long id) {
        this.id = id;
    }

    public PointTransaction(Long id, int points) {
        this.id = id;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public PointRule getRuleId() {
        return ruleId;
    }

    public void setRuleId(PointRule ruleId) {
        this.ruleId = ruleId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof PointTransaction)) {
            return false;
        }
        PointTransaction other = (PointTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.PointTransaction[ id=" + id + " ]";
    }
    
}
