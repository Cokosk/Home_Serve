package com.cryptonews.repository;

import com.cryptonews.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    Page<News> findBySentimentLabel(String sentimentLabel, Pageable pageable);
    
    Page<News> findByRelatedCoinsContaining(String coin, Pageable pageable);
    
    Page<News> findByRelatedCoinsContainingOrderByPublishedAtDesc(String coin, Pageable pageable);
    
    List<News> findByPublishedAtAfterOrderByPublishedAtDesc(LocalDateTime since);
    
    @Query("SELECT n.sentimentLabel, COUNT(n) FROM News n WHERE n.publishedAt > :since GROUP BY n.sentimentLabel")
    List<Object[]> countBySentimentSince(LocalDateTime since);
    
    @Query("SELECT DATE(n.publishedAt) as date, AVG(n.sentiment) as avgSentiment FROM News n WHERE n.publishedAt > :since GROUP BY DATE(n.publishedAt) ORDER BY date")
    List<Object[]> getSentimentTrend(LocalDateTime since);
    
    boolean existsByUrl(String url);
}