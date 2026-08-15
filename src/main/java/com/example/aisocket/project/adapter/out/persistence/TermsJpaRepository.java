package com.example.aisocket.project.adapter.out.persistence;

import com.example.aisocket.project.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TermsJpaRepository extends JpaRepository<Terms, Long> {

    List<Terms> findByActiveTrueOrderByRequiredDescIdAsc();

    List<Terms> findByActiveTrueAndRequiredTrueOrderByIdAsc();

    Optional<Terms> findByCodeAndActiveTrue(String code);

    List<Terms> findByIdInAndActiveTrue(Collection<Long> ids);
}
