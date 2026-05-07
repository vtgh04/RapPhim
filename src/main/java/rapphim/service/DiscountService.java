package rapphim.service;

import rapphim.dao.DiscountDao;
import rapphim.model.Discount;

import java.sql.SQLException;
import java.util.List;

public class DiscountService {
    private DiscountDao discountDao;

    public DiscountService() {
        this.discountDao = new DiscountDao();
    }

    public List<Discount> getAllDiscounts() throws SQLException {
        return discountDao.findAll();
    }

    public void addDiscount(Discount discount) throws SQLException {
        if (discount.getDiscountId() == null || discount.getDiscountId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã giảm giá không được để trống");
        }
        if (discount.getDiscountName() == null || discount.getDiscountName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chương trình không được để trống");
        }
        if (discountDao.findById(discount.getDiscountId()) != null) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
        }
        discountDao.add(discount);
    }

    public void updateDiscount(Discount discount) throws SQLException {
        if (discountDao.findById(discount.getDiscountId()) == null) {
            throw new IllegalArgumentException("Mã giảm giá không tồn tại");
        }
        discountDao.update(discount);
    }

    public void deleteDiscount(String discountId) throws SQLException {
        discountDao.delete(discountId);
    }

    public List<Discount> searchDiscounts(String keyword) throws SQLException {
        return discountDao.search(keyword);
    }

    public Discount getDiscountById(String discountId) throws SQLException {
        return discountDao.findById(discountId);
    }
}
