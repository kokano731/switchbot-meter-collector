package com.example.switchbotmetercollector.model;

// exec_time,device_name,temperature,humidity
// 2022-07-30 00:04:06,north_room,27.4,57
public record Meter (
    String id, String exec_time, String device_name, Float temperature, Integer humidity ) {
};