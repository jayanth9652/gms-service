package org.codealpha.gmsservice.services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.*;
import org.codealpha.gmsservice.entities.dashboard.*;
import org.codealpha.gmsservice.models.*;
import org.codealpha.gmsservice.repositories.ActualDisbursementRepository;
import org.codealpha.gmsservice.repositories.DisbursementRepository;
import org.codealpha.gmsservice.repositories.GrantAssignmentHistoryRepository;
import org.codealpha.gmsservice.repositories.GrantRepository;
import org.codealpha.gmsservice.repositories.ReportsCountPerGrantRepository;
import org.codealpha.gmsservice.repositories.WorkflowStatusRepository;
import org.codealpha.gmsservice.repositories.dashboard.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//Edited comment for testing

@Service
public class DashboardService {

    private Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private User user;

    @Autowired
    private WorkflowPermissionService workflowPermissionService;
    @Autowired
    private AppConfigService appConfigService;
    @Autowired
    private GranterGrantTemplateService granterGrantTemplateService;
    @Autowired
    private GrantService grantService;
    @Autowired
    private TemplateLibraryService templateLibraryService;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkflowStatusService workflowStatusService;
    @Autowired
    private GranterCountAndAmountTotalRepository granterCountAndAmountTotalRepository;
    @Autowired
    private GranterGranteeRepository granterGranteeRepository;
    @Autowired
    private GranterActiveUserRepository granterActiveUserRepository;
    @Autowired
    private GranterGrantSummaryCommittedRepository granterActiveGrantSummaryCommittedRepository;
    @Autowired
    private GranterGrantSummaryDisbursedRepository granterActiveGrantSummaryDisbursedRepository;
    @Autowired
    private GranterReportStatusRepository granterReportStatusRepository;
    @Autowired
    private GranterReportSummaryStatusRepository granterReportSummaryStatusRepository;
    @Autowired
    private ReportsCountPerGrantRepository reportsCountPerGrantRepository;
    @Autowired
    private DisbursementRepository disbursementRepository;
    @Autowired
    private ActualDisbursementRepository actualDisbursementRepository;
    @Autowired
    private GrantRepository grantRepository;
    @Autowired
    private GrantAssignmentHistoryRepository assignmentHistoryRepository;
    @Autowired
    private WorkflowStatusRepository workflowStatusRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    private TransitionStatusOrderRepository transitionStatusOrderRepository;

    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private OrgTagService orgTagService;

    @Value("${spring.timezone}")
    private String timezone;
    @Autowired
    private GranteeReportStatusRepository granteeReportStatusRepository;

    List<Tenant> tenants;

