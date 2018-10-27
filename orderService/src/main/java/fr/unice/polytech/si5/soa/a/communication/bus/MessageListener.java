package fr.unice.polytech.si5.soa.a.communication.bus;

import fr.unice.polytech.si5.soa.a.exceptions.UnknowPaymentException;
import fr.unice.polytech.si5.soa.a.services.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

/**
 * Class MessageListener
 *
 * @author Joël CANCELA VAZ
 */
public class MessageListener {

	private CountDownLatch latch = new CountDownLatch(3);

	@Autowired
	private IPaymentService paymentService;

	@KafkaListener(topics = "${bank.topic.name}", containerFactory = "bankContainerFactory")
	public void listenPaymentConfirmation(PaymentConfirmation message) {
		System.out.println("Received payment confirmation': " + message.getId());
		try {
			paymentService.updatePayment(message);
		} catch (UnknowPaymentException e) {
			System.out.println("Problem while processing payment confirmation with id "+message.getId());
		}
		latch.countDown();
	}

	@KafkaListener(topics = "${message.topic.name}", containerFactory = "topicKafkaListenerContainerFactory")
	public void listenGroupOrder(Message message) {
		System.out.println("Received Message in group 'order': " + message);
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}
}
