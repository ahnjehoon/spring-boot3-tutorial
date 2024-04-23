package com.jehoon.tutorial.controller;

import com.jehoon.tutorial.dto.SensorSelectRequest;
import com.jehoon.tutorial.entity.Sensor;
import com.jehoon.tutorial.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping
    public List<Sensor> select(@ParameterObject SensorSelectRequest param) {
        return sensorService.select(param);
    }

    @PostMapping
    public Sensor create(@RequestBody Sensor param) {
        return sensorService.create(param);
    }

    @PutMapping
    public Sensor update(@RequestBody Sensor param) {
        return sensorService.update(param);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable("id") Long param) {
        return sensorService.delete(param);
    }
}
