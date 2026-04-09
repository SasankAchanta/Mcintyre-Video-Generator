package org.mcintyrelab.repository;

import org.mcintyrelab.dto.UntrainedResponse;
import org.mcintyrelab.model.Untrained;
import org.mcintyrelab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UntrainedRepository extends JpaRepository<Untrained, Integer> {

    boolean existsByVideoUrl(String videoUrl);

    List<UntrainedResponse> findByUserUserId(Integer userId);
}
