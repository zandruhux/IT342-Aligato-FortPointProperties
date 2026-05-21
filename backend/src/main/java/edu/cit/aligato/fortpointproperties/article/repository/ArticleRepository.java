package edu.cit.aligato.fortpointproperties.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.cit.aligato.fortpointproperties.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    @Query("SELECT a FROM Article a ORDER BY COALESCE(a.updatedAt, a.createdAt) DESC")
    List<Article> findAllOrderByLatestDateDesc();

    @Query("""
            SELECT a FROM Article a
            WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
            ORDER BY COALESCE(a.updatedAt, a.createdAt) DESC
            """)
    List<Article> findByTitleContainingIgnoreCaseOrderByLatestDateDesc(@Param("title") String title);
}
