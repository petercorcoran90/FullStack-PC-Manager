package com.tus.pcmanager.repository;

import com.tus.pcmanager.model.HardwarePart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HardwarePartRepository extends JpaRepository<HardwarePart, Long> {
    List<HardwarePart> findByNameContainingIgnoreCase(String keyword);
    List<HardwarePart> findByCategory(String category);
    boolean existsByNameIgnoreCase(String name);
}