    public DashboardService build(User user, List<GrantCard> grants, Organization tenantOrg) {
        this.user = user;
        List<String> tenantNames = new ArrayList<>();
        if (!tenantNames.contains(tenantOrg.getCode())) {
            tenantNames.add(tenantOrg.getCode());
        }

        tenants = new ArrayList<>();
        for (String name : tenantNames) {
            Tenant tenant = new Tenant();
            tenant.setName(name);
            List<GrantCard> grantsList = new ArrayList<>();
            tenant.setGrants(grantsList);
            tenant.setGrantTemplates(granterGrantTemplateService
                    .findByGranterIdAndPublishedStatusAndPrivateStatus(user.getOrganization().getId(), true, false));
            if (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")) {
                tenant.setTemplateLibrary(
                        templateLibraryService.getTemplateLibraryForGranter((Granter) user.getOrganization()));
            }
            tenants.add(tenant);
        }

        for (GrantCard grant: grants) {
            for (Tenant tenant : tenants) {
                if ((user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTER")
                        && tenant.getName().equalsIgnoreCase(grant.getGrantorOrganization().getCode()))
                        || (user.getOrganization().getOrganizationType().equalsIgnoreCase("GRANTEE"))) {
                    List<GrantCard> grantList = tenant.getGrants();

                    /*GrantVO grantVO = new GrantVO();
                    grantVO = grantVO.build(grant, grantService.getGrantSections(grant), workflowPermissionService,
                            user, appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                                    AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS),
                            userService);
                    grant.setGrantDetails(grantVO.getGrantDetails());*/
                    // grant.setNoteAddedBy(grantVO.getNoteAddedBy());
                    //grant.setNoteAddedByUser(
                    //        userService.getUserByEmailAndOrg(grant.getNoteAddedBy(), grant.getGrantorOrganization()));
                    //grant.setGrantTemplate(granterGrantTemplateService.findByTemplateId(grant.getTemplateId()));
                    //List<GrantAssignmentsVO> workflowAssignments = new ArrayList<>();
                    /*for (GrantAssignments assignment : grantService.getGrantWorkflowAssignments(grant)) {
                        GrantAssignmentsVO assignmentsVO = new GrantAssignmentsVO();
                        assignmentsVO.setId(assignment.getId());
                        assignmentsVO.setAnchor(assignment.isAnchor());
                        assignmentsVO.setAssignments(assignment.getAssignments());
                        if (assignment.getAssignments() != null && assignment.getAssignments() > 0) {
                            assignmentsVO.setAssignmentUser(userService.getUserById(assignment.getAssignments()));
                        }
                        assignmentsVO.setGrantId(assignment.getGrantId());
                        assignmentsVO.setStateId(assignment.getStateId());
                        assignmentsVO.setStateName(workflowStatusService.findById(assignment.getStateId()));

                        if (grantRepository.findGrantsThatMovedAtleastOnce(grant.getId()).size() > 0) {
                            List<GrantAssignmentHistory> history = assignmentHistoryRepository
                                    .findByGrantIdAndStateIdOrderByUpdatedOnDesc(grant.getId(),
                                            assignment.getStateId());
                            for (GrantAssignmentHistory h : history) {
                                if (h.getAssignments() != null && h.getAssignments() != 0) {
                                    h.setAssignmentUser(userService.getUserById(h.getAssignments()));
                                }

                                if (h.getUpdatedBy() != null && h.getUpdatedBy() != 0) {
                                    h.setUpdatedByUser(userService.getUserById(h.getUpdatedBy()));
                                }
                            }
                            assignmentsVO.setHistory(history);
                        }
                        workflowAssignments.add(assignmentsVO);
                    }*/
                    //grant.setWorkflowAssignment(workflowAssignments);

                    //List<GrantAssignments> grantAssignments = grantService.getGrantCurrentAssignments(grant);
                    /*if (grantAssignments != null) {
                        for (GrantAssignments assignment : grantAssignments) {
                            if (grant.getCurrentAssignment() == null) {
                                List<AssignedTo> assignedToList = new ArrayList<>();
                                grant.setCurrentAssignment(assignedToList);
                            }
                            AssignedTo newAssignedTo = new AssignedTo();
                            if (assignment.getAssignments() != null && assignment.getAssignments() > 0) {
                                newAssignedTo.setUser(userService.getUserById(assignment.getAssignments()));
                            }
                            grant.getCurrentAssignment().add(newAssignedTo);
                        }
                    }
                    grant.getWorkflowAssignment().sort((a, b) -> a.getId().compareTo(b.getId()));

                    grant.getGrantDetails().getSections()
                            .sort((a, b) -> Long.valueOf(a.getOrder()).compareTo(Long.valueOf(b.getOrder())));
                    for (SectionVO section : grant.getGrantDetails().getSections()) {
                        if (section.getAttributes() != null) {
                            section.getAttributes().sort((a, b) -> Long.valueOf(a.getAttributeOrder())
                                    .compareTo(Long.valueOf(b.getAttributeOrder())));
                        }
                    }*/
                    //grant.setSecurityCode(grantService.buildHashCode(grant));
                    /*grant.setProjectDocumentsCount(grantService.getGrantsDocuments(grant.getId()).size());

                    List<WorkflowStatus> workflowStatuses = workflowStatusRepository
                            .getAllTenantStatuses("DISBURSEMENT", grant.getGrantorOrganization().getId());
                    List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                            .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED"))
                            .collect(Collectors.toList());
                    List<Long> statusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                            .collect(Collectors.toList());
                    *//*List<Disbursement> approvedDisbursements = disbursementRepository
                            .getDisbursementByGrantAndStatuses(grant.getId(), statusIds);
                    List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
                    if (approvedDisbursements != null) {

                        for (Disbursement approved : approvedDisbursements) {
                            List<ActualDisbursement> approvedActuals = actualDisbursementRepository
                                    .findByDisbursementId(approved.getId());
                            if (approvedActuals.size() > 0) {
                                approvedActualDisbursements.addAll(approvedActuals);
                            }
                        }
                    }*//*
                    Double total = 0d;
                    *//*if (approvedActualDisbursements.size() > 0) {

                        for (ActualDisbursement ad : approvedActualDisbursements) {
                            if (ad.getActualAmount() != null) {
                                total += ad.getActualAmount();
                            }
                        }

                    }*//*
                        //if(grant.getOrigGrantId()!=null){
                            total +=getAllLinkedGrantsDisbursementsTotal(grant,statusIds);
                        //}
                    grant.setApprovedDisbursementsTotal(total);*/

                    /*Optional<WorkflowStatus> reportApprovedStatus = workflowStatusService
                            .getTenantWorkflowStatuses("REPORT", tenantOrg.getId()).stream()
                            .filter(s -> s.getInternalStatus().equalsIgnoreCase("CLOSED")).findFirst();
                    List<Report> reports = new ArrayList<>();
                    int noOfReports = 0;
                    if (reportApprovedStatus.isPresent()) {
                        reports = reportService.findReportsByStatusForGrant(reportApprovedStatus.get(), grant);
                        noOfReports = reports.size();
                        // Include approved reports of orgiginal grant if exist
                        if (grant.getOrigGrantId() != null) {
                            reports = reportService.findReportsByStatusForGrant(reportApprovedStatus.get(),
                                    grantService.getById(grant.getOrigGrantId()));
                            noOfReports += reports.size();
                        }
                        // End
                        grant.setApprovedReportsForGrant(noOfReports);
                    }*/

                    if (grant.getOrigGrantId() != null
                            && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase("ACTIVE")
                            && !grant.getGrantStatus().getInternalStatus().equalsIgnoreCase("CLOSED")) {
                        grant.setOrigGrantRefNo(grantService.getById(grant.getOrigGrantId()).getReferenceNo());
                    }

                    /*if (grant.getOrigGrantId() != null) {
                        List<Report> existingReports = reportService
                                .getReportsForGrant(grantService.getById(grant.getOrigGrantId()));
                        if (existingReports != null && existingReports.size() > 0) {
                            *//*existingReports
                                    .removeIf(r -> r.getStatus().getInternalStatus().equalsIgnoreCase("DRAFT"));*//*
                            existingReports.removeIf(r -> r.getEndDate()==null);
                            if (existingReports != null && existingReports.size() > 0) {


                                Comparator<Report> endDateComparator = Comparator.comparing(c -> c.getEndDate());
                                existingReports.sort(endDateComparator);

                                Report lastReport = existingReports.get(existingReports.size() - 1);
                                grant.setMinEndEndate(lastReport.getEndDate());
                            }
                        }*/

                        /*List<Disbursement> existingDisbursements = disbursementService
                                .getAllDisbursementsForGrant(grant.getOrigGrantId());
                        if (existingDisbursements != null && existingDisbursements.size() > 0) {
                            existingDisbursements
                                    .removeIf(d -> d.getStatus().getInternalStatus().equalsIgnoreCase("DRAFT"));
                            if (existingDisbursements != null && existingDisbursements.size() > 0) {

                                Comparator<Disbursement> endDateComparator = Comparator.comparing(d -> d.getMovedOn());
                                existingDisbursements.sort(endDateComparator);
                                Disbursement lastDisbursement = existingDisbursements
                                        .get(existingDisbursements.size() - 1);
                                if (grant.getMinEndEndate() != null && new DateTime(lastDisbursement.getMovedOn())
                                        .isAfter(new DateTime(grant.getMinEndEndate()))) {
                                    grant.setMinEndEndate(lastDisbursement.getMovedOn());
                                }

                            }
                        }
                    }*/

                    /*List<GrantTag> grantTags = grantService.getTagsForGrant(grant.getId());
                    List<GrantTagVO> grantTagsVoList = new ArrayList<>();
                    for(GrantTag tag: grantTags){
                        GrantTagVO vo =new GrantTagVO();
                        vo.setGrantId(grant.getId());
                        vo.setId(tag.getId());
                        vo.setOrgTagId(tag.getOrgTagId());
                        vo.setTagName(orgTagService.getOrgTagById(tag.getOrgTagId()).getName());
                        grantTagsVoList.add(vo);
                    }
                    grant.setGrantTags(grantTagsVoList);*/

                    grantList.add(grant);
                    tenant.setGrants(grantList);
                }
            }
        }

        return this;
    }

