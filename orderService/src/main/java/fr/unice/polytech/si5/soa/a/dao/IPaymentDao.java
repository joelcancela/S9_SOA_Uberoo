package fr.unice.polytech.si5.soa.a.dao;

import java.util.Optional;

import fr.unice.polytech.si5.soa.a.entities.Payment;

/**
 * Class name	IPaymentDAO
 * Date			22/10/2018
 * @author 		PierreRainero
 *
 */
public interface IPaymentDao {
	/**
	 * Add an {@link Payment} into the database
	 * @param paymentToAdd payment to add
	 * @return the saved payment
	 */
	Payment addPayment(Payment paymentToAdd);
	
	/**
	 * Search a payment in the database using his id
	 * @param idToSearch id to use
	 * @return the payment wrapped in an {@link Optional} if the payment exists, Optional.empty() otherwise
	 */
	Optional<Payment> findPaymentById(int idToSearch);
}