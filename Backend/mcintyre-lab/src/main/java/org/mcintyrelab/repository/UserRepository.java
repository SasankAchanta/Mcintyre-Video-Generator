package org.mcintyrelab.repository;

import org.mcintyrelab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Integer> {
    boolean existsByUsername(String username);
}
