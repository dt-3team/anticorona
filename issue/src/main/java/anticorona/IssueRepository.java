package anticorona;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="issues", path="issues")
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long>{

    Optional<Issue> findByBookingId(Long bookingId);
    
}
