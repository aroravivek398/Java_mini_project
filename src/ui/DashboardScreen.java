package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

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

    // Stat labels (update these when real data is available)
    JLabel totalExpenseValue;
    JLabel thisMonthValue;
    JLabel budgetLeftValue;

    // Recent transactions panel
    JPanel transactionsPanel;

    public DashboardScreen(UserModel user) {

        this.user = user;

        setTitle("Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ===================== LEFT SIDEBAR ===================== */

        JPanel sidebar = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(90, 120, 255),
                        0, getHeight(), new Color(150, 70, 210)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        /* App Name */
        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sidebarSep = new JSeparator();
        sidebarSep.setForeground(new Color(255, 255, 255, 60));
        sidebarSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        /* Welcome */
        String name = (user != null && user.getName() != null && !user.getName().isEmpty())
                      ? user.getName() : "User";

        JLabel welcomeText = new JLabel("Welcome,");
        welcomeText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeText.setForeground(new Color(200, 210, 255));
        welcomeText.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userName = new JLabel(name);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sidebarSep);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(welcomeText);
        sidebar.add(userName);
        sidebar.add(Box.createVerticalStrut(30));

        /* Nav Buttons */
        dashboardButton    = createSidebarButton("Dashboard",      "\uD83C\uDFE0");
        addExpenseButton   = createSidebarButton("Add Expense",    "+");
        viewExpensesButton = createSidebarButton("View Expenses",  "\uD83D\uDCCB");
        analysisButton     = createSidebarButton("Analysis",       "\uD83D\uDCCA");
        budgetButton       = createSidebarButton("Budget",         "\uD83D\uDCB0");

        dashboardButton.addActionListener(this);
        addExpenseButton.addActionListener(this);
        viewExpensesButton.addActionListener(this);
        analysisButton.addActionListener(this);
        budgetButton.addActionListener(this);

        // Highlight active button
        dashboardButton.setBackground(new Color(255, 255, 255, 40));

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

        /* Logout */
        logoutButton = createSidebarButton("Logout", "\u21BA");
        logoutButton.addActionListener(this);
        sidebar.add(logoutButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel();
        content.setBackground(new Color(245, 246, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(35, 35, 35, 35));

        /* Dashboard Title */
        JLabel dashTitle = new JLabel("Dashboard");
        dashTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        dashTitle.setForeground(new Color(40, 40, 40));
        dashTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dashSub = new JLabel("Here's your expense overview");
        dashSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dashSub.setForeground(new Color(150, 150, 170));
        dashSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(dashTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(dashSub);
        content.add(Box.createVerticalStrut(25));

        /* ---- Stat Cards Row ---- */
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        totalExpenseValue = new JLabel();
        thisMonthValue    = new JLabel("\u20B90.00");
        budgetLeftValue   = new JLabel("\u20B90.00");

        statsRow.add(createStatCard("Total Expenses", totalExpenseValue, new Color(100, 100, 230)));
        statsRow.add(createStatCard("This Month",     thisMonthValue,    new Color(220, 70,  100)));
        statsRow.add(createStatCard("Budget Left",    budgetLeftValue,   new Color(50,  180, 130)));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(20));

        /* ---- Recent Transactions ---- */
        JPanel recentCard = new JPanel();
        recentCard.setBackground(Color.WHITE);
        recentCard.setLayout(new BorderLayout());
        recentCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        recentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel recentTitle = new JLabel("Recent Transactions");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        recentTitle.setForeground(new Color(40, 40, 40));

        JLabel noData = new JLabel("No transactions yet. Click 'Add Expense' to get started.");
        noData.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

    /* ---------- Stat Card Helper ---------- */

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {

        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
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
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(255, 255, 255, 200));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);

        return card;
    }

    /* ---------- Sidebar Button Helper ---------- */

    private JButton createSidebarButton(String text, String icon) {

        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(new Color(255, 255, 255, 30));
                btn.repaint();
            }
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
                btn.repaint();
            }
        });

        return btn;
    }

    /* ---------- ActionListener ---------- */

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addExpenseButton) {

             new AddExpenseScreen(user);
             dispose();
            //JOptionPane.showMessageDialog(this, "Add Expense - Coming Soon!");

        } else if (e.getSource() == viewExpensesButton) {

            // TODO: new ViewExpensesScreen(user);
            // dispose();
            JOptionPane.showMessageDialog(this, "View Expenses - Coming Soon!");

        } else if (e.getSource() == analysisButton) {

            // TODO: new AnalysisScreen(user);
            // dispose();
            JOptionPane.showMessageDialog(this, "Analysis - Coming Soon!");

        } else if (e.getSource() == budgetButton) {

            // TODO: new BudgetScreen(user);
            // dispose();
            JOptionPane.showMessageDialog(this, "Budget - Coming Soon!");

        } else if (e.getSource() == dashboardButton) {

            // Already on dashboard — do nothing
            JOptionPane.showMessageDialog(this, "You are already on Dashboard!");

        } else if (e.getSource() == logoutButton) {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginScreen();
                dispose();
            }
        }
    }
}