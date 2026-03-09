package com.tus.pcmanager.repository;

import com.tus.pcmanager.model.PcBuild;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PcBuildRepository extends JpaRepository<PcBuild, Long> {
    List<PcBuild> findByUserId(Long userId);
}