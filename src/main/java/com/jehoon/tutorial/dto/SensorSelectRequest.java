package com.jehoon.tutorial.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorSelectRequest {
    private Long[] id;
    private String[] code;
    private String[] type;
    private String likeName;
}
