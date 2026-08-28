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
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "user_badge")
@NamedQueries({
    @NamedQuery(name = "UserBadge.findAll", query = "SELECT u FROM UserBadge u"),
    @NamedQuery(name = "UserBadge.findById", query = "SELECT u FROM UserBadge u WHERE u.id = :id"),
    @NamedQuery(name = "UserBadge.findByAwardedAt", query = "SELECT u FROM UserBadge u WHERE u.awardedAt = :awardedAt")})
public class UserBadge implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "awarded_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date awardedAt;
    @JoinColumn(name = "badge_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Badge badgeId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public UserBadge() {
    }

    public UserBadge(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(Date awardedAt) {
        this.awardedAt = awardedAt;
    }

    public Badge getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(Badge badgeId) {
        this.badgeId = badgeId;
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
        if (!(object instanceof UserBadge)) {
            return false;
        }
        UserBadge other = (UserBadge) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.UserBadge[ id=" + id + " ]";
    }
    
}
