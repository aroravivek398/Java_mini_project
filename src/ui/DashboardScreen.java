package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import model.ExpenseModel;
import model.UserModel;
import ui.Auth.LoginScreen;

public class DashboardScreen extends JFrame implements ActionListener {

    private UserModel user;

    JButton dashboardButton;
    JButton addExpenseButton;
    JButton viewExpensesButton;
    JButton analysisButton;
    JButton budgetButton;
    JButton logoutButton;

    JLabel totalExpenseValue;
    JLabel thisMonthValue;
    JLabel budgetLeftValue;

    JPanel recentCard; // class-level rakha taaki refreshTransactions access kar sake

    public DashboardScreen(UserModel user) {
        this.user = user;

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

        /* ---- Recent Transactions ---- */
        recentCard = new JPanel(new BorderLayout());
        recentCard.setBackground(Color.WHITE);
        recentCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        recentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        recentTitle.setForeground(new Color(40, 40, 40));

        JLabel noData = new JLabel("No transactions yet. Click 'Add Expense' to get started.");
        noData.setFont(new Font("SansSerif", Font.PLAIN, 13));
        noData.setForeground(new Color(170, 170, 190));
        noData.setHorizontalAlignment(SwingConstants.CENTER);

        recentCard.add(recentTitle, BorderLayout.NORTH);
        recentCard.add(noData, BorderLayout.CENTER);

        content.add(recentCard);

        /* ===================== ASSEMBLE ===================== */
        add(sidebar,  BorderLayout.WEST);
        add(content,  BorderLayout.CENTER);

        setVisible(true);
    }

    /* ===================== REFRESH METHODS ===================== */

    // Stat cards update karne ke liye — AddExpenseScreen ya DB se call karo
    public void refreshData(double total, double month, double budget) {
        totalExpenseValue.setText("\u20B9" + total);
        thisMonthValue.setText("\u20B9" + month);
        budgetLeftValue.setText("\u20B9" + budget);
    }

    // Recent transactions update karne ke liye — AddExpenseScreen ya DB se call karo
    public void refreshTransactions(List<ExpenseModel> expenses) {
        recentCard.removeAll(); // purane components hata do

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        recentTitle.setForeground(new Color(40, 40, 40));
        recentCard.add(recentTitle, BorderLayout.NORTH);

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

            for (ExpenseModel exp : expenses) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(new EmptyBorder(8, 0, 8, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel nameLabel = new JLabel(exp.getCategory());
                nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                nameLabel.setForeground(new Color(40, 40, 40));

                JLabel amountLabel = new JLabel("\u20B9" + exp.getAmount());
                amountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
                amountLabel.setForeground(new Color(220, 70, 100));

                row.add(nameLabel,   BorderLayout.WEST);
                row.add(amountLabel, BorderLayout.EAST);
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

        recentCard.revalidate(); // layout recalculate karo
        recentCard.repaint();    // screen pe draw karo
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
            new AddExpenseScreen(this,user);
        } else if (e.getSource() == viewExpensesButton) {
            new ViewExpensesScreen(this,user);
        } else if (e.getSource() == analysisButton) {
            JOptionPane.showMessageDialog(this, "Analysis - Coming Soon!");
        } else if (e.getSource() == budgetButton) {
            JOptionPane.showMessageDialog(this, "Budget - Coming Soon!");
        } else if (e.getSource() == dashboardButton) {
            JOptionPane.showMessageDialog(this, "You are already on Dashboard!");
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

    /* ===================== MAIN ===================== */

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            UserModel testUser = new UserModel("Vivek", "vivek@example.com", "pass123");
//            new DashboardScreen(testUser);
//        });
//    }
}