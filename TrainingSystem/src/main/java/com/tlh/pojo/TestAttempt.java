/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "test_attempt")
@NamedQueries({
    @NamedQuery(name = "TestAttempt.findAll", query = "SELECT t FROM TestAttempt t"),
    @NamedQuery(name = "TestAttempt.findById", query = "SELECT t FROM TestAttempt t WHERE t.id = :id"),
    @NamedQuery(name = "TestAttempt.findByAttemptNo", query = "SELECT t FROM TestAttempt t WHERE t.attemptNo = :attemptNo"),
    @NamedQuery(name = "TestAttempt.findByScore", query = "SELECT t FROM TestAttempt t WHERE t.score = :score"),
    @NamedQuery(name = "TestAttempt.findByPassed", query = "SELECT t FROM TestAttempt t WHERE t.passed = :passed"),
    @NamedQuery(name = "TestAttempt.findByStartedAt", query = "SELECT t FROM TestAttempt t WHERE t.startedAt = :startedAt"),
    @NamedQuery(name = "TestAttempt.findBySubmittedAt", query = "SELECT t FROM TestAttempt t WHERE t.submittedAt = :submittedAt")})
public class TestAttempt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attempt_no")
    private int attemptNo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "score")
    private int score;
    @Basic(optional = false)
    @NotNull
    @Column(name = "passed")
    private boolean passed;
    @Column(name = "started_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedAt;
    @Column(name = "submitted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submittedAt;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "attemptId")
    private List<AttemptAnswer> attemptAnswerList;
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Test testId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public TestAttempt() {
    }

    public TestAttempt(Long id) {
        this.id = id;
    }

    public TestAttempt(Long id, int attemptNo, int score, boolean passed) {
        this.id = id;
        this.attemptNo = attemptNo;
        this.score = score;
        this.passed = passed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        this.attemptNo = attemptNo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<AttemptAnswer> getAttemptAnswerList() {
        return attemptAnswerList;
    }

    public void setAttemptAnswerList(List<AttemptAnswer> attemptAnswerList) {
        this.attemptAnswerList = attemptAnswerList;
    }

    public Test getTestId() {
        return testId;
    }

    public void setTestId(Test testId) {
        this.testId = testId;
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
        if (!(object instanceof TestAttempt)) {
            return false;
        }
        TestAttempt other = (TestAttempt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.TestAttempt[ id=" + id + " ]";
    }
    
}
