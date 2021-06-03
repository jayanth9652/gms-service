package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.models.GrantDetailVO;
import org.codealpha.gmsservice.models.TableData;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Developer code-alpha.org
 **/
@Entity
@Table(name = "grants")
@ApiModel(value = "Grant Model", description = "Data model of a Grant")
@SqlResultSetMapping(name="GRANTSLIST",
        entities={
                @EntityResult(entityClass= GrantCard.class
                        /*fields={
                                @FieldResult(name="id",column="id"),
                                @FieldResult(name="name", column="name"),
                                @FieldResult(name="amendGrantId",column = "amend_grant_id"),
                                @FieldResult(name="approvedReportsForGrant",column="approved_reports_for_grant")
                        }*/),
        }
)
@NamedNativeQuery(
        name="LISTINPROGRESSGRANTS",
        query = "select distinct A.*,(select assignments from grant_assignments where grant_id=a.id and state_id=a.grant_status_id) current_assignment,approved_reports_for_grant(a.id) approved_reports_for_grant, disbursed_amount_for_grant(a.id) approved_disbursements_total, project_documents_for_grant(a.id) project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=:granterId and A.deleted=false and ( (B.anchor=true and B.assignments=:userId) or (B.assignments=:userId and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 ) or (C.internal_status='REVIEW') ) order by A.updated_at desc",
        resultSetMapping = "GRANTSLIST"
)
@NamedNativeQuery(
        name="LISTACTIVEGRANTS",
        query = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,approved_reports_for_grant(A.id) approved_reports_for_grant, disbursed_amount_for_grant(A.id) approved_disbursements_total, project_documents_for_grant(A.id) project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=:granterId and A.deleted=false and ( (C.internal_status='ACTIVE') ) order by A.updated_at desc",
        resultSetMapping = "GRANTSLIST"
)
@NamedNativeQuery(
        name="LISTCLOSEDGRANTS",
        query = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,approved_reports_for_grant(A.id) approved_reports_for_grant, disbursed_amount_for_grant(A.id) approved_disbursements_total, project_documents_for_grant(A.id) project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=:granterId and A.deleted=false and ( (C.internal_status='CLOSED') ) order by A.updated_at desc",
        resultSetMapping = "GRANTSLIST"
)
@NamedNativeQuery(
        name="LISTNONADMINGRANTS",
        query = "select distinct A.*,(select assignments from grant_assignments where grant_id=A.id and state_id=A.grant_status_id) current_assignment,0 approved_reports_for_grant, 0 approved_disbursements_total, 0 project_documents_count from grants A inner join grant_assignments B on B.grant_id=A.id inner join workflow_statuses C on C.id=A.grant_status_id where A.grantor_org_id=:granterId and A.deleted=false and ( (B.anchor=true and B.assignments=:userId) or (B.assignments=:userId and B.state_id=A.grant_status_id) or (C.internal_status='DRAFT' and (select count(*) from grant_history where id=A.id)>0 and :userId = any (array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='REVIEW' and :userId = any( array(select assignments from grant_assignments where grant_id=A.id))) or (C.internal_status='ACTIVE' or C.internal_status='CLOSED' ) ) order by A.updated_at desc",
        resultSetMapping = "GRANTSLIST"
)
public class GrantCard {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  @ApiModelProperty(name = "id", value = "Unique identifier of the grant", dataType = "Long")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "organization_id")
  @ApiModelProperty(name = "organization", value = "Grantee organization associated with the grant", dataType = "Organization")
  private Organization organization;

  @ManyToOne
  @JoinColumn(name = "grantor_org_id")
  @ApiModelProperty(name = "grantorOrganization", value = "Granter or tenant organization associated with the grant", dataType = "Granter")
  private Granter grantorOrganization;

  @Column(name = "name", columnDefinition = "text")
  @ApiModelProperty(name = "name", value = "Title of the grant", dataType = "String")
  private String name;

  @Column(name = "description", columnDefinition = "text")
  @ApiModelProperty(name = "description", value = "Description of the grant", dataType = "String")
  private String description;

  @Column
  @ApiModelProperty(name = "templateId", value = "Unique identified of teamplte associated with the grant", dataType = "Long")
  private Long templateId;

  @Transient
  @ApiModelProperty(name = "grantTemplate", value = "Template associated with the grant", dataType = "GranterGrantTemplate")
  private GranterGrantTemplate grantTemplate;

  @Column
  @ApiModelProperty(name = "amount", value = "Grant amount", dataType = "Double")
  private Double amount;

  @Column
  @ApiModelProperty(name = "createdAt", value = "Date when grant was created", dataType = "Date")
  private Date createdAt;

  @Column
  @ApiModelProperty(name = "createdBy", value = "Email id of user who created the grant", dataType = "String")
  private String createdBy;

  @Column
  @ApiModelProperty(name = "updatedAt", value = "Date when grant was updated", dataType = "Date")
  private Date updatedAt;

  @Column
  @ApiModelProperty(name = "updatedBy", value = "Email id of user who updated the grant", dataType = "String")
  private String updatedBy;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @ApiModelProperty(name = "grantStatus", value = "Grant workflow status", dataType = "WorkflowStatus")
  private WorkflowStatus grantStatus;

  @Column
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(name = "statusName", value = "Grant status in text format", dataType = "String")
  private GrantStatus statusName;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private WorkflowStatus substatus;

  @Column
  @ApiModelProperty(name = "startDate", value = "Start date of the grant", dataType = "Date")
  private Date startDate;

  @Transient
  @ApiModelProperty(name = "stDate", value = "Formatted start date of the grant ", dataType = "String")
  private String stDate;

  @Column
  @ApiModelProperty(name = "endDate", value = "End date of the grant", dataType = "Date")
  private Date endDate;
  @Transient
  @ApiModelProperty(name = "enDate", value = "Formatted end date of the grant", dataType = "Date")
  private String enDate;

  @Column
  @ApiModelProperty(name = "representative", value = "Name of representative from Grantee organization", dataType = "String")
  private String representative;

  @Column
  @ApiModelProperty(name = "note", value = "Current note associated with the grant", dataType = "String")
  private String note;

  @Column
  @ApiModelProperty(name = "noteAdded", value = "Date when current note was associated with the grant", dataType = "Date")
  private Date noteAdded;

  @Column
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(name = "noteAddedBy", value = "Email id of the user who added the current note", dataType = "String")
  private String noteAddedBy;

  @Transient
  @ApiModelProperty(name = "noteAddedByUser", value = "User who added the current note", dataType = "User")
  private User noteAddedByUser;

  @OneToMany(mappedBy = "grant", cascade = CascadeType.ALL)
  @OrderBy("submitBy ASC")
  @JsonManagedReference
  @JsonIgnore
  private List<Submission> submissions;

  @Transient
  @ApiModelProperty(name = "actionAuthorities", value = "Allowed actions that can be performed by the user for current grant status", dataType = "List<WorkflowActionPermission>")
  private WorkflowActionPermission actionAuthorities;
  @Transient
  @ApiModelProperty(name = "actionAuthorities", value = "Allowed workflow status changes that can be performed by the user for current grant status", dataType = "List<WorkflowPermission>")
  private List<WorkFlowPermission> flowAuthorities;
  @Transient
  @ApiModelProperty(name = "grantDetails", value = "All grant section and section attributes and values of the grant", dataType = "GrantDetailVO")
  private GrantDetailVO grantDetails;
  @Column
  @ApiModelProperty(name = "currentAssignment", value = "Current owner of grant based on grant status", dataType = "List<AssignedTo>")
  private Long currentAssignment;
  @OneToMany(mappedBy = "grant")
  @ApiModelProperty(name = "workflowAssignment", value = "Allowed workflow ownership assignments for the grant", dataType = "List<GrantAssignmentsVO>")
  private List<GrantAssignmentsCard> workflowAssignment;

  @OneToMany(mappedBy = "grant")
  private List<GrantTagCard> grantTags;

  @Transient
  @ApiModelProperty(name = "securityCode", value = "Secure code for grant")
  private String securityCode;

  @Column
  private Date movedOn;

  @Transient
  private List<TableData> approvedReportsDisbursements;

  @Column(columnDefinition = "text")
  private String referenceNo;
  @Column
  private Boolean deleted;
  @Transient
  private Boolean hasOngoingDisbursement = false;
  @Column
  private int projectDocumentsCount = 0;
  @Column
  private Double approvedDisbursementsTotal = 0d;
  @Column()
  private int approvedReportsForGrant;
  @Column
  private Long origGrantId;
  @Transient
  private String origGrantRefNo;
  @Column
  private Long amendGrantId;
  @Column
  private boolean amended;
  @Column
  private int amendmentNo = 0;
  @Transient
  private Date minEndEndate;
  @Column
  private Boolean internal;
  @Column
  private Long grantTypeId;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
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

  public Organization getGrantorOrganization() {
    return grantorOrganization;
  }

  public void setGrantorOrganization(Granter grantorOrganization) {
    this.grantorOrganization = grantorOrganization;
  }

  public GrantStatus getStatusName() {
    return statusName;
  }

  public void setStatusName(GrantStatus status) {
    this.statusName = status;
  }

  public WorkflowStatus getSubstatus() {
    return substatus;
  }

  public void setSubstatus(WorkflowStatus substatus) {
    this.substatus = substatus;
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

  public List<Submission> getSubmissions() {
    return submissions;
  }

  public void setSubmissions(List<Submission> submissions) {
    this.submissions = submissions;
  }

  public WorkflowStatus getGrantStatus() {
    return grantStatus;
  }

  public void setGrantStatus(WorkflowStatus status) {
    this.grantStatus = status;
  }

  public WorkflowActionPermission getActionAuthorities() {
    return actionAuthorities;
  }

  public void setActionAuthorities(WorkflowActionPermission actionAuthorities) {
    this.actionAuthorities = actionAuthorities;
  }

  public List<WorkFlowPermission> getFlowAuthorities() {
    return flowAuthorities;
  }

  public void setFlowAuthorities(List<WorkFlowPermission> flowAuthorities) {
    this.flowAuthorities = flowAuthorities;
  }

  public GrantDetailVO getGrantDetails() {
    return grantDetails;
  }

  public void setGrantDetails(GrantDetailVO grantDetails) {
    this.grantDetails = grantDetails;
  }

  public String getStDate() {
    if (startDate == null) {
      return "";
    }

    return new SimpleDateFormat("yyyy-MM-dd").format(startDate);
  }

  public void setStDate(String stDate) {
    this.stDate = stDate;
  }

  public String getEnDate() {

    if (endDate == null) {
      return "";
    }
    return new SimpleDateFormat("yyyy-MM-dd").format(endDate);
  }

  public void setEnDate(String enDate) {
    this.enDate = enDate;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setRepresentative(String rep) {
    this.representative = rep;
  }

  public String getRepresentative() {
    return this.representative;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public GranterGrantTemplate getGrantTemplate() {
    return grantTemplate;
  }

  public void setGrantTemplate(GranterGrantTemplate grantTemplate) {
    this.grantTemplate = grantTemplate;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Long getCurrentAssignment() {
    return currentAssignment;
  }

  public void setCurrentAssignment(Long currentAssignment) {
    this.currentAssignment = currentAssignment;
  }

  public List<GrantAssignmentsCard> getWorkflowAssignment() {
    return workflowAssignment;
  }

  public void setWorkflowAssignment(List<GrantAssignmentsCard> workflowAssignment) {
    this.workflowAssignment = workflowAssignment;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Date getNoteAdded() {
    return noteAdded;
  }

  public void setNoteAdded(Date noteAdded) {
    this.noteAdded = noteAdded;
  }

  public String getNoteAddedBy() {
    return noteAddedBy;
  }

  public void setNoteAddedBy(String noteAddedBy) {
    this.noteAddedBy = noteAddedBy;
  }

  public User getNoteAddedByUser() {
    return noteAddedByUser;
  }

  public void setNoteAddedByUser(User noteAddedByUser) {
    this.noteAddedByUser = noteAddedByUser;
  }

  public String getSecurityCode() {
    return securityCode;
  }

  public void setSecurityCode(String securityCode) {
    this.securityCode = securityCode;
  }

  public Date getMovedOn() {
    return movedOn;
  }

  public void setMovedOn(Date movedOn) {
    this.movedOn = movedOn;
  }

  public List<TableData> getApprovedReportsDisbursements() {
    return approvedReportsDisbursements;
  }

  public void setApprovedReportsDisbursements(List<TableData> approvedReportsDisbursements) {
    this.approvedReportsDisbursements = approvedReportsDisbursements;
  }

  public String getReferenceNo() {
    return referenceNo;
  }

  public void setReferenceNo(String referenceNo) {
    this.referenceNo = referenceNo;
  }

  public Boolean getDeleted() {
    return deleted;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  public Boolean getHasOngoingDisbursement() {
    return hasOngoingDisbursement;
  }

  public void setHasOngoingDisbursement(Boolean hasOngoingDisbursement) {
    this.hasOngoingDisbursement = hasOngoingDisbursement;
  }

  public int getProjectDocumentsCount() {
    return projectDocumentsCount;
  }

  public void setProjectDocumentsCount(int projectDocumentsCount) {
    this.projectDocumentsCount = projectDocumentsCount;
  }

  public Double getApprovedDisbursementsTotal() {
    return approvedDisbursementsTotal;
  }

  public void setApprovedDisbursementsTotal(Double approvedDisbursementsTotal) {
    this.approvedDisbursementsTotal = approvedDisbursementsTotal;
  }

  public int getApprovedReportsForGrant() {
    return approvedReportsForGrant;
  }

  public void setApprovedReportsForGrant(int approvedReportsForGrant) {
    this.approvedReportsForGrant = approvedReportsForGrant;
  }

  public Long getOrigGrantId() {
    return origGrantId;
  }

  public void setOrigGrantId(Long origGrantId) {
    this.origGrantId = origGrantId;
  }

  public Long getAmendGrantId() {
    return amendGrantId;
  }

  public void setAmendGrantId(Long amendGrantId) {
    this.amendGrantId = amendGrantId;
  }

  public boolean isAmended() {
    return amended;
  }

  public void setAmended(boolean amended) {
    this.amended = amended;
  }

  public String getOrigGrantRefNo() {
    return origGrantRefNo;
  }

  public void setOrigGrantRefNo(String origGrantRefNo) {
    this.origGrantRefNo = origGrantRefNo;
  }

  public int getAmendmentNo() {
    return amendmentNo;
  }

  public void setAmendmentNo(int amendmentNo) {
    this.amendmentNo = amendmentNo;
  }

  public Date getMinEndEndate() {
    return minEndEndate;
  }

  public void setMinEndEndate(Date minEndEndate) {
    this.minEndEndate = minEndEndate;
  }

  public Boolean getInternal() {
    return internal;
  }

  public void setInternal(Boolean internal) {
    this.internal = internal;
  }

  public Long getGrantTypeId() {
    return grantTypeId;
  }

  public void setGrantTypeId(Long grantTypeId) {
    this.grantTypeId = grantTypeId;
  }

  public List<GrantTagCard> getGrantTags() {
    return grantTags;
  }

  public void setGrantTags(List<GrantTagCard> grantTags) {
    this.grantTags = grantTags;
  }



}
