package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class GrantQuantitativeKpiData extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private Integer goal;
  @Column(nullable = true)
  private Integer actuals;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private Submission submission;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  private GrantKpi grantKpi;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public int getGoal() {
    return goal;
  }

  public void setGoal(int goal) {
    this.goal = goal;
  }

  public void setGoal(Integer goal) {
    this.goal = goal;
  }

  public Submission getSubmission() {
    return submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public Integer getActuals() {
    return actuals;
  }

  public void setActuals(Integer actuals) {
    this.actuals = actuals;
  }
}
