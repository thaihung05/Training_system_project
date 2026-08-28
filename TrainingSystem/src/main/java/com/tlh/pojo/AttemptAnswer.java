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
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
@Entity
@Table(name = "attempt_answer")
@NamedQueries({
    @NamedQuery(name = "AttemptAnswer.findAll", query = "SELECT a FROM AttemptAnswer a"),
    @NamedQuery(name = "AttemptAnswer.findById", query = "SELECT a FROM AttemptAnswer a WHERE a.id = :id"),
    @NamedQuery(name = "AttemptAnswer.findByIsCorrect", query = "SELECT a FROM AttemptAnswer a WHERE a.isCorrect = :isCorrect")})
public class AttemptAnswer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_correct")
    private boolean isCorrect;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Question questionId;
    @JoinColumn(name = "selected_option_id", referencedColumnName = "id")
    @ManyToOne
    private QuestionOption selectedOptionId;
    @JoinColumn(name = "attempt_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private TestAttempt attemptId;

    public AttemptAnswer() {
    }

    public AttemptAnswer(Long id) {
        this.id = id;
    }

    public AttemptAnswer(Long id, boolean isCorrect) {
        this.id = id;
        this.isCorrect = isCorrect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Question getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Question questionId) {
        this.questionId = questionId;
    }

    public QuestionOption getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(QuestionOption selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public TestAttempt getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(TestAttempt attemptId) {
        this.attemptId = attemptId;
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
        if (!(object instanceof AttemptAnswer)) {
            return false;
        }
        AttemptAnswer other = (AttemptAnswer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.AttemptAnswer[ id=" + id + " ]";
    }
    
}
