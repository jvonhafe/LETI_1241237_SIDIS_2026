package com.aisafe.repository;

import com.aisafe.model.MaintenanceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceTemplateRepository extends JpaRepository<MaintenanceTemplate, String> {
}
