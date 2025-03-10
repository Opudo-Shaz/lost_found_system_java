package com.example.lostandfound.repository;

import com.example.lostandfound.entity.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    // Custom query methods can be added here

    @Query("SELECT l FROM LostItem l WHERE l.name LIKE %?1% "
            + "OR l.description LIKE %?1% "
            + "OR l.category LIKE %?1% "
            + "OR CONCAT(l.dateLost, '') LIKE %?1%")
    List<LostItem> searchByKeyword(String keyword);
}
