package com.openclaw.deploy.repository;

import com.openclaw.deploy.entity.DeployOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeployOrderRepository extends JpaRepository<DeployOrder, Long> {
    List<DeployOrder> findByEmail(String email);
    List<DeployOrder> findByStatus(String status);
}