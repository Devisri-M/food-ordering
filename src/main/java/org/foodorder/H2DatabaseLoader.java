package org.foodorder;

import org.foodorder.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class H2DatabaseLoader {

  @Bean
  CommandLineRunner initDatabase(RestaurantRepository repository) {
    return args -> {
      // Insert initial data into H2 using JPA repository
//      repository.save(new RestaurantEntity("TheFancyFork", "123 Culinary Street", "Italian"));
//      repository.save(new RestaurantEntity("SpicySpoon", "456 Flavor Avenue", "Indian"));
    };
  }
}
