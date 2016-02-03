package io.pivotal.pa.rabbitmq.jms.raw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("usage")
@Component
public class UsageMessageRunner implements CommandLineRunner {

	@Override
	public void run(String... arg0) throws Exception {
		System.out.println("Usage:");
	}

}