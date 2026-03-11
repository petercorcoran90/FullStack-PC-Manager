package com.tus.pcmanager.repository;

import com.tus.pcmanager.dto.CategoryStockDTO;
import com.tus.pcmanager.model.HardwarePart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface HardwarePartRepository extends JpaRepository<HardwarePart, Long> {
    List<HardwarePart> findByNameContainingIgnoreCase(String keyword);
    List<HardwarePart> findByCategory(String category);
    boolean existsByNameIgnoreCase(String name);
    
    @Query("SELECT new com.tus.pcmanager.dto.CategoryStockDTO(h.category, SUM(h.stockLevel)) FROM HardwarePart h GROUP BY h.category")
    List<CategoryStockDTO> getStockPerCategory();
}