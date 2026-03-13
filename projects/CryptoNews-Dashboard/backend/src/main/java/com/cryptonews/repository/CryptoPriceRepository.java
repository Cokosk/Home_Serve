package com.cryptonews.repository;

import com.cryptonews.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    
    List<CryptoPrice> findBySymbolOrderByRecordedAtDesc(String symbol);
    
    Optional<CryptoPrice> findTopBySymbolOrderByRecordedAtDesc(String symbol);
    
    List<CryptoPrice> findBySymbolAndRecordedAtAfterOrderByRecordedAtAsc(String symbol, LocalDateTime since);
    
    void deleteByRecordedAtBefore(LocalDateTime time);
}