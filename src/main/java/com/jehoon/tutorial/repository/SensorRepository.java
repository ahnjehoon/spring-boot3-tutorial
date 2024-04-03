package com.jehoon.tutorial.repository;

import com.jehoon.tutorial.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
