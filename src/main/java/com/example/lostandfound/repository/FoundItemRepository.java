package com.example.lostandfound.repository;

import com.example.lostandfound.entity.FoundItem;
import com.example.lostandfound.enums.FoundItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {

    @Query("SELECT f.id, f.name, f.finderEmail FROM FoundItem f WHERE f.id = :id")
    Object[] findItemDetailsById(Long id);

    // Custom query method to search by query string in name, category, or description
    @Query("SELECT f FROM FoundItem f WHERE f.name LIKE %?1% "
            + "OR f.description LIKE %?1% "
            + "OR f.category LIKE %?1% "
            + "OR CONCAT(f.dateFound, '') LIKE %?1%")
    List<FoundItem> searchByKeyword(String keyword);

    List<FoundItem> findByStatus(FoundItemStatus status);


}