package rapphim.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rapphim.model.Discount;
import rapphim.repository.DiscountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    @Transactional
    public void addDiscount(Discount discount) {
        if (discount.getDiscountId() == null || discount.getDiscountId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã giảm giá không được để trống");
        }
        if (discount.getDiscountName() == null || discount.getDiscountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chương trình không được để trống");
        }
        if (discountRepository.existsById(discount.getDiscountId())) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại: " + discount.getDiscountId());
        }
        discountRepository.save(discount);
    }

    @Transactional
    public void updateDiscount(Discount discount) {
        if (!discountRepository.existsById(discount.getDiscountId())) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại: " + discount.getDiscountId());
        }
        discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(String discountId) {
        discountRepository.deleteById(discountId);
    }

    public List<Discount> searchDiscounts(String keyword) {
        return discountRepository.findByDiscountIdContainingOrDiscountNameContaining(keyword, keyword);
    }

    public Discount getDiscountById(String discountId) {
        return discountRepository.findById(discountId).orElse(null);
    }
}
