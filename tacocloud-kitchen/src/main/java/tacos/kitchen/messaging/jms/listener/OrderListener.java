package tacos.kitchen.messaging.jms.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.kitchen.KitchenUI;

@Slf4j
@Profile("jms-listener")
@Component
public class OrderListener {
  
  private KitchenUI ui;

  @Autowired
  public OrderListener(KitchenUI ui) {
    this.ui = ui;
  }

  // publish/subscribe에서 subscribe role...
  @JmsListener(destination = "tacocloud.order.queue3")
  public void receiveOrder(Order order) {
	  log.info("OrderListener.receiveOrder" + order);
	  ui.displayOrder(order);
  }
  
}