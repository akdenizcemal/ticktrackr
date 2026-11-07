package com.cemal.tickrackr;

import com.cemal.tickrackr.client.ElancoClient;
import com.cemal.tickrackr.service.IngestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TickrackrApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickrackrApplication.class, args);
	}

	@Bean
	CommandLineRunner ingestData(ElancoClient client, IngestService ingestService) {
		return args -> {

			var records = client.fetchAll();

			System.out.println("FETCHED: " + records.size());

			ingestService.ingest(records);

			System.out.println("INGESTION COMPLETE");
		};
	}
}