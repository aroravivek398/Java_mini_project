package ui;

import javax.swing.*;
import javax.swing.border.*;

import db.BudgetDAO;
import db.ExpenseDAO;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import model.ExpenseModel;
import model.UserModel;
import ui.Auth.LoginScreen;
import java.time.LocalDate;

public class DashboardScreen extends JFrame implements ActionListener {

    private UserModel user;
    private ExpenseDAO expenseDao;
    private BudgetDAO budgetDao;
    
    private LocalDate today = LocalDate.now();
	private int month = today.getMonthValue();
	private int year = today.getYear();

    JButton dashboardButton;
    JButton addExpenseButton;
    JButton viewExpensesButton;
    JButton analysisButton;
    JButton budgetButton;
    JButton logoutButton;

    JLabel totalExpenseValue;
    JLabel thisMonthValue;
    JLabel budgetLeftValue;

    JComboBox<Integer> txnLimitSelector;

    JPanel recentCard;

    public DashboardScreen(UserModel user) {
        this.user = user;
        this.expenseDao = new ExpenseDAO();
        this.budgetDao  = new BudgetDAO();

        setTitle("Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ===================== LEFT SIDEBAR ===================== */

        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(90, 120, 255),
                        0, getHeight(), new Color(150, 70, 210)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));
        sidebar.setOpaque(true);

        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("SansSerif", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sidebarSep = new JSeparator();
        sidebarSep.setForeground(Color.WHITE);
        sidebarSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        String name = (user != null && user.getName() != null && !user.getName().isEmpty())
                      ? user.getName() : "User";

        JLabel welcomeText = new JLabel("Welcome,");
        welcomeText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        welcomeText.setForeground(new Color(200, 210, 255));
        welcomeText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userName = new JLabel(name);
        userName.setFont(new Font("SansSerif", Font.BOLD, 15));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sidebarSep);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(welcomeText);
        sidebar.add(userName);
        sidebar.add(Box.createVerticalStrut(30));

        dashboardButton    = createSidebarButton("Dashboard");
        addExpenseButton   = createSidebarButton("Add Expense");
        viewExpensesButton = createSidebarButton("View Expenses");
        analysisButton     = createSidebarButton("Analysis");
        budgetButton       = createSidebarButton("Budget");

        dashboardButton.addActionListener(this);
        addExpenseButton.addActionListener(this);
        viewExpensesButton.addActionListener(this);
        analysisButton.addActionListener(this);
        budgetButton.addActionListener(this);