    private Double getAllLinkedGrantsDisbursementsTotal(Grant byId, List<Long> statusIds) {
        List<Disbursement> approvedDisbursements = disbursementRepository
                .getDisbursementByGrantAndStatuses(byId.getId(), statusIds);

        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        if (approvedDisbursements != null) {

            for (Disbursement approved : approvedDisbursements) {
                List<ActualDisbursement> approvedActuals = actualDisbursementRepository
                        .findByDisbursementId(approved.getId());
                if (approvedActuals.size() > 0) {
                    approvedActualDisbursements.addAll(approvedActuals);
                }
            }
        }
        Double total = 0d;
        if (approvedActualDisbursements.size() > 0) {

            for (ActualDisbursement ad : approvedActualDisbursements) {
                if (ad.getActualAmount() != null) {
                    total += ad.getActualAmount();
                }
            }

        }
        if (byId.getOrigGrantId() != null) {
            total += getAllLinkedGrantsDisbursementsTotal(grantService.getById(byId.getOrigGrantId()), statusIds);
        }

        return total;
    }

    private Double getAllLinkedGrantsDisbursementsTotalForGrantee(Grant byId) {
        List<Disbursement> approvedDisbursements = disbursementRepository
                .getClosedDisbursementByGrantAndStatusesForGrantee(byId.getId());

        List<ActualDisbursement> approvedActualDisbursements = new ArrayList<>();
        if (approvedDisbursements != null) {

            for (Disbursement approved : approvedDisbursements) {
                List<ActualDisbursement> approvedActuals = actualDisbursementRepository
                        .findByDisbursementId(approved.getId());
                if (approvedActuals.size() > 0) {
                    approvedActualDisbursements.addAll(approvedActuals);
                }
            }
        }
        Double total = 0d;
        if (approvedActualDisbursements.size() > 0) {

            for (ActualDisbursement ad : approvedActualDisbursements) {
                if (ad.getActualAmount() != null) {
                    total += ad.getActualAmount();
                }
            }

        }
        if (byId.getOrigGrantId() != null) {
            total += getAllLinkedGrantsDisbursementsTotalForGrantee(grantService.getById(byId.getOrigGrantId()));
        }

        return total;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public GranterCountAndAmountTotal getSummaryForGranter(Long granterId) {
        return granterCountAndAmountTotalRepository.getSummaryForGranter(granterId);
    }

    public GranterCountAndAmountTotal getSummaryForGrantee(Long granterId) {
        return granterCountAndAmountTotalRepository.getSummaryForGranter(granterId);
    }

    public GranterCountAndAmountTotal getMySummaryForGranter(Long granterId) {
        return granterCountAndAmountTotalRepository.getMySummaryForGranter(granterId);
    }

    public GranterGrantee getGranteesSummaryForGranter(Long granterId) {
        return granterGranteeRepository.getGranteeSummaryForGranter(granterId);
    }

    public GranterGrantee getMyGranteesSummaryForGranter(Long userId,String status) {
        return granterGranteeRepository.getMyGranteeSummaryForGranter(userId,status);
    }

    public GranterActiveUser getActiveUserSummaryForGranter(Long granterId) {
        return granterActiveUserRepository.getActiveUserSummaryForGranter(granterId);
    }

    public GranterGrantSummaryCommitted getActiveGrantCommittedSummaryForGranter(Long granterId, String status) {
        return granterActiveGrantSummaryCommittedRepository.getGrantCommittedSummaryForGranter(granterId, status);
    }

    public GranterGrantSummaryCommitted getActiveGrantCommittedSummaryForGrantee(Long granteeId, String status) {
        return granterActiveGrantSummaryCommittedRepository.getGrantCommittedSummaryForGrantee(granteeId, status);
    }

    public Double getActiveGrantDisbursedAmountForGranter(Long granterId, String status) {
        Double disbursedAmount = 0d;

        List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                granterId);

        List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED")).collect(Collectors.toList());
        List<Long> closedStatusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                .collect(Collectors.toList());

        List<Grant> activeGrants = grantRepository.findGrantsByStatus(granterId,status);
        if (activeGrants != null && !activeGrants.isEmpty()) {
            for (Grant ag : activeGrants) {
                disbursedAmount+= getAllLinkedGrantsDisbursementsTotal(ag,closedStatusIds);
            }
            /*List<Disbursement> allClosedDisbursements = new ArrayList<>();

            for (Grant ag : activeGrants) {
                List<Disbursement> closedDisbursements = disbursementRepository
                        .getDisbursementByGrantAndStatuses(ag.getId(), closedStatusIds);
                allClosedDisbursements.addAll(closedDisbursements);
            }

            List<ActualDisbursement> allActualDisbursements = new ArrayList();
            for (Disbursement cd : allClosedDisbursements) {
                List<ActualDisbursement> actualDisbursements = actualDisbursementRepository
                        .findByDisbursementId(cd.getId());
                if (actualDisbursements != null) {
                    allActualDisbursements.addAll(actualDisbursements);
                }
            }

            for (ActualDisbursement ad : allActualDisbursements) {
                disbursedAmount += ad.getActualAmount() == null ? 0 : ad.getActualAmount();
            }*/

        }

        return disbursedAmount;
    }

    public Double getActiveGrantDisbursedAmountForGrantee(Long granteeId, String status) {
        Double disbursedAmount = 0d;

        List<Grant> activeGrants = grantRepository.findGrantsByStatusForGrantee(granteeId,status);
        if (activeGrants != null && !activeGrants.isEmpty()) {
            for (Grant ag : activeGrants) {
                disbursedAmount+= getAllLinkedGrantsDisbursementsTotalForGrantee(ag);
            }
        }

        return disbursedAmount;
    }

    public List<GranterReportStatus> getReportStatusSummaryForGranterAndStatus(Long granterId, String status) {
        return granterReportStatusRepository.getReportStatusesForGranter(granterId, status);
    }

    public List<GranterReportStatus> getReportStatusSummaryForGranteeAndStatus(Long granteeId, String status) {
        return granterReportStatusRepository.getReportStatusesForGrantee(granteeId, status);
    }

    public List<GranterReportStatus> getReportStatusSummaryForUserAndStatus(Long userId, String status) {
        return granterReportStatusRepository.getReportStatusesForUser(userId, status);
    }

    public List<GranterReportSummaryStatus> getReportByStatusForGranter(Long granterId) {
        return granterReportSummaryStatusRepository.getReportsByStatusForGranter(granterId);
    }

    public List<GranterReportSummaryStatus> getReportByStatusForUser(Long userId) {
        return granterReportSummaryStatusRepository.getReportsByStatusForUser(userId);
    }

