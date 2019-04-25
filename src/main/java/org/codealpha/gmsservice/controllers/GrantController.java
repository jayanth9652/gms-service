package org.codealpha.gmsservice.controllers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import javax.transaction.Transactional;
import org.codealpha.gmsservice.constants.AppConfiguration;
import org.codealpha.gmsservice.entities.Grant;
import org.codealpha.gmsservice.entities.GrantDocumentKpiData;
import org.codealpha.gmsservice.entities.GrantQualitativeKpiData;
import org.codealpha.gmsservice.entities.GrantQuantitativeKpiData;
import org.codealpha.gmsservice.entities.Submission;
import org.codealpha.gmsservice.entities.User;
import org.codealpha.gmsservice.models.GrantVO;
import org.codealpha.gmsservice.models.KpiSubmissionData;
import org.codealpha.gmsservice.services.AppConfigService;
import org.codealpha.gmsservice.services.CommonEmailSevice;
import org.codealpha.gmsservice.services.GrantDocumentDataService;
import org.codealpha.gmsservice.services.GrantQualitativeDataService;
import org.codealpha.gmsservice.services.GrantQuantitativeDataService;
import org.codealpha.gmsservice.services.GrantService;
import org.codealpha.gmsservice.services.SubmissionService;
import org.codealpha.gmsservice.services.UserService;
import org.codealpha.gmsservice.services.WorkflowPermissionService;
import org.codealpha.gmsservice.services.WorkflowStatusService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/{userId}/grant")
public class GrantController {

  @Autowired
  private GrantQuantitativeDataService quantitativeDataService;
  @Autowired
  GrantQualitativeDataService qualitativeDataService;
  @Autowired
  private WorkflowPermissionService workflowPermissionService;
  @Autowired
  private AppConfigService appConfigService;
  @Autowired
  private UserService userService;
  @Autowired
  private WorkflowStatusService workflowStatusService;
  @Autowired
  private GrantService grantService;
  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private GrantDocumentDataService grantDocumentDataService;
  @Value("${spring.upload-file-location}")
  private String uploadLocation;

  @Autowired
  private CommonEmailSevice commonEmailSevice;

  @PutMapping(value = "/kpi")
  @Transactional
  public GrantVO saveKpiSubmissions(@RequestBody List<KpiSubmissionData> submissionData,
      @PathVariable("userId") Long userId) {

    User user = userService.getUserById(userId);
    for (KpiSubmissionData data : submissionData) {
      switch (data.getType()) {
        case "QUANTITATIVE":
          GrantQuantitativeKpiData quantitativeKpiData = quantitativeDataService
              .findById(data.getKpiDataId());
          quantitativeKpiData.setActuals(Integer.valueOf(data.getValue()));
          quantitativeKpiData.setUpdatedAt(DateTime.now().toDate());
          quantitativeKpiData.setUpdatedBy(user.getEmailId());
          //quantitativeKpiData.setStatusName(workflowStatusService.findById(data.getToStatusId()).getName());
          quantitativeDataService.saveData(quantitativeKpiData);
          break;
        case "QUALITATIVE":
          GrantQualitativeKpiData qualitativeKpiData = qualitativeDataService
              .findById(data.getKpiDataId());
          qualitativeKpiData.setActuals(data.getValue());
          qualitativeKpiData.setCreatedAt(DateTime.now().toDate());
          qualitativeKpiData.setUpdatedBy(user.getEmailId());
          qualitativeDataService.saveData(qualitativeKpiData);
          break;
        case "DOCUMENT":
          GrantDocumentKpiData documentKpiData = grantDocumentDataService
              .findById(data.getKpiDataId());
          if (documentKpiData.getActuals() != null && (data.getFileName() == null || data
              .getFileName().equalsIgnoreCase(""))) {
            documentKpiData.setActuals(documentKpiData.getActuals());
          } else {
            String fileName = uploadLocation + data.getFileName();
            documentKpiData.setActuals(data.getFileName());
            try {
              FileOutputStream fileOutputStream = new FileOutputStream(fileName);
              byte[] dataBytes = Base64.getDecoder().decode(data.getValue());
              fileOutputStream.write(dataBytes);
              fileOutputStream.close();
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }
            documentKpiData.setType(data.getFileType());
          }

          documentKpiData.setUpdatedAt(DateTime.now().toDate());
          documentKpiData.setUpdatedBy(user.getEmailId());
          grantDocumentDataService.saveDocumentKpi(documentKpiData);
          break;
      }
    }
    GrantQuantitativeKpiData quantitativeKpiData = quantitativeDataService
        .findById(submissionData.get(0).getKpiDataId());

    Submission submission = quantitativeKpiData.getSubmission();
    submission.setSubmittedOn(DateTime.now().toDate());
    submission
        .setSubmissionStatus(workflowStatusService.findById(submissionData.get(0).getToStatusId()));

    submission = submissionService.saveSubmission(submission);

    List<User> usersToNotify = userService
        .usersToNotifyOnSubmissionStateChangeTo(submission.getSubmissionStatus().getId());

    for (User userToNotify : usersToNotify) {
      commonEmailSevice.sendMail(userToNotify.getEmailId(), appConfigService
              .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                  AppConfiguration.SUBMISSION_ALTER_MAIL_SUBJECT).getConfigValue(),
          submissionService.buildMailContent(submission, appConfigService
              .getAppConfigForGranterOrg(submission.getGrant().getGrantorOrganization().getId(),
                  AppConfiguration.SUBMISSION_ALTER_MAIL_CONTENT).getConfigValue()));
    }

    Grant grant = submission.getGrant();
    grant.setSubstatus(submission.getSubmissionStatus());
    grant = grantService.saveGrant(grant);
    GrantVO grantVO = new GrantVO()
        .build(grant, workflowPermissionService, user,
            appConfigService.getAppConfigForGranterOrg(grant.getGrantorOrganization().getId(),
                AppConfiguration.KPI_SUBMISSION_WINDOW_DAYS));
    return grantVO;
  }
}