        sidebar.add(dashboardButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(addExpenseButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(viewExpensesButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(analysisButton);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(budgetButton);
        sidebar.add(Box.createVerticalGlue());

        logoutButton = createSidebarButton("Logout");
        logoutButton.addActionListener(this);
        sidebar.add(logoutButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel();
        content.setBackground(new Color(245, 246, 252));
        content.setOpaque(true);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(35, 35, 35, 35));

        JLabel dashTitle = new JLabel("Dashboard");
        dashTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        dashTitle.setForeground(new Color(40, 40, 40));
        dashTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dashSub = new JLabel("Here's your expense overview");
        dashSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dashSub.setForeground(new Color(150, 150, 170));
        dashSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(dashTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(dashSub);
        content.add(Box.createVerticalStrut(25));

        /* ---- Stat Cards ---- */
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        totalExpenseValue = new JLabel("\u20B90.00");
        thisMonthValue    = new JLabel("\u20B90.00");
        budgetLeftValue   = new JLabel("\u20B90.00");

        statsRow.add(createStatCard("Total Expenses", totalExpenseValue, new Color(100, 100, 230)));
        statsRow.add(createStatCard("This Month",     thisMonthValue,    new Color(220, 70,  100)));
        statsRow.add(createStatCard("Budget Left",    budgetLeftValue,   new Color(50,  180, 130)));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(20));

        /* ---- Txn Header Row (title + dropdown) ---- */
        JPanel txnHeaderRow = new JPanel(new BorderLayout());
        txnHeaderRow.setOpaque(false);
        txnHeaderRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        txnHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel txnTitle = new JLabel("Recent Transactions");
        txnTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        txnTitle.setForeground(new Color(40, 40, 40));

        Integer[] limits = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        txnLimitSelector = new JComboBox<>(limits);
        txnLimitSelector.setSelectedItem(5);
        txnLimitSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txnLimitSelector.setPreferredSize(new Dimension(70, 28));
        txnLimitSelector.setFocusable(false);
        txnLimitSelector.addActionListener(this);

        txnHeaderRow.add(txnTitle, BorderLayout.WEST);
        txnHeaderRow.add(txnLimitSelector, BorderLayout.EAST);

        content.add(txnHeaderRow);
        content.add(Box.createVerticalStrut(8));

        /* ---- Recent Transactions Card ---- */
        recentCard = new JPanel(new BorderLayout());
        recentCard.setBackground(Color.WHITE);
        recentCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        recentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        content.add(recentCard);

        /* ===================== ASSEMBLE ===================== */
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        setVisible(true);

        loadDashboardData();
    }

    /* ===================== LOAD DASHBOARD DATA ===================== */

    public void loadDashboardData() {
        try {
            double total      = expenseDao.getTotalExpense(user.getId());
            double monthSpent = expenseDao.getMonthlyTotal(user.getId(), this.month, this.year);
            double budgetLeft = budgetDao.getRemainingBudget(user.getId(),this.month,this.year);

            refreshData(total, monthSpent, budgetLeft);

            // ✅ FIX: duplicate variable hataya — sirf ek baar getAllExpense call
            ArrayList<ExpenseModel> allExpenses = expenseDao.getAllExpense(user.getId());
            int limit = (txnLimitSelector != null) ? (int) txnLimitSelector.getSelectedItem() : 5;

            ArrayList<ExpenseModel> limited = new ArrayList<>();
            for (int i = 0; i < Math.min(limit, allExpenses.size()); i++) {
                limited.add(allExpenses.get(i));
            }

            refreshTransactions(limited);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ===================== REFRESH METHODS ===================== */

    public void refreshData(double total, double month, double budget) {
        totalExpenseValue.setText(String.format("\u20B9%.2f", total));
        thisMonthValue.setText(String.format("\u20B9%.2f", month));
        budgetLeftValue.setText(String.format("\u20B9%.2f", budget));
    }

    public void refreshTransactions(List<ExpenseModel> expenses) {
        recentCard.removeAll();

        if (expenses == null || expenses.isEmpty()) {
            JLabel noData = new JLabel("No transactions yet. Click 'Add Expense' to get started.");
            noData.setFont(new Font("SansSerif", Font.PLAIN, 13));
            noData.setForeground(new Color(170, 170, 190));
            noData.setHorizontalAlignment(SwingConstants.CENTER);
            recentCard.add(noData, BorderLayout.CENTER);

        } else {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(Color.WHITE);

            // ---- Column Headers ----
            JPanel headerRow = new JPanel(new GridLayout(1, 3));
            headerRow.setBackground(new Color(245, 246, 252));
            headerRow.setBorder(new EmptyBorder(4, 0, 8, 0));
            headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            String[] cols = {"Category", "Date", "Amount"};
            for (String col : cols) {
                JLabel h = new JLabel(col);
                h.setFont(new Font("SansSerif", Font.BOLD, 12));
                h.setForeground(new Color(120, 120, 140));
                if (col.equals("Amount")) h.setHorizontalAlignment(SwingConstants.RIGHT);
                if (col.equals("Date"))   h.setHorizontalAlignment(SwingConstants.CENTER);
                headerRow.add(h);
            }
            listPanel.add(headerRow);

            JSeparator headerSep = new JSeparator();
            headerSep.setForeground(new Color(210, 210, 225));
            headerSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            listPanel.add(headerSep);

            // ---- Data Rows ----
            for (ExpenseModel exp : expenses) {
                JPanel row = new JPanel(new GridLayout(1, 3));
                row.setBackground(Color.WHITE);
                row.setBorder(new EmptyBorder(10, 0, 10, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

                JLabel nameLabel = new JLabel(exp.category);
                nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                nameLabel.setForeground(new Color(40, 40, 40));

                JLabel amountLabel = new JLabel("\u20B9" + exp.amount);
                JLabel categoryLabel = new JLabel(exp.category);
                categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                categoryLabel.setForeground(new Color(40, 40, 40));

                JLabel dateLabel = new JLabel(exp.date);
                dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                dateLabel.setForeground(new Color(130, 130, 150));
                dateLabel.setHorizontalAlignment(SwingConstants.CENTER);

                amountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
                amountLabel.setForeground(new Color(220, 70, 100));
                amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

                row.add(categoryLabel);
                row.add(dateLabel);
                row.add(amountLabel);
                listPanel.add(row);

                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(230, 230, 240));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                listPanel.add(sep);
            }

            JScrollPane scrollPane = new JScrollPane(listPanel);
            scrollPane.setBorder(null);
            scrollPane.setBackground(Color.WHITE);
            recentCard.add(scrollPane, BorderLayout.CENTER);
        }

        recentCard.revalidate();
        recentCard.repaint();
    }

    /* ===================== HELPER METHODS ===================== */

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (hovered) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        return btn;
    }

    /* ===================== ACTION LISTENER ===================== */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addExpenseButton) {
            new AddExpenseScreen(this, user);
        } else if (e.getSource() == viewExpensesButton) {
            new ViewExpensesScreen(this, user);
        } else if (e.getSource() == analysisButton) {
            new AnalysisScreen(this, user);
        } else if (e.getSource() == budgetButton) {
            new BudgetScreen(this, user);
        } else if (e.getSource() == dashboardButton) {
            loadDashboardData();
        } else if (e.getSource() == txnLimitSelector) {
            // ✅ Dropdown change hone pe transactions reload
            try {
                int limit = (int) txnLimitSelector.getSelectedItem();
                ArrayList<ExpenseModel> all = expenseDao.getAllExpense(user.getId());
                ArrayList<ExpenseModel> filtered = new ArrayList<>();
                for (int i = 0; i < Math.min(limit, all.size()); i++) {
                    filtered.add(all.get(i));
                }
                refreshTransactions(filtered);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == logoutButton) {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginScreen();
                dispose();
            }
        }
    }
}