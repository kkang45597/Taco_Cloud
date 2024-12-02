package tacos.restclient;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.hateoas.Resources;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Taco;

@Service
@Slf4j
public class TacoCloudClient {
	private RestTemplate rest;
	private Traverson traverson;
	private String jwtToken;

	public TacoCloudClient(RestTemplate rest, Traverson traverson) {
		this.rest = rest;
		this.traverson = traverson;
	}

	//
	// GET examples
	//
	//로그인하여 JWT 토큰을 가져오는 메서드
	public void login(String username, String password) {
		String loginUrl = "http://localhost:8080/login";
   
		// 요청 본문에 사용자 자격 증명 추가
		Map<String, String> credentials = new HashMap<>();
		credentials.put("username", username);
		credentials.put("password", password);
   
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);
   
		// 서버에 로그인 요청 보내기
		ResponseEntity<Map> response = rest.postForEntity(loginUrl, request, Map.class);
   
		// JWT 토큰 추출
		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			jwtToken = (String) response.getBody().get("token");  // 서버 응답이 "token" 키를 통해 토큰을 반환한다고 가정
			log.info("JWT Token: " + jwtToken);
		} else {
			log.error("Failed to login and retrieve JWT token.");
		}
	}

  
	/*
	 * Specify parameter as varargs argument
	*/
	public Ingredient getIngredientById(String ingredientId) {
		return rest.getForObject("http://localhost:8080/ingredients/{id}",
                             Ingredient.class, ingredientId);
	}
	
	public Ingredient getIngredientByIdWithToken(String ingredientId) {
	    // HTTP 요청 헤더에 Authorization 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);

	    HttpEntity<String> request = new HttpEntity<>(headers);

	    // JWT 토큰이 포함된 요청을 보내도록 수정
	    ResponseEntity<Ingredient> response = rest.exchange(
	        "http://localhost:8080/ingredients/{id}",
	        HttpMethod.GET,
	        request,
	        Ingredient.class,
	        ingredientId
	    );

	    return response.getBody();
	}

	/*
	 * Alternate implementations...
	 * The next three methods are alternative implementations of
	 * getIngredientById() as shown in chapter 6. If you'd like to try
	 * any of them out, comment out the previous method and uncomment
	 * the variant you want to use.
	 */

	/*
	 * Specify parameters with a map
	*/
	// public Ingredient getIngredientById(String ingredientId) {
	//   Map<String, String> urlVariables = new HashMap<>();
	//   urlVariables.put("id", ingredientId);
	//   return rest.getForObject("http://localhost:8080/ingredients/{id}",
	//       Ingredient.class, urlVariables);
	// }

	/*
	 * Request with URI instead of String
    */
	// public Ingredient getIngredientById(String ingredientId) {
	//   Map<String, String> urlVariables = new HashMap<>();
	//   urlVariables.put("id", ingredientId);
	//   URI url = UriComponentsBuilder
	//             .fromHttpUrl("http://localhost:8080/ingredients/{id}")
	//             .build(urlVariables);
	//   return rest.getForObject(url, Ingredient.class);
	// }

	/*
     * Use getForEntity() instead of getForObject()
     */
	// public Ingredient getIngredientById(String ingredientId) {
	//   ResponseEntity<Ingredient> responseEntity =
	//       rest.getForEntity("http://localhost:8080/ingredients/{id}",
	//           Ingredient.class, ingredientId);
	//   log.info("Fetched time: " +
	//           responseEntity.getHeaders().getDate());
	//   return responseEntity.getBody();
	// }

	public List<Ingredient> getAllIngredients() {
		return rest.exchange("http://localhost:8080/ingredients",
				HttpMethod.GET, 
				null, 
				new ParameterizedTypeReference<List<Ingredient>>() {})
				.getBody();
	}
  
	public List<Ingredient> getAllIngredientsWithToken() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);
	    HttpEntity<String> request = new HttpEntity<>(headers);

	    return rest.exchange("http://localhost:8080/ingredients",
	            HttpMethod.GET,
	            request,
	            new ParameterizedTypeReference<List<Ingredient>>() {})
	            .getBody();
	}

	//
	// PUT examples
	//
	public void updateIngredient(Ingredient ingredient) {
		rest.put("http://localhost:8080/ingredients/{id}",
				ingredient, ingredient.getId());
	}
	
	public void updateIngredientWithToken(Ingredient ingredient) {
	    // HTTP 요청 헤더에 Authorization 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);
	    headers.set("Content-Type", "application/json"); // JSON 형식 설정

	    HttpEntity<Ingredient> request = new HttpEntity<>(ingredient, headers);

	    // JWT 토큰이 포함된 요청을 보내도록 수정
	    rest.exchange(
	        "http://localhost:8080/ingredients/{id}",
	        HttpMethod.PUT,
	        request,
	        Void.class,
	        ingredient.getId()
	    );
	}


	//
	// POST examples
	//
	public Ingredient createIngredient(Ingredient ingredient) {
		return rest.postForObject("http://localhost:8080/ingredients",
				ingredient, Ingredient.class);
	}
	
	public Ingredient createIngredientWithToken(Ingredient ingredient) {
	    // HTTP 요청 헤더에 Authorization 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);
	    headers.set("Content-Type", "application/json"); // JSON 형식 설정

	    HttpEntity<Ingredient> request = new HttpEntity<>(ingredient, headers);

	    // JWT 토큰이 포함된 요청을 보내도록 수정
	    ResponseEntity<Ingredient> response = rest.exchange(
	        "http://localhost:8080/ingredients",
	        HttpMethod.POST,
	        request,
	        Ingredient.class
	    );

	    return response.getBody();
	}


	/*
	 * Alternate implementations...
	 * The next two methods are alternative implementations of
	 * createIngredient() as shown in chapter 6. If you'd like to try
	 * any of them out, comment out the previous method and uncomment
	 * the variant you want to use.
	*/

	// public URI createIngredient(Ingredient ingredient) {
	//   return rest.postForLocation("http://localhost:8080/ingredients",
	//       ingredient, Ingredient.class);
	// }

	// public Ingredient createIngredient(Ingredient ingredient) {
	//   ResponseEntity<Ingredient> responseEntity =
	//          rest.postForEntity("http://localhost:8080/ingredients",
	//                             ingredient,
	//                             Ingredient.class);
	//   log.info("New resource created at " +
	//            responseEntity.getHeaders().getLocation());
	//   return responseEntity.getBody();
	// }

	//
	// DELETE examples
	//
	public void deleteIngredient(Ingredient ingredient) {
		log.info("deleteIngredient: " + ingredient);
		rest.delete("http://localhost:8080/ingredients/{id}",
        ingredient.getId());
	}
	
	public void deleteIngredientWithToken(Ingredient ingredient) {
	    log.info("deleteIngredient: " + ingredient);

	    // HTTP 요청 헤더에 Authorization 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);

	    HttpEntity<String> request = new HttpEntity<>(headers);

	    // JWT 토큰이 포함된 요청을 보내도록 수정
	    rest.exchange(
	        "http://localhost:8080/ingredients/{id}",
	        HttpMethod.DELETE,
	        request,
	        Void.class,
	        ingredient.getId()
	    );
	}


	//
	// Traverson with RestTemplate examples
	//
	public Iterable<Ingredient> getAllIngredientsWithTraverson() {
		ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType =
        new ParameterizedTypeReference<CollectionModel<Ingredient>>() {};

        CollectionModel<Ingredient> ingredientRes = traverson
        		.follow("ingredients")
        		.toObject(ingredientType);
    
        Collection<Ingredient> ingredients = ingredientRes.getContent();
          
        return ingredients;
	}
	
	public Iterable<Ingredient> getAllIngredientsWithTraversonWithToken() {
		
	    ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType =
	        new ParameterizedTypeReference<CollectionModel<Ingredient>>() {};

	    // HTTP 요청 헤더에 Authorization 추가
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + jwtToken);
	    // JWT 토큰을 포함한 요청을 설정
	    CollectionModel<Ingredient> ingredientRes = traverson
	        .follow("ingredients")
	        .withHeaders(headers)
	        .toObject(ingredientType);

	    Collection<Ingredient> ingredients = ingredientRes.getContent();

	    return ingredients;
	}



	public Ingredient addIngredient(Ingredient ingredient) {
		String ingredientsUrl = traverson
				.follow("ingredients")
				.asLink()
				.getHref();
		return rest.postForObject(ingredientsUrl,
				ingredient,
                Ingredient.class);
	}
	
	public Ingredient addIngredientWithToken(Ingredient ingredient) {
	    String ingredientsUrl = traverson
	        .follow("ingredients")
	        .asLink()
	        .getHref();

	    // HTTP 요청 헤더에 Authorization 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + jwtToken);
	    headers.set("Content-Type", "application/json");

	    HttpEntity<Ingredient> request = new HttpEntity<>(ingredient, headers);

	    // JWT 토큰이 포함된 요청을 보내도록 수정
	    ResponseEntity<Ingredient> response = rest.exchange(
	        ingredientsUrl,
	        HttpMethod.POST,
	        request,
	        Ingredient.class
	    );

	    return response.getBody();
	}
	
	public Iterable<Taco> getRecentTacosWithTraverson() {
		ParameterizedTypeReference<CollectionModel<Taco>> tacoType =
        new ParameterizedTypeReference<CollectionModel<Taco>>() {};

        CollectionModel<Taco> tacoRes = traverson
        		.follow("tacos")
        		.follow("recents")
        		.toObject(tacoType);

        // Alternatively, list the two paths in the same call to follow()
        //    Resources<Taco> tacoRes =
        //        traverson
        //          .follow("tacos", "recents")
        //          .toObject(tacoType);

        return tacoRes.getContent();
        
	}


	public Iterable<Taco> getRecentTacosWithTraversonWithToken() {
		ParameterizedTypeReference<EntityModel<Taco>> tacoType =
		        new ParameterizedTypeReference<EntityModel<Taco>>() {};

        // HTTP 요청 헤더에 Authorization 추가
 		HttpHeaders headers = new HttpHeaders();
 		headers.set("Authorization", "Bearer " + jwtToken);
 		
 		EntityModel<Taco> tacoRes = traverson
 		        .follow("tacos")
 		        .follow("recents")
 		        .withHeaders(headers)
 		        .toObject(tacoType); // 런타임 오류

 		return List.of(tacoRes.getContent());
        
	}

}