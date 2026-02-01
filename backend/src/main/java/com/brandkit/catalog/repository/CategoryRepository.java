package com.brandkit.catalog.repository;

import com.brandkit.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Category Repository
 * FRD-002 FR-14: Category Structure
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    Optional<Category> findBySlug(String slug);
    
    Optional<Category> findByName(String name);
    
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    boolean existsBySlug(String slug);
    
    boolean existsByName(String name);
}
