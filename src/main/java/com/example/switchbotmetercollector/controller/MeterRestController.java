package com.example.switchbotmetercollector.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.switchbotmetercollector.model.*;

@RestController
public class MeterRestController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	private List<Meter> meterItems = new ArrayList<>();

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@PostMapping("/meter")
    public String addMeter(
		@RequestParam("exec_time") String exec_time,
		@RequestParam("device_name") String device_name,
		@RequestParam("temperature") Float temperature,
		@RequestParam("humidity") Integer humidity
	) {
		String id = UUID.randomUUID().toString().substring(0, 8);
        Meter meter = new Meter(id, exec_time, device_name, temperature, humidity);
        meterItems.add(meter);
		
		return "add meter\n";
	}

	@GetMapping("/meter")
    String listMeterItems() {
        String result = meterItems.stream()
                .map(Meter::toString)
                .collect(Collectors.joining(", "));
        return result;
    }
}