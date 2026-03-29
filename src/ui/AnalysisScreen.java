package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import db.ExpenseDAO;
import model.ExpenseModel;
import model.UserModel;

public class AnalysisScreen extends JFrame implements ActionListener {

    private DashboardScreen dashboard;
    private UserModel user;

    private JButton backButton;
    private JComboBox<String> chartSelector;
    private JPanel chartArea; 

    private ArrayList<ExpenseModel> expenses;

    private static final Color GRAD_TOP   = new Color(90, 120, 255);
    private static final Color GRAD_BTM   = new Color(150, 70, 210);
    private static final Color BG_COLOR   = new Color(245, 246, 252);
    private static final Color TEXT_COLOR = new Color(30, 30, 60);
    private static final Color BORDER_CLR = new Color(210, 210, 230);

    public AnalysisScreen(DashboardScreen dashboard, UserModel user) {
        this.dashboard = dashboard;
        this.user      = user;

        setTitle("Analysis");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ===================== LEFT SIDEBAR ===================== */

        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, 0, getHeight(), GRAD_BTM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 620));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        sidebar.setOpaque(true);

        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("SansSerif", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.WHITE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel sideTitle = new JLabel("Analysis");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        sideTitle.setForeground(Color.WHITE);
        sideTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sideSub = new JLabel("Your spending insights");
        sideSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sideSub.setForeground(new Color(200, 210, 255));
        sideSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Chart selector label
        JLabel selectLabel = new JLabel("Select Chart");
        selectLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        selectLabel.setForeground(new Color(200, 210, 255));
        selectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dropdown
        String[] chartOptions = {
            "Category Wise (Pie)",
            "Monthly Expense (Bar)",
            "Expense Trend (Line)"
        };
        chartSelector = new JComboBox<>(chartOptions);
        chartSelector.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chartSelector.setBackground(Color.WHITE);
        chartSelector.setForeground(TEXT_COLOR);
        chartSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        chartSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartSelector.setFocusable(false);
        chartSelector.addActionListener(this);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sideTitle);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(sideSub);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(selectLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(chartSelector);
        sidebar.add(Box.createVerticalGlue());

        backButton = createSidebarButton("Back to Dashboard");
        backButton.addActionListener(this);
        sidebar.add(backButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel("Expense Analysis");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_COLOR);

        JLabel pageSub = new JLabel("Visual breakdown of your spending");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(new Color(150, 150, 180));

        titleText.add(pageTitle);
        titleText.add(Box.createVerticalStrut(3));
        titleText.add(pageSub);
        titleRow.add(titleText, BorderLayout.WEST);
        content.add(titleRow, BorderLayout.NORTH);

        // Chart area — white card
        chartArea = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chartArea.setOpaque(false);
        chartArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        content.add(chartArea, BorderLayout.CENTER);

        /* ===================== ASSEMBLE ===================== */
        add(sidebar,  BorderLayout.WEST);
        add(content,  BorderLayout.CENTER);

        // DB se data load karo
        loadData();

        // Default — pehla chart dikhao
        showChart(0);

        setVisible(true);
    }

    /* ===================== LOAD DATA ===================== */

    private void loadData() {
        try {
            ExpenseDAO dao = new ExpenseDAO();
            expenses = dao.getAllExpense(user.getId());
        } catch (Exception ex) {
            expenses = new ArrayList<>();
            JOptionPane.showMessageDialog(this,
                    "Error loading data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ===================== SHOW CHART ===================== */

    private void showChart(int index) {
        chartArea.removeAll(); // purana chart hato

        JFreeChart chart;
        switch (index) {
            case 0: chart = buildPieChart();  break;
            case 1: chart = buildBarChart();  break;
            case 2: chart = buildLineChart(); break;
            default: chart = buildPieChart(); break;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setPopupMenu(null);

        chartArea.add(chartPanel, BorderLayout.CENTER);
        chartArea.revalidate();
        chartArea.repaint();
    }

    /* ===================== PIE CHART ===================== */

    private JFreeChart buildPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        double food = 0, transport = 0, shopping = 0, bills = 0, health = 0, other = 0;
        for (ExpenseModel exp : expenses) {
            switch (exp.category.toLowerCase()) {
                case "food":      food      += exp.amount; break;
                case "transport": transport += exp.amount; break;
                case "shopping":  shopping  += exp.amount; break;
                case "bills":     bills     += exp.amount; break;
                case "health":    health    += exp.amount; break;
                default:          other     += exp.amount; break;
            }
        }

        if (food > 0)      dataset.setValue("Food",      food);
        if (transport > 0) dataset.setValue("Transport", transport);
        if (shopping > 0)  dataset.setValue("Shopping",  shopping);
        if (bills > 0)     dataset.setValue("Bills",     bills);
        if (health > 0)    dataset.setValue("Health",    health);
        if (other > 0)     dataset.setValue("Other",     other);

        JFreeChart chart = ChartFactory.createPieChart(
                "Category Wise Expense", dataset, true, true, false
        );
        chart.setBackgroundPaint(java.awt.Color.WHITE);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionPaint("Food",      new Color(100, 100, 230));
        plot.setSectionPaint("Transport", new Color(220, 70,  100));
        plot.setSectionPaint("Shopping",  new Color(50,  180, 130));
        plot.setSectionPaint("Bills",     new Color(255, 160,  50));
        plot.setSectionPaint("Health",    new Color(90,  200, 220));
        plot.setSectionPaint("Other",     new Color(180, 100, 220));
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");

        return chart;
    }

    /* ===================== BAR CHART ===================== */

    private JFreeChart buildBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double[] monthly = new double[12];
        String[] months  = {"Jan","Feb","Mar","Apr","May","Jun",
                             "Jul","Aug","Sep","Oct","Nov","Dec"};

        for (ExpenseModel exp : expenses) {
            try {
                String[] parts = exp.date.split("-");
                int month = Integer.parseInt(parts[1]) - 1;
                monthly[month] += exp.amount;
            } catch (Exception ignored) {}
        }

        for (int i = 0; i < 12; i++) {
            dataset.addValue(monthly[i], "Expense", months[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Expense", "Month", "Amount (\u20B9)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );
        chart.setBackgroundPaint(java.awt.Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 235));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, GRAD_TOP);
        renderer.setDrawBarOutline(false);
        renderer.setMaximumBarWidth(0.05);

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));

        return chart;
    }

    /* ===================== LINE CHART ===================== */

    private JFreeChart buildLineChart() {
        XYSeries series = new XYSeries("Cumulative Expense");

        double cumulative = 0;
        int i = 1;
        for (ExpenseModel exp : expenses) {
            cumulative += exp.amount;
            series.add(i++, cumulative);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Expense Trend", "Transaction #", "Cumulative (\u20B9)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );
        chart.setBackgroundPaint(java.awt.Color.WHITE);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 235));
        plot.setOutlineVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, GRAD_BTM);
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));

        return chart;
    }

    /* ===================== ACTION LISTENER ===================== */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chartSelector) {
            showChart(chartSelector.getSelectedIndex());
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }

    /* ===================== HELPERS ===================== */

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return btn;
    }
}