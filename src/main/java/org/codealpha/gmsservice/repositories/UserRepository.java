package org.codealpha.gmsservice.repositories;

import java.util.List;
import org.codealpha.gmsservice.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  public User findByEmailId(String email);

  @Query(value = "select distinct u.* from workflow_status_transitions A inner join roles r on A.role_id = r.id inner join users u on r.id = u.role_id where A.from_state_id=?1",nativeQuery = true)
  public List<User> usersToNotifyOnSubmissionSateChangeTo(Long toStateId);


}