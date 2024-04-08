package com.jehoon.tutorial.repository;

import com.jehoon.tutorial.dto.SensorSelectRequest;
import com.jehoon.tutorial.entity.QSensor;
import com.jehoon.tutorial.entity.Sensor;
import com.jehoon.tutorial.expression.SensorExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SensorQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Sensor> select(SensorSelectRequest param) {
        var sensor = QSensor.sensor;
        var sensorQueryExpression = new SensorExpression(sensor);
        return jpaQueryFactory
                .selectFrom(sensor)
                .where(
                        sensorQueryExpression.inId(param.getId()),
                        sensorQueryExpression.inCode(param.getCode()),
                        sensorQueryExpression.inType(param.getType()),
                        sensorQueryExpression.likeName(param.getLikeName())
                )
                .fetch();
    }
}
