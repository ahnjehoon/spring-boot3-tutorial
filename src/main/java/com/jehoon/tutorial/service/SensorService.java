package com.jehoon.tutorial.service;

import com.jehoon.tutorial.dto.SensorSelectRequest;
import com.jehoon.tutorial.entity.Sensor;
import com.jehoon.tutorial.repository.SensorQueryRepository;
import com.jehoon.tutorial.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final SensorQueryRepository sensorQueryRepository;

    public List<Sensor> select(SensorSelectRequest param) {
        return sensorQueryRepository.select(param);
    }

    public Sensor create(Sensor param) {
        return sensorRepository.save(param);
    }

    public Sensor update(Sensor param) {
        return sensorRepository.save(param);
    }

    public boolean delete(Long param) {
        sensorRepository.deleteById(param);
        return true;
    }
}