    public List<TransitionStatusOrder> getStatusTransitionOrderByWorflowAndGrantType(Long workflowId,Long grantTypeId){
        return transitionStatusOrderRepository.getTransitionOrderByWorkflowAndGrantType(workflowId,grantTypeId);
    }

    public TransitionStatusOrder getStatusTransitionOrderForTerminalState(Long workflowId,Long grantTypeId){
        return transitionStatusOrderRepository.getTransitionOrderForTerminalState(workflowId,grantTypeId);
    }

    public List<GranterReportStatus> findGrantCountsByReportNumbersAndStatusForGranter(Long granterId, String status) {
        List<GranterReportStatus> reportStatuses = new ArrayList<>();
        Map<Long, Long> transposedMap = new HashMap<>();
        List<ReportsCountPerGrant> countPerGrants = reportsCountPerGrantRepository
                .findGrantCountsByReportNumbersAndStatusForGranter(granterId, status);
        for (ReportsCountPerGrant countPerGrant : countPerGrants) {

            if (transposedMap.containsKey(countPerGrant.getCount())) {
                transposedMap.replace(countPerGrant.getCount(), transposedMap.get(countPerGrant.getCount()) + 1);
            } else {
                transposedMap.put(countPerGrant.getCount(), 1l);
            }

        }
        transposedMap.forEach((k, v) -> {
            GranterReportStatus reportStatus = new GranterReportStatus();
            reportStatus.setStatus(String.valueOf(k));
            reportStatus.setCount(v.intValue());
            reportStatuses.add(reportStatus);
        });
        return reportStatuses;
    }

    public Map<Integer, String> getActiveGrantsCommittedPeriodsForGranterAndStatus(Long granterId, String status) {

        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if (status.equalsIgnoreCase("ACTIVE")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getActiveGrantCommittedSummaryForGranter(granterId);
        } else if (status.equalsIgnoreCase("CLOSED")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getClosedGrantCommittedSummaryForGranter(granterId);
        }
        Map<Integer, String> periods = new HashMap<>();
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {
                DateTime grantDate = new DateTime(granterGrantSummaryDisbursed.getStartDate(),
                        DateTimeZone.forID(timezone));
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone)).withYear(grantDate.getYear())
                        .withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);
                String period = null;
                if (grantDate.isAfter(calendarYearStart)) {
                    period = String.valueOf(grantDate.getYear()) + " - " + String.valueOf(grantDate.getYear() + 1);
                    periods.put(grantDate.getYear(), period);
                } else {
                    period = String.valueOf(grantDate.getYear() - 1) + " - " + String.valueOf(grantDate.getYear());
                    periods.put(grantDate.getYear() - 1, period);
                }

                //////
                List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                        granterId);

                List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                        .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED")).collect(Collectors.toList());
                List<Long> closedStatusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                        .collect(Collectors.toList());
                List<Disbursement> allClosedDisbursements = new ArrayList<>();
                List<Disbursement> closedDisbursements = disbursementRepository
                        .getDisbursementByGrantAndStatuses(granterGrantSummaryDisbursed.getGrantId(), closedStatusIds);
                allClosedDisbursements.addAll(closedDisbursements);

                for (Disbursement cd : allClosedDisbursements) {
                    List<ActualDisbursement> actualDisbursements = actualDisbursementRepository
                            .findByDisbursementId(cd.getId());

                    for(ActualDisbursement ad : actualDisbursements){
                        DateTime actualDisbursementDate = new DateTime(ad.getDisbursementDate(),
                                DateTimeZone.forID(timezone));
                        DateTime calendarYearStart1 = new DateTime(DateTimeZone.forID(timezone)).withYear(actualDisbursementDate.getYear())
                                .withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);
                        String period1 = null;
                        if (actualDisbursementDate.isAfter(calendarYearStart1)) {
                            period1 = String.valueOf(actualDisbursementDate.getYear()) + " - " + String.valueOf(actualDisbursementDate.getYear() + 1);
                            periods.put(actualDisbursementDate.getYear(), period1);
                        } else {
                            period1 = String.valueOf(actualDisbursementDate.getYear() - 1) + " - " + String.valueOf(actualDisbursementDate.getYear());
                            periods.put(actualDisbursementDate.getYear() - 1, period1);
                        }
                    }
                }
                ///
            }
        }
        return periods;
    }

    public Map<Integer, String> getGrantsCommittedPeriodsForUserAndStatus(User user, String status) {

        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if (status.equalsIgnoreCase("ACTIVE")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getActiveGrantCommittedSummaryForUser(user.getId());
        } else if (status.equalsIgnoreCase("CLOSED")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getClosedGrantCommittedSummaryForUser(user.getId());
        }
        Map<Integer, String> periods = new HashMap<>();
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {
                DateTime grantDate = new DateTime(granterGrantSummaryDisbursed.getStartDate(),
                        DateTimeZone.forID(timezone));
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone)).withYear(grantDate.getYear())
                        .withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);
                String period = null;
                if (grantDate.isAfter(calendarYearStart)) {
                    period = String.valueOf(grantDate.getYear()) + " - " + String.valueOf(grantDate.getYear() + 1);
                    periods.put(grantDate.getYear(), period);
                } else {
                    period = String.valueOf(grantDate.getYear() - 1) + " - " + String.valueOf(grantDate.getYear());
                    periods.put(grantDate.getYear() - 1, period);
                }

                //////
                List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                        user.getOrganization().getId());

                List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                        .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED")).collect(Collectors.toList());
                List<Long> closedStatusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                        .collect(Collectors.toList());
                List<Disbursement> allClosedDisbursements = new ArrayList<>();
                List<Disbursement> closedDisbursements = disbursementRepository
                        .getDisbursementByGrantAndStatuses(granterGrantSummaryDisbursed.getGrantId(), closedStatusIds);
                allClosedDisbursements.addAll(closedDisbursements);

                for (Disbursement cd : allClosedDisbursements) {
                    List<ActualDisbursement> actualDisbursements = actualDisbursementRepository
                            .findByDisbursementId(cd.getId());

                    for(ActualDisbursement ad : actualDisbursements){
                        DateTime actualDisbursementDate = new DateTime(ad.getDisbursementDate(),
                                DateTimeZone.forID(timezone));
                        DateTime calendarYearStart1 = new DateTime(DateTimeZone.forID(timezone)).withYear(actualDisbursementDate.getYear())
                                .withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);
                        String period1 = null;
                        if (actualDisbursementDate.isAfter(calendarYearStart1)) {
                            period1 = String.valueOf(actualDisbursementDate.getYear()) + " - " + String.valueOf(actualDisbursementDate.getYear() + 1);
                            periods.put(actualDisbursementDate.getYear(), period1);
                        } else {
                            period1 = String.valueOf(actualDisbursementDate.getYear() - 1) + " - " + String.valueOf(actualDisbursementDate.getYear());
                            periods.put(actualDisbursementDate.getYear() - 1, period1);
                        }
                    }
                }
                ///
            }
        }
        return periods;
    }



    public Double[] getDisbursedAmountForGranterAndPeriodAndStatus(Integer period, Long granterId, String status) {

        Double total = 0d;

        List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                granterId);

        List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED")).collect(Collectors.toList());
        List<Long> closedStatusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                .collect(Collectors.toList());

        List<Grant> grantsByStatus = grantRepository.findGrantsByStatus(granterId,status);
        if (grantsByStatus != null && !grantsByStatus.isEmpty()) {
            List<Disbursement> allClosedDisbursements = new ArrayList<>();

            for (Grant ag : grantsByStatus) {
                List<Disbursement> closedDisbursements = disbursementRepository
                        .getDisbursementByGrantAndStatuses(ag.getId(), closedStatusIds);
                allClosedDisbursements.addAll(closedDisbursements);
            }

            List<ActualDisbursement> allActualDisbursements = new ArrayList();
            for (Disbursement cd : allClosedDisbursements) {
                List<ActualDisbursement> actualDisbursements = actualDisbursementRepository
                        .findByDisbursementId(cd.getId());
                if (actualDisbursements != null) {
                    allActualDisbursements.addAll(actualDisbursements);
                }
            }

            for (ActualDisbursement ad : allActualDisbursements) {
                SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
                DateTime disbursementDate = new DateTime(ad.getDisbursementDate(), DateTimeZone.forID(timezone));
                Double disbursementAmt = ad.getActualAmount();
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone))
                        .withYear(disbursementDate.getYear()).withMonthOfYear(Month.MARCH.getValue())
                        .withDayOfMonth(31);

                int disbursementYear = 0;
                if (disbursementDate.isAfter(calendarYearStart)) {
                    disbursementYear = disbursementDate.getYear();
                } else {
                    disbursementYear = disbursementDate.getYear() - 1;
                }
                if (period == disbursementYear) {
                    total += disbursementAmt == null ? 0 : disbursementAmt;
                }
            }

        }
        return new Double[] { total };
    }

    public Double[] getDisbursedAmountForUserAndPeriodAndStatus(Integer period, User user, String status) {

        Double total = 0d;

        List<WorkflowStatus> workflowStatuses = workflowStatusService.getTenantWorkflowStatuses("DISBURSEMENT",
                user.getOrganization().getId());

        List<WorkflowStatus> closedStatuses = workflowStatuses.stream()
                .filter(ws -> ws.getInternalStatus().equalsIgnoreCase("CLOSED")).collect(Collectors.toList());
        List<Long> closedStatusIds = closedStatuses.stream().mapToLong(s -> s.getId()).boxed()
                .collect(Collectors.toList());

        List<Grant> grantsByStatus = grantRepository.findGrantsByStatusForUser(user.getId(),status);
        if (grantsByStatus != null && !grantsByStatus.isEmpty()) {
            List<Disbursement> allClosedDisbursements = new ArrayList<>();

            for (Grant ag : grantsByStatus) {
                List<Disbursement> closedDisbursements = disbursementRepository
                        .getDisbursementByGrantAndStatuses(ag.getId(), closedStatusIds);
                allClosedDisbursements.addAll(closedDisbursements);
            }

            List<ActualDisbursement> allActualDisbursements = new ArrayList();
            for (Disbursement cd : allClosedDisbursements) {
                List<ActualDisbursement> actualDisbursements = actualDisbursementRepository
                        .findByDisbursementId(cd.getId());
                if (actualDisbursements != null) {
                    allActualDisbursements.addAll(actualDisbursements);
                }
            }

            for (ActualDisbursement ad : allActualDisbursements) {
                SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
                DateTime disbursementDate = new DateTime(ad.getDisbursementDate(), DateTimeZone.forID(timezone));
                Double disbursementAmt = ad.getActualAmount();
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone))
                        .withYear(disbursementDate.getYear()).withMonthOfYear(Month.MARCH.getValue())
                        .withDayOfMonth(31);

                int disbursementYear = 0;
                if (disbursementDate.isAfter(calendarYearStart)) {
                    disbursementYear = disbursementDate.getYear();
                } else {
                    disbursementYear = disbursementDate.getYear() - 1;
                }
                if (period == disbursementYear) {
                    total += disbursementAmt == null ? 0 : disbursementAmt;
                }
            }

        }
        return new Double[] { total };
    }

    public Long[] getCommittedAmountForGranterAndPeriodAndStatus(Integer period, Long granterId, String status) {
        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if (status.equalsIgnoreCase("ACTIVE")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getActiveGrantCommittedSummaryForGranter(granterId);
        } else if (status.equalsIgnoreCase("CLOSED")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getClosedGrantCommittedSummaryForGranter(granterId);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Long total = 0l;
        Map<Long, String> countMap = new HashMap<>();
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {

                DateTime committedDate = new DateTime(granterGrantSummaryDisbursed.getStartDate(),
                        DateTimeZone.forID(timezone));
                Long disbursementAmt = Long.valueOf(granterGrantSummaryDisbursed.getGrantAmount());
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone))
                        .withYear(committedDate.getYear()).withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);

                int disbursementYear = 0;
                if (committedDate.isAfter(calendarYearStart)) {
                    disbursementYear = committedDate.getYear();
                } else {
                    disbursementYear = committedDate.getYear() - 1;
                }
                if (period == disbursementYear) {
                    total += disbursementAmt;
                    countMap.put(granterGrantSummaryDisbursed.getGrantId(), "");
                }
            }
        }
        return new Long[] { total, Long.valueOf(countMap.size()) };
    }

    public Long[] getCommittedAmountForUserAndPeriodAndStatus(Integer period, User user, String status) {
        List<GranterGrantSummaryDisbursed> disbursedList = null;
        if (status.equalsIgnoreCase("ACTIVE")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getActiveGrantCommittedSummaryForUser(user.getId());
        } else if (status.equalsIgnoreCase("CLOSED")) {
            disbursedList = granterActiveGrantSummaryDisbursedRepository
                    .getClosedGrantCommittedSummaryForUser(user.getId());
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Long total = 0l;
        Map<Long, String> countMap = new HashMap<>();
        if (disbursedList != null && disbursedList.size() > 0) {
            for (GranterGrantSummaryDisbursed granterGrantSummaryDisbursed : disbursedList) {

                DateTime committedDate = new DateTime(granterGrantSummaryDisbursed.getStartDate(),
                        DateTimeZone.forID(timezone));
                Long disbursementAmt = Long.valueOf(granterGrantSummaryDisbursed.getGrantAmount());
                DateTime calendarYearStart = new DateTime(DateTimeZone.forID(timezone))
                        .withYear(committedDate.getYear()).withMonthOfYear(Month.MARCH.getValue()).withDayOfMonth(31);

                int disbursementYear = 0;
                if (committedDate.isAfter(calendarYearStart)) {
                    disbursementYear = committedDate.getYear();
                } else {
                    disbursementYear = committedDate.getYear() - 1;
                }
                if (period == disbursementYear) {
                    total += disbursementAmt;
                    countMap.put(granterGrantSummaryDisbursed.getGrantId(), "");
                }
            }
        }
        return new Long[] { total, Long.valueOf(countMap.size()) };
    }

    public GranterGrantSummaryCommitted getDisbursementPeriodsForUserAndStatus(Long userId, String status) {
        return granterActiveGrantSummaryCommittedRepository.getDisbursementPeriodsForUserAndStatus(userId,status);
    }

    public List<GranteeReportStatus> getReportApprovedStatusSummaryForGranteeAndStatusByGranter(Long id, String status) {
        return granteeReportStatusRepository.getReportApprovedStatusSummaryForGranteeAndStatusByGranter(id,status);
    }
}
