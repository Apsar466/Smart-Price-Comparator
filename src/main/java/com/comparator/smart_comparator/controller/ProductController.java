package com.comparator.smart_comparator.controller;

import com.comparator.smart_comparator.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/prices")
@CrossOrigin
public class ProductController {

    @Autowired
    private ExternalApiService externalApiService;

    // 🔥 LIVE SEARCH + SMART COMPARE
    @GetMapping("/live/{name}")
    public Map<String, Object> getLivePrices(
            @PathVariable String name,
            @RequestParam(required = false) List<String> stores
    ) {

        List<Map<String, Object>> products = externalApiService.fetchPrices(name);

        // ❌ No data
        if (products == null || products.isEmpty()) {
            return Map.of("message", "No products found");
        }

        // 🔍 STORE FILTER
        if (stores != null && !stores.isEmpty()) {
            List<Map<String, Object>> filtered = new ArrayList<>();

            for (Map<String, Object> p : products) {
                String store = ((String) p.get("store")).toLowerCase();

                for (String s : stores) {
                    if (store.contains(s.toLowerCase())) {
                        filtered.add(p);
                        break;
                    }
                }
            }

            products = filtered;
        }

        // ❌ No data after filter
        if (products.isEmpty()) {
            return Map.of("message", "No products after filtering");
        }

        // 🟢 INIT
        Map<String, Object> cheapest = products.get(0);
        Map<String, Object> best = products.get(0);

        // 🔥 LOGIC LOOP
        for (Map<String, Object> p : products) {

            double price = ((Number) p.get("price")).doubleValue();
            double rating = ((Number) p.get("rating")).doubleValue();

            double cheapestPrice = ((Number) cheapest.get("price")).doubleValue();

            double bestPrice = ((Number) best.get("price")).doubleValue();
            double bestRating = ((Number) best.get("rating")).doubleValue();

            // 💰 CHEAPEST
            if (price < cheapestPrice) {
                cheapest = p;
            }

            // ⭐ BEST DEAL (FIXED LOGIC)
            // Avoid divide by zero
            if (rating == 0) rating = 1;
            if (bestRating == 0) bestRating = 1;

            double score = price / rating;
            double bestScore = bestPrice / bestRating;

            if (score < bestScore) {
                best = p;
            }
        }

        // 📦 RESPONSE
        Map<String, Object> result = new HashMap<>();
        result.put("allProducts", products);
        result.put("cheapest", cheapest);
        result.put("bestDeal", best);

        return result;
    }
}