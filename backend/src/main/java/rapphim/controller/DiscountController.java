package rapphim.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rapphim.model.Discount;
import rapphim.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(discountService.searchDiscounts(search));
        }
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable String id) {
        Discount d = discountService.getDiscountById(id);
        if (d == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(d);
    }

    @PostMapping
    public ResponseEntity<?> addDiscount(@Valid @RequestBody Discount discount) {
        discountService.addDiscount(discount);
        return ResponseEntity.status(HttpStatus.CREATED).body(discount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscount(@PathVariable String id, @Valid @RequestBody Discount discount) {
        discount.setDiscountId(id);
        discountService.updateDiscount(discount);
        return ResponseEntity.ok(discount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok().build();
    }
}
