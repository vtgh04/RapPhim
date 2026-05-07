package rapphim.view.panels;

import rapphim.model.Invoice;
import rapphim.service.InvoiceService;
import rapphim.service.DashboardService;
import rapphim.util.InvoiceExcelUtils;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.*;

public class DashboardPanel extends JPanel {

    Color BG = new Color(240, 242, 245);
    Color WHITE = Color.WHITE;
    Color TEXT_PRIMARY = new Color(30, 30, 35);
    Color TEXT_SECONDARY = new Color(130, 135, 148);

    private JLabel lbRevenue, lbInvoices, lbTickets;
    private ChartPanel chartPanel;
    private JPanel topMoviesPanel;

    private final InvoiceService service = new InvoiceService();
    private final DashboardService dashboardService = new DashboardService();
    private java.util.List<Invoice> invoices = new ArrayList<>();

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        new Timer(3000, e -> loadData()).start();
        loadData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(30, 35, 15, 35));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Overview of revenue and performance");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(subtitle);

        JButton exportBtn = new JButton("Export Excel");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportBtn.addActionListener(e -> handleExportExcel());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.addActionListener(e -> loadData());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(exportBtn);
        right.add(refreshBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel createBody() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(0, 35, 30, 35));
        panel.setOpaque(false);

        panel.add(createCards(), BorderLayout.NORTH);
        panel.add(createCenter(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCards() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);

        lbRevenue = new JLabel("0 VND");
        lbInvoices = new JLabel("0");
        lbTickets = new JLabel("0");

        panel.add(card("Revenue", lbRevenue, new Color(59, 130, 246)));
        panel.add(card("Invoices", lbInvoices, new Color(34, 197, 94)));
        panel.add(card("Tickets", lbTickets, new Color(168, 85, 247)));

        return panel;
    }

    private JPanel card(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel t = new JLabel(title);
        t.setForeground(TEXT_SECONDARY);

        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
        value.setForeground(color);

        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);

        return p;
    }

    private JPanel createCenter() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);

        // CHART
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(800, 350));
        chartPanel.setBackground(WHITE);
        chartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(chartPanel, BorderLayout.CENTER);

        // TOP MOVIES
        panel.add(createTopMovies(), BorderLayout.EAST);

        return panel;
    }

    private JPanel createTopMovies() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(WHITE);
        wrapper.setPreferredSize(new Dimension(260, 300));
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Top Movies");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 8, 0));

        // ALIGN TOP
        topMoviesPanel = new JPanel();
        topMoviesPanel.setLayout(new BoxLayout(topMoviesPanel, BoxLayout.Y_AXIS));
        topMoviesPanel.setBackground(WHITE);
        topMoviesPanel.setAlignmentY(Component.TOP_ALIGNMENT); // 🟢 QUAN TRỌNG

        JScrollPane scroll = new JScrollPane(topMoviesPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private void loadData() {
        try {
            invoices = service.getAllInvoices();

            double revenue = 0;
            int tickets = 0;

            for (Invoice i : invoices) {
                revenue += i.getTotalAmount();
                tickets += i.getTotalTickets();
            }

            lbRevenue.setText(String.format("%,.0f VND", revenue));
            lbInvoices.setText(String.valueOf(invoices.size()));
            lbTickets.setText(String.valueOf(tickets));

            Map<Integer, Double> map = new HashMap<>();
            for (Object[] r : dashboardService.getRevenueByDay()) {
                map.put((Integer) r[0], (Double) r[1]);
            }
            chartPanel.setData(map);

            renderTopMovies();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderTopMovies() throws Exception {

        topMoviesPanel.removeAll();

        for (Object[] r : dashboardService.getTopMovies()) {

            String title = (String) r[0];
            String poster = (String) r[1];
            int count = (int) r[2];

            // CARD
            JPanel card = new JPanel(new BorderLayout(12, 0));
            card.setBackground(WHITE);
            card.setBorder(new EmptyBorder(8, 5, 8, 5));
            card.setMaximumSize(new Dimension(240, 110));

            // IMAGE
            JLabel img = new JLabel();
            img.setPreferredSize(new Dimension(80, 110));

            if (poster != null) {
                java.net.URL url = getClass().getClassLoader().getResource(poster);
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    img.setIcon(new ImageIcon(
                            icon.getImage().getScaledInstance(80, 110, Image.SCALE_SMOOTH)));
                }
            }

            // TEXT
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(WHITE);

            JLabel name = new JLabel(title);
            name.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel tickets = new JLabel(count + " tickets");
            tickets.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tickets.setForeground(new Color(120, 120, 130));

            textPanel.add(Box.createVerticalGlue());
            textPanel.add(name);
            textPanel.add(Box.createVerticalStrut(5));
            textPanel.add(tickets);
            textPanel.add(Box.createVerticalGlue());

            card.add(img, BorderLayout.WEST);
            card.add(textPanel, BorderLayout.CENTER);

            // HOVER
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(new Color(235, 240, 255));
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(WHITE);
                }
            });

            topMoviesPanel.add(card);
            topMoviesPanel.add(Box.createVerticalStrut(10)); // ✅ spacing
        }

        topMoviesPanel.add(Box.createVerticalGlue()); // ✅ giữ item ở trên cùng

        topMoviesPanel.revalidate();
        topMoviesPanel.repaint();
    }

    private void handleExportExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                InvoiceExcelUtils.export(invoices, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Export thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    static class ChartPanel extends JPanel {

        private Map<Integer, Double> data = new TreeMap<>();
        private float progress = 0f;

        public void setData(Map<Integer, Double> d) {
            this.data = new TreeMap<>(d);

            // reset animation
            progress = 0f;

            new Timer(16, e -> {
                progress += 0.03f;

                if (progress >= 1f) {
                    progress = 1f;
                    ((Timer) e.getSource()).stop();
                }

                repaint();
            }).start();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (data == null || data.isEmpty())
                return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            int padding = 50;

            int maxX = Collections.max(data.keySet());
            double maxY = Collections.max(data.values());

            // background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // title
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.setColor(new Color(30, 30, 35));
            g2.drawString("Revenue Overview", padding, 25);

            // grid
            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i < 5; i++) {
                int y = padding + i * (h - 2 * padding) / 5;
                g2.drawLine(padding, y, w - padding, y);
            }

            // points
            java.util.List<Point> points = new ArrayList<>();

            for (int x : data.keySet()) {
                int dx = padding + (int) ((x * (w - 2.0 * padding)) / (maxX + 1));
                int dy = h - padding - (int) ((data.get(x) / maxY) * (h - 2 * padding));
                points.add(new Point(dx, dy));
            }

            if (points.size() < 2)
                return;

            int visible = (int) (points.size() * progress);

            visible = Math.max(visible, 2);

            // gradient fill
            Polygon poly = new Polygon();

            for (int i = 0; i < visible; i++) {
                poly.addPoint(points.get(i).x, points.get(i).y);
            }

            poly.addPoint(points.get(visible - 1).x, h - padding);
            poly.addPoint(points.get(0).x, h - padding);

            g2.setPaint(new GradientPaint(
                    0, padding,
                    new Color(59, 130, 246, 120),
                    0, h,
                    new Color(59, 130, 246, 0)));

            g2.fillPolygon(poly);

            // line
            g2.setColor(new Color(59, 130, 246));
            g2.setStroke(new BasicStroke(3f));

            for (int i = 1; i < visible; i++) {
                Point p1 = points.get(i - 1);
                Point p2 = points.get(i);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // dots
            for (int i = 0; i < visible; i++) {
                Point p = points.get(i);

                g2.setColor(Color.WHITE);
                g2.fillOval(p.x - 5, p.y - 5, 10, 10);

                g2.setColor(new Color(59, 130, 246));
                g2.drawOval(p.x - 5, p.y - 5, 10, 10);
            }

            // label X
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(120, 120, 130));

            Object[] keys = data.keySet().toArray();

            for (int i = 0; i < visible; i++) {
                Point p = points.get(i);
                g2.drawString(String.valueOf(keys[i]), p.x - 3, h - 10);
            }
        }
    }
}