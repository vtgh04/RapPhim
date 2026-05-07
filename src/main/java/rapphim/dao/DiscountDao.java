package rapphim.dao;

import rapphim.config.DatabaseConnection;
import rapphim.model.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDao {

    public List<Discount> findAll() throws SQLException {
        List<Discount> list = new ArrayList<>();
        String sql = "SELECT * FROM discounts";
        Connection conn = DatabaseConnection.getInstance();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Discount findById(String id) throws SQLException {
        String sql = "SELECT * FROM discounts WHERE discount_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public void add(Discount discount) throws SQLException {
        String sql = "INSERT INTO discounts (discount_id, discount_name, discount_type, discount_rate, " +
                "valid_from, valid_to, min_ticket_quantity, is_active, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, discount.getDiscountId());
            ps.setString(2, discount.getDiscountName());
            ps.setString(3, discount.getDiscountType());
            ps.setDouble(4, discount.getDiscountRate());
            ps.setDate(5, discount.getValidFrom() != null ? new java.sql.Date(discount.getValidFrom().getTime()) : null);
            ps.setDate(6, discount.getValidTo() != null ? new java.sql.Date(discount.getValidTo().getTime()) : null);
            ps.setInt(7, discount.getMinTicketQuantity());
            ps.setBoolean(8, discount.isActive());
            ps.setString(9, discount.getDescription());
            ps.executeUpdate();
        }
    }

    public void update(Discount discount) throws SQLException {
        String sql = "UPDATE discounts SET discount_name = ?, discount_type = ?, discount_rate = ?, " +
                "valid_from = ?, valid_to = ?, min_ticket_quantity = ?, is_active = ?, description = ? " +
                "WHERE discount_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, discount.getDiscountName());
            ps.setString(2, discount.getDiscountType());
            ps.setDouble(3, discount.getDiscountRate());
            ps.setDate(4, discount.getValidFrom() != null ? new java.sql.Date(discount.getValidFrom().getTime()) : null);
            ps.setDate(5, discount.getValidTo() != null ? new java.sql.Date(discount.getValidTo().getTime()) : null);
            ps.setInt(6, discount.getMinTicketQuantity());
            ps.setBoolean(7, discount.isActive());
            ps.setString(8, discount.getDescription());
            ps.setString(9, discount.getDiscountId());
            ps.executeUpdate();
        }
    }

    public void delete(String discountId) throws SQLException {
        String sql = "DELETE FROM discounts WHERE discount_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, discountId);
            ps.executeUpdate();
        }
    }

    public List<Discount> search(String keyword) throws SQLException {
        List<Discount> list = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE discount_id LIKE ? OR discount_name LIKE ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Discount mapRow(ResultSet rs) throws SQLException {
        return new Discount(
                rs.getString("discount_id"),
                rs.getString("discount_name"),
                rs.getString("discount_type"),
                rs.getDouble("discount_rate"),
                rs.getDate("valid_from"),
                rs.getDate("valid_to"),
                rs.getInt("min_ticket_quantity"),
                rs.getBoolean("is_active"),
                rs.getString("description")
        );
    }
}
