package fr.unice.polytech.si5.soa.a.entities;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.springframework.lang.NonNull;

import fr.unice.polytech.si5.soa.a.communication.RestaurantOrderDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Class name	RestaurantOrder
 * Date			08/10/2018
 * @author		PierreRainero
 * 
 * @version		1.1
 * Date			23/10/2018
 */
@Entity
@Data
@Table(name = "`RESTAURANTORDER`")
@EqualsAndHashCode(exclude={"id"})
@ToString()
public class RestaurantOrder implements Serializable {
	/**
	 * Generated UID version 
	 */
	private static final long serialVersionUID = 6675013421945330865L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	@Setter(NONE)
	private int id;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private List<Meal> meals = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private OrderState state = OrderState.TO_PREPARE;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@NonNull
	private Restaurant restaurant;
	
	@Column(name = "validationDate")
	private Date validationDate;
	
	public RestaurantOrder() {
		// Default constructor for JPA
	}
	
	public RestaurantOrder(RestaurantOrderDTO data) {
		this.state = data.getState();
		this.validationDate = data.getValidationDate();
	}
	
	public RestaurantOrderDTO toDTO() {
		return new RestaurantOrderDTO(id, meals.stream().map(meal -> meal.toDTO()).collect(Collectors.toList()), state, restaurant.toDTO(), validationDate);
	}

	public void addMeal(Meal meal){
        meals.add(meal);
    }

    public void removeMeal(Meal meal){
        meals.remove(meal);
    }
}
