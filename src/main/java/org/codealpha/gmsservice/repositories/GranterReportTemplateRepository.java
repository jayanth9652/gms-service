package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.GranterReportTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GranterReportTemplateRepository extends CrudRepository<GranterReportTemplate,Long> {
    public GranterReportTemplate findByGranterIdAndDefaultTemplate(Long granterId,Boolean flag);
    public List<GranterReportTemplate> findByGranterId(Long granterId);
    public List<GranterReportTemplate> findByGranterIdAndPublished(Long granterId,Boolean publishedStatus);
}