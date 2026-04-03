package com.comparator.smart_comparator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ExternalApiService {

    private final String API_KEY = "b2c1413535417ed1ce49d0b9915e9a114362316ecb2d6eb8c29fbe6647de60be"; // 🔑 replace

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchPrices(String product) {

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            String url = "https://serpapi.com/search.json?q=" + product +
                    "&engine=google_shopping&api_key=" + API_KEY;

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> response =
                    restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("shopping_results")) {
                return result;
            }

            List<Map<String, Object>> shoppingResults =
                    (List<Map<String, Object>>) response.get("shopping_results");

            for (Map<String, Object> item : shoppingResults) {

                Map<String, Object> p = new HashMap<>();

                p.put("store", item.getOrDefault("source", "Unknown"));
                p.put("price", extractPrice(item.get("price")));
                p.put("rating", item.get("rating") != null
                        ? Double.parseDouble(item.get("rating").toString())
                        : 4.0);
                p.put("title", item.getOrDefault("title", "No Title"));
                p.put("link", item.getOrDefault("link", "#"));
                p.put("image", item.getOrDefault("thumbnail", ""));

                result.add(p);
            }

        } catch (Exception e) {
            System.out.println("API ERROR: " + e.getMessage());
        }

        return result;
    }

    private double extractPrice(Object priceObj) {
        try {
            if (priceObj == null) return 0;
            String priceStr = priceObj.toString().replaceAll("[^0-9.]", "");
            return Double.parseDouble(priceStr);
        } catch (Exception e) {
            return 0;
        }
    }
}