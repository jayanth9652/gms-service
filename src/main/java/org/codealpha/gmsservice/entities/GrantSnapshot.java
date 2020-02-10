package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "grant_snapshot")
public class GrantSnapshot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;

  @Column
  private Long assignedToId;

  @Column
  private Long grantId;

  @Column
  private String grantee;

  @Column(columnDefinition = "text")
  private String stringAttributes;

  @Column(name = "name",columnDefinition = "text")
  private String name;

  @Column(name = "description",columnDefinition = "text")
  private String description;

  @Column
  private Double amount;

  @Column
  private Long grantStatusId;

  @Column
  private Date startDate;

  @Column
  private Date endDate;

  @Column
  private String representative;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAssignedToId() {
    return assignedToId;
  }

  public void setAssignedToId(Long assignedToId) {
    this.assignedToId = assignedToId;
  }

  public Long getGrantId() {
    return grantId;
  }

  public void setGrantId(Long grantId) {
    this.grantId = grantId;
  }

  public String getGrantee() {
    return grantee;
  }

  public void setGrantee(String grantee) {
    this.grantee = grantee;
  }

  public String getStringAttributes() {
    return stringAttributes;
  }

  public void setStringAttributes(String stringAttributes) {
    this.stringAttributes = stringAttributes;
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

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

    public Long getGrantStatusId() {
        return grantStatusId;
    }

    public void setGrantStatusId(Long grantStatusId) {
        this.grantStatusId = grantStatusId;
    }

    public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getRepresentative() {
    return representative;
  }

  public void setRepresentative(String representative) {
    this.representative = representative;
  }
}