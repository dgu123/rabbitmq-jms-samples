package io.pivotal.pa.rabbitmq.jms.raw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("usage")
@Component
public class UsageMessageRunner implements CommandLineRunner {

	//TODO: Show contents of help file
	@Override
	public void run(String... arg0) throws Exception {
		System.out.println("Parameters:");
		System.out.println("--spring.profiles.active=[send | consume | publish | subscribe]");
		System.out.println("--amqp.uri=[<uri>] (overrides amqp.host, etc)");
		System.out.println("--amqp.host=[localhost | <host>]");
		System.out.println("--amqp.username=[guest | <username>]");
		System.out.println("--amqp.password=[guest | <password>]");
		System.out.println("--amqp.port=[5672 | <port>]");
		System.out.println("--amqp.vhost=[/ | <vhost>]");
		System.out.println("--amqp.exchange=[<exchange>] (messages go to the specified exchange)");
		System.out.println("--amqp.queue=[<queue>] (listeners attach to the specified queue)");
		System.out.println("--amqp.ssl=[true | false]");
		System.out.println("--jms.ack=[AUTO_ACKNOWLEDGE | CLIENT_ACKNOWLEDGE | DUPS_OK_ACKNOWLEDGE | SESSION_TRANSCTION]" );
		System.out.println("--jms.queue=[test.queue | <queue>]");
		System.out.println("--jms.topic=[test.topic | <topic>]");
		System.out.println("--jms.durable-queue=[<durable queue name>] (turns on use of durable subscriber)");
		System.out.println("--jms.persistent=[false | <true|false>]");
		System.out.println("--jms.priority=[<0-9>]");
		System.out.println("--jms.ttl=[<time-to-live>]");
		System.out.println("--message=[default message | <message>]");
		System.out.println("--message-size=[-1 | <n>] (in characters.  n less than 0 prints default message)");
		System.out.println("--delay=[0 | <delay>] (in milliseconds)");
		System.out.println("--nummessages=[1 | <nummessages>]");
		System.out.println("--batchsize=[<batchsize>] (turns on transactionality for senders)");
		System.out.println("--counter=[true | <true|false> ] (turns on/off display of counter in consumer and prepending of counter in senders)");
		System.out.println("--poison.send-percent=[0 | <0-100>] (>0 turns it on)");
		System.out.println("--poison.message=[default | <message>]");
		System.out.println("--poison.try-limit=[0 | <n>] (>0 turns monitoring for poison on)");
		System.out.println("--poison.backout-queue=[backout.queue | <backout-queue>]");
	}
	
}
