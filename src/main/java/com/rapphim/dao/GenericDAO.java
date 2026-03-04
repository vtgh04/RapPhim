package com.rapphim.dao;

import java.util.List;

/**
 * Interface chung cho tất cả DAO classes.
 * Định nghĩa các thao tác CRUD cơ bản.
 *
 * @param <T>  Loại entity
 * @param <ID> Loại khóa chính
 */
public interface GenericDAO<T, ID> {

    /**
     * Thêm mới một entity vào database
     * @param entity đối tượng cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    boolean insert(T entity);

    /**
     * Cập nhật thông tin entity trong database
     * @param entity đối tượng cần cập nhật
     * @return true nếu thành công
     */
    boolean update(T entity);

    /**
     * Xóa entity theo ID
     * @param id khóa chính của entity
     * @return true nếu thành công
     */
    boolean delete(ID id);

    /**
     * Lấy entity theo ID
     * @param id khóa chính
     * @return entity hoặc null nếu không tìm thấy
     */
    T findById(ID id);

    /**
     * Lấy tất cả entity
     * @return danh sách tất cả entity
     */
    List<T> findAll();
}
