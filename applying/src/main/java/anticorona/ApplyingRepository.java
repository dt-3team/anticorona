package anticorona;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="applyings", path="applyings")
public interface ApplyingRepository extends PagingAndSortingRepository<Applying, Long>{


}
