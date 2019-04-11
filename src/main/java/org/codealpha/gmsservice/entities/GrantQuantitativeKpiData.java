package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.codealpha.gmsservice.constants.KPIStatus;

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
  @JsonIgnore
  @JoinColumn(referencedColumnName = "id")
  private GrantKpi grantKpi;
  @Column
  private String statusName;
  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private WorkflowStatus status;
  @Column
  private Date submitByDate;
  @Column
  private Date submittedOnDate;

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

  public Integer getActuals() {
    return actuals;
  }

  public void setActuals(Integer actuals) {
    this.actuals = actuals;
  }

  public GrantKpi getGrantKpi() {
    return grantKpi;
  }

  public void setGrantKpi(GrantKpi grantKpi) {
    this.grantKpi = grantKpi;
  }

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String status) {
    this.statusName = status;
  }

  public Date getSubmitByDate() {
    return submitByDate;
  }

  public void setSubmitByDate(Date submitByDate) {
    this.submitByDate = submitByDate;
  }

  public Date getSubmittedOnDate() {
    return submittedOnDate;
  }

  public void setSubmittedOnDate(Date submittedOnDate) {
    this.submittedOnDate = submittedOnDate;
  }


  public WorkflowStatus getStatus() {
    return status;
  }

  public void setStatus(WorkflowStatus status) {
    this.status = status;
  }
}
