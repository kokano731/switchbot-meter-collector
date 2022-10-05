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

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		@RequestParam("humidity") Integer humidity,
		@RequestParam("dt") String dt
	) throws IOException, ExecutionException, InterruptedException {

		String id = UUID.randomUUID().toString().substring(0, 8);

		String projectId = "switch-bot-320517";
		String topicId = "myTopic";

		Meter meter = new Meter(id, exec_time, device_name, temperature, humidity, dt);
		meterItems.add(meter);

		ObjectMapper mapper = new ObjectMapper();
		String messageJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(meter);
		System.out.println(messageJson);

		TopicName topicName = TopicName.of(projectId, topicId);

		Publisher publisher = null;

		try {
			// Create a publisher instance with default settings bound to the topic
			publisher = Publisher.newBuilder(topicName).build();

			ByteString data = ByteString.copyFromUtf8(messageJson);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

			// Once published, returns a server-assigned message id (unique within the topic)
			ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
			String messageId = messageIdFuture.get();
			System.out.println("Published message ID: " + messageId);
		} finally {
			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				publisher.shutdown();
				publisher.awaitTermination(1, TimeUnit.MINUTES);
			}
		}
		
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