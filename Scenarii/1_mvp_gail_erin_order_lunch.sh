# -- Hostnames ---
orderService="localhost:9555"
restaurant="localhost:9777"
coursierservice="localhost:9888"
# -- Context ---
echo "Scenario#1: As Gail or Erin, I can order my lunch from a restaurant so that the food is delivered to my place"
echo ""
echo "--- Creating context... ---"
# Creating gail
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"lastName\":\"Oho\",\"firstName\":\"Gail\"}" "http://$orderService/users" > temp/1/0_gailId.txt
gail_id=$(grep -Po '"id": *\K[^,]*' temp/1/0_gailId.txt | head -1)
# Create a restaurant POST /restaurants
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"name\":\"Asiakeo\",\"restaurantAddress\":\"407 ch. de l'oued\",\"meals\":[]}" "http://$restaurant/restaurants" > temp/1/1_restaurantCreated.txt
restaurant_id=$(grep -Po '"id": *\K[^,]*' temp/1/1_restaurantCreated.txt | head -1)
# Create a ramen meal POST /restaurants/{restaurantId}/meals
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{\"id\":-1,\"name\":\"Ramen\",\"price\":3,\"ingredients\":[\"pork\"],\"tags\":[\"Asian\"]}" "http://$restaurant/restaurants/$restaurant_id/meals" > temp/1/2_mealCreated.txt
sleep 2
echo "--- Context created ---"
echo ""
# ************** Scenario **************
echo "*******1- As Gail or Erin, I search the list of the restaurants"
echo "Press any key to continue..."
read
# Récupère tous les restaurants disponible :
curl -X GET --silent "http://$orderService/restaurants" > temp/1/3_resultOfRestaurants.txt
echo "Result (temp/1/3_resultOfRestaurants.txt):"
cat temp/1/3_resultOfRestaurants.txt
echo ""
echo ""
echo "Press any key to continue..."
read

# Récupère tous les plats d'un restaurant spécifiques :
echo "*******2- As Gail or Erin, I decide to look for the meals of a specific restaurant"
echo "Press any key to continue..."
read
restau_id=$(grep -Po '"id": *\K[^,]*' temp/1/3_resultOfRestaurants.txt | tail -1)
curl -X GET --silent "http://$orderService/restaurants/$restau_id/meals" > temp/1/4_resultOfMeals.txt
echo "Result (temp/1/4_resultOfMeals.txt):"
cat temp/1/4_resultOfMeals.txt
echo ""
echo ""
echo "Press any key to continue..."
read

# Choisi son plat et envoit sa commande :
echo "*******3- As Gail or Erin, I choose my meal and send it to the system"
echo "Press any key to continue..."
read
curl -X POST --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "{ \"id\": -1, \"meals\": [ {\"name\":\"Ramen\",\"tags\":[\"Asian\"],\"restaurant\":{\"id\":$restau_id,\"name\":\"Asiakeo\",\"restaurantAddress\":\"690 Route de Grasse, 06600 Antibes\"}} ], \"transmitter\": { \"id\": \"$gail_id\", \"lastName\":\"Oho\",\"firstName\":\"Gail\" }, \"deliveryAddress\": \"930 Route des Colles, 06410 Biot\", \"eta\": null, \"state\": \"WAITING\", \"restaurant\":{\"id\":$restau_id,\"name\":\"Asiakeo\",\"restaurantAddress\":\"690 Route de Grasse, 06600 Antibes\"} }" "http://$orderService/orders" > temp/1/5_orderWithETA.txt
echo "*******The system show me the ETA"
echo "Result (temp/1/5_orderWithETA.txt):"
cat temp/1/5_orderWithETA.txt
echo ""
echo ""
echo "Press any key to continue..."
read

# Acception de l'ETA :
echo "*******4- As Gail or Erin, I decide to accept the ETA"
echo "Press any key to continue..."
read
sed -i 's/WAITING/VALIDATED/g' temp/1/5_orderWithETA.txt
order_id=$(grep -Po '"id": *\K[^,]*' temp/1/5_orderWithETA.txt | head -1)
# Envoi au système de la commande acceptée, le système poste un message dans le bus pour le restaurant :
curl -X PUT --silent -H "Content-Type:application/JSON; charset=UTF-8" -d "$(tail -1 temp/1/5_orderWithETA.txt)" "http://$orderService/orders/$order_id" > temp/1/6_validatedOrder.txt
echo ""
echo ""
echo "Press any key to continue..."
read