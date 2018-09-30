package fr.unice.polytech.si5.soa.a.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Class name	CommandDTO
 * Date			30/09/2018
 * @author		PierreRainero
 */
@Data
@EqualsAndHashCode()
@ToString()
public class CommandDTO implements Serializable {
	/**
	 * Generated UID version
	 */
	private static final long serialVersionUID = 5044196469861617600L;
	
	private List<MealDTO> meals = new ArrayList<>();
	private UserDTO transmitter;
	private String deliveryAddress;
	
	/**
	 * Default constructor
	 */
	public CommandDTO() {
		// Default constructor for Jackson databinding
	}
	
	/**
	 * Normal constructor 
	 * @param meals list of meals (DTO)
	 */
	public CommandDTO(List<MealDTO> meals, UserDTO transmitter, String deliveryAddress) {
		this.meals = meals;
		this.transmitter = transmitter;
		this.deliveryAddress = deliveryAddress;
	}
}
