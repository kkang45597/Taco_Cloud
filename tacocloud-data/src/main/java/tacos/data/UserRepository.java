package tacos.data;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import tacos.User;

@RepositoryRestController(path="/users") //추가
public interface UserRepository extends CrudRepository<User, Long> {

  User findByUsername(String username);
  
}
