package com.rapphim.view.panels;

import java.awt.Color;
import javax.swing.JButton;

/**
 * Panel lịch chiếu dành cho Staff.
 * Kế thừa giao diện từ ShowtimesPanel nhưng ẩn đi các chức năng quản lý.
 */
public class StaffShowPanel extends ShowtimesPanel {

    public StaffShowPanel() {
        super();
    }

    @Override
    protected JButton mkBtn(String text, Color bg, Color hover) {
        JButton btn = super.mkBtn(text, bg, hover);
        // Ẩn các nút chỉnh sửa/export/import đối với Staff
        if (text.equals("Export Excel") || 
            text.equals("Import Excel") || 
            text.equals("Tạo suất chiếu") || 
            text.equals("Cập nhật thông tin") || 
            text.equals("Hủy suất chiếu")) {
            btn.setVisible(false);
        }
        return btn;
    }
}
