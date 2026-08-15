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
@Table(name = "badge")
@NamedQueries({
    @NamedQuery(name = "Badge.findAll", query = "SELECT b FROM Badge b"),
    @NamedQuery(name = "Badge.findById", query = "SELECT b FROM Badge b WHERE b.id = :id"),
    @NamedQuery(name = "Badge.findByCode", query = "SELECT b FROM Badge b WHERE b.code = :code"),
    @NamedQuery(name = "Badge.findByName", query = "SELECT b FROM Badge b WHERE b.name = :name"),
    @NamedQuery(name = "Badge.findByDescription", query = "SELECT b FROM Badge b WHERE b.description = :description"),
    @NamedQuery(name = "Badge.findByIconUrl", query = "SELECT b FROM Badge b WHERE b.iconUrl = :iconUrl")})
public class Badge implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 500)
    @Column(name = "icon_url")
    private String iconUrl;
    @JsonIgnore
    @OneToMany(mappedBy = "badgeId")
    private List<UserBadge> userBadgeList;

    public Badge() {
    }

    public Badge(Long id) {
        this.id = id;
    }

    public Badge(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<UserBadge> getUserBadgeList() {
        return userBadgeList;
    }

    public void setUserBadgeList(List<UserBadge> userBadgeList) {
        this.userBadgeList = userBadgeList;
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
        if (!(object instanceof Badge)) {
            return false;
        }
        Badge other = (Badge) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.Badge[ id=" + id + " ]";
    }
    
}
