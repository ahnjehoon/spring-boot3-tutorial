package com.jehoon.tutorial.controller;

import com.jehoon.tutorial.entity.Sensor;
import com.jehoon.tutorial.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/sensor")
@RequiredArgsConstructor
public class SensorController {
    private final SensorService sensorService;

    @GetMapping
    public List<Sensor> select() {
        return sensorService.select();
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
