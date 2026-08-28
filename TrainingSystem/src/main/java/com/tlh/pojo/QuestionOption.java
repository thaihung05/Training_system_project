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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "question_option")
@NamedQueries({
    @NamedQuery(name = "QuestionOption.findAll", query = "SELECT q FROM QuestionOption q"),
    @NamedQuery(name = "QuestionOption.findById", query = "SELECT q FROM QuestionOption q WHERE q.id = :id"),
    @NamedQuery(name = "QuestionOption.findByOptionText", query = "SELECT q FROM QuestionOption q WHERE q.optionText = :optionText"),
    @NamedQuery(name = "QuestionOption.findByIsCorrect", query = "SELECT q FROM QuestionOption q WHERE q.isCorrect = :isCorrect"),
    @NamedQuery(name = "QuestionOption.findByOrderIndex", query = "SELECT q FROM QuestionOption q WHERE q.orderIndex = :orderIndex")})
public class QuestionOption implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "option_text")
    private String optionText;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_correct")
    private boolean isCorrect;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_index")
    private int orderIndex;
    @JsonIgnore
    @OneToMany(mappedBy = "selectedOptionId")
    private List<AttemptAnswer> attemptAnswerList;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Question questionId;

    public QuestionOption() {
    }

    public QuestionOption(Long id) {
        this.id = id;
    }

    public QuestionOption(Long id, String optionText, boolean isCorrect, int orderIndex) {
        this.id = id;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<AttemptAnswer> getAttemptAnswerList() {
        return attemptAnswerList;
    }

    public void setAttemptAnswerList(List<AttemptAnswer> attemptAnswerList) {
        this.attemptAnswerList = attemptAnswerList;
    }

    public Question getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Question questionId) {
        this.questionId = questionId;
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
        if (!(object instanceof QuestionOption)) {
            return false;
        }
        QuestionOption other = (QuestionOption) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tlh.pojo.QuestionOption[ id=" + id + " ]";
    }
    
}
