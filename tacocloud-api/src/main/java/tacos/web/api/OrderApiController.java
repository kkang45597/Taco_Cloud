package tacos.web.api;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import lombok.extern.slf4j.Slf4j;


import tacos.Order;
import tacos.Taco;
import tacos.data.OrderRepository;
import tacos.data.TacoRepository;
import tacos.messaging.OrderMessagingService;

@Slf4j
@RestController
@RequestMapping(path="/orders",
                produces="application/json")
@CrossOrigin(origins="*")
public class OrderApiController {

  private OrderRepository repo;
  private TacoRepository tacoRepo;
  private OrderMessagingService orderMessages;

  public OrderApiController(OrderRepository repo,
		  TacoRepository tacoRepo,
		  					OrderMessagingService orderMessages) {
    this.repo = repo;
    this.tacoRepo = tacoRepo;
    this.orderMessages = orderMessages;
  }
  
  @GetMapping(produces="application/json")
  public Iterable<Order> allOrders() {
    return repo.findAll();
  }
  
//  @PostMapping(consumes="application/json")
//  @ResponseStatus(HttpStatus.CREATED)
//  public Order postOrder(@RequestBody Order order) {
//	  log.info("Before SENDED ORDER:  " + order);
//	  orderMessages.sendOrder(order);
//	  log.info("SENDED ORDER:  " + order);
//
//	  return repo.save(order);
//  }
  
  @PostMapping(consumes="application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Order postOrder(@RequestBody Order order) {
      log.info("Before SENDED ORDER: " + order);

      // TacoRepository를 사용하여 Taco 객체를 먼저 저장
      List<Taco> tacos = order.getTacos();
      for (int i = 0; i < tacos.size(); i++) {
          Taco taco = tacos.get(i);
          if (taco.getId() == null) {
              // Taco가 영속화되지 않았다면, 먼저 TacoRepository를 통해 저장합니다.
              Taco savedTaco = tacoRepo.save(taco);
              tacos.set(i, savedTaco); // 저장된 Taco로 업데이트
          }
      }

      // Order 객체를 메시지로 보낸 후 저장
      orderMessages.sendOrder(order);
      Order savedOrder = repo.save(order);

      log.info("SENDED ORDER: " + savedOrder);
      return savedOrder;
  }

  
  

  @PutMapping(path="/{orderId}", consumes="application/json")
  public Order putOrder(@RequestBody Order order) {
    return repo.save(order);
  }

  @PatchMapping(path="/{orderId}", consumes="application/json")
  public Order patchOrder(@PathVariable("orderId") Long orderId,
                          @RequestBody Order patch) {
    
    Order order = repo.findById(orderId).get();
    if (patch.getDeliveryName() != null) {
      order.setDeliveryName(patch.getDeliveryName());
    }
    if (patch.getDeliveryStreet() != null) {
      order.setDeliveryStreet(patch.getDeliveryStreet());
    }
    if (patch.getDeliveryCity() != null) {
      order.setDeliveryCity(patch.getDeliveryCity());
    }
    if (patch.getDeliveryState() != null) {
      order.setDeliveryState(patch.getDeliveryState());
    }
    if (patch.getDeliveryZip() != null) {
      order.setDeliveryZip(patch.getDeliveryState());
    }
    if (patch.getCcNumber() != null) {
      order.setCcNumber(patch.getCcNumber());
    }
    if (patch.getCcExpiration() != null) {
      order.setCcExpiration(patch.getCcExpiration());
    }
    if (patch.getCcCVV() != null) {
      order.setCcCVV(patch.getCcCVV());
    }
    return repo.save(order);
  }
  
  @DeleteMapping("/{orderId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrder(@PathVariable("orderId") Long orderId) {
    try {
      repo.deleteById(orderId);
    } catch (EmptyResultDataAccessException e) {}
  }

}
