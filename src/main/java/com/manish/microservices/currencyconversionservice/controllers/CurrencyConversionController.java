package com.manish.microservices.currencyconversionservice.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.manish.microservices.currencyconversionservice.configuration.RestTemplateConfiguration;
import com.manish.microservices.currencyconversionservice.dto.CurrencyConversion;
import com.manish.microservices.currencyconversionservice.proxy.CurrencyExchangeProxy;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy currencyExchangeProxy;
	
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{qty}")
	public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
			@PathVariable Integer qty) {

		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntiry = this.restTemplate.getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);
		CurrencyConversion currencyConversion = responseEntiry.getBody();
		currencyConversion.setQuantity(qty);
		currencyConversion
				.setTotalCalculatedAmount(currencyConversion.getConversionMultiple().multiply(BigDecimal.valueOf(qty)));

		return currencyConversion;
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{qty}")
	public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable Integer qty) {
		
		CurrencyConversion currencyConversion = this.currencyExchangeProxy.retrieveExchangeValue(from, to);

		currencyConversion.setQuantity(qty);
		currencyConversion
				.setTotalCalculatedAmount(currencyConversion.getConversionMultiple().multiply(BigDecimal.valueOf(qty)));

		return currencyConversion;
	}
}
