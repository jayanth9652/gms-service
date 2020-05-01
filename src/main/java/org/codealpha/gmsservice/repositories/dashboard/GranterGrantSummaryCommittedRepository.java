package org.codealpha.gmsservice.repositories.dashboard;

import org.codealpha.gmsservice.entities.dashboard.GranterGrantSummaryCommitted;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GranterGrantSummaryCommittedRepository extends CrudRepository<GranterGrantSummaryCommitted,Long> {

    @Query(value="select * from (SELECT row_number() OVER () as id,a.grantor_org_id AS granter_id, b.internal_status, count(a.*) AS grant_count, min(a.start_date) AS period_start, max(a.end_date) AS period_end, sum(a.amount) AS committed_amount FROM grants a JOIN workflow_statuses b ON b.id = a.grant_status_id GROUP BY a.grantor_org_id, b.internal_status) X  where X.granter_id=?1 and X.internal_status=?2",nativeQuery = true)
    public GranterGrantSummaryCommitted getGrantCommittedSummaryForGranter(Long granterId,String status);
}