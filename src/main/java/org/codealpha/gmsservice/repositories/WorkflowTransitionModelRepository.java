package org.codealpha.gmsservice.repositories;

import org.codealpha.gmsservice.entities.WorkflowTransitionModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkflowTransitionModelRepository  extends CrudRepository<WorkflowTransitionModel, Long> {

    @Query(value = "select wst.id,wst.action,wst.from_state_id, (select display_name from workflow_statuses where id = wst.from_state_id) _from, wst.to_state_id, (select display_name from workflow_statuses where id = wst.to_state_id) _to, wst.role_id, (select name from roles where id = wst.role_id) _performedby from workflow_status_transitions wst inner join workflows w on wst.workflow_id = w.id where w.granter_id=?1 and w.object=?2",nativeQuery = true)
    public List<WorkflowTransitionModel> getWorkflowsByGranterAndObject(Long granterId, String object);
}
