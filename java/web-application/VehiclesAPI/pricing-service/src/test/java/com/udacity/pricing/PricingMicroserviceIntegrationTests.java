package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingMicroserviceIntegrationTests {

    public static class Prices implements Serializable {
        private List<Price> prices;

        public Prices() {}

        public List<Price> getPrices() {
            return prices;
        }

        public void setDogs(List<Price> prices) {
            this.prices = prices;
        }

        @Override
        public String toString() {
            return "Prices: " + prices.toString();
        }
    }
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllPrices() {
        ResponseEntity<Prices> response =
                this.restTemplate.getForEntity("http://localhost:" + port + "/services/prices", Prices.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void getPrice() {
        ResponseEntity<Price> response =
                this.restTemplate.getForEntity("http://localhost:" + port + "/services/prices/1", Price.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
