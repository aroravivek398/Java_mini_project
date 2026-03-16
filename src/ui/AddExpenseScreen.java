package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import db.ExpenseDAO;
import model.ExpenseModel;
import model.UserModel;

public class AddExpenseScreen extends JFrame implements ActionListener {

    private UserModel user;

    JComboBox<String> categoryDropdown;
    JTextField        amountField;
    JTextField        dateField;
    JTextField        descField;
    JButton           addButton;
    JButton           backButton;

    public AddExpenseScreen(UserModel user) {

        this.user = user;

        setTitle("Add Expense");
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

        JLabel appName = new JLabel("ExpenseTracker");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sidebarSep = new JSeparator();
        sidebarSep.setForeground(new Color(255, 255, 255, 60));
        sidebarSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        String name = (user != null && user.getName() != null) ? user.getName() : "User";

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

        sidebar.add(createSidebarButton("Dashboard",     "\uD83C\uDFE0", false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Add Expense",   "+",            true));   // active
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("View Expenses", "\uD83D\uDCCB", false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Analysis",      "\uD83D\uDCCA", false));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createSidebarButton("Budget",        "\uD83D\uDCB0", false));

        sidebar.add(Box.createVerticalGlue());

        backButton = createSidebarButton("\u2190  Back to Dashboard", "", false);
        backButton.addActionListener(this);
        sidebar.add(backButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel();
        content.setBackground(new Color(245, 246, 252));
        content.setLayout(new GridBagLayout());

        /* White Form Card */
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 240), 1, true),
                new EmptyBorder(35, 40, 35, 40)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 420));

        /* Title */
        JLabel title = new JLabel("Add New Expense");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Fill in the details below");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(160, 160, 180));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        /* Date Field */
        dateField = new JTextField();
        dateField.setToolTipText("YYYY-MM-DD");
        card.add(createInput("Date (YYYY-MM-DD)", dateField));
        card.add(Box.createVerticalStrut(16));

        /* Category Dropdown */
        String[] categories = { "Food", "Transport", "Shopping", "Bills", "Health", "Other" };
        categoryDropdown = new JComboBox<>(categories);
        categoryDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        categoryDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryDropdown.setBorder(new MatteBorder(0, 0, 2, 0, new Color(90, 120, 255)));

        JPanel catPanel = new JPanel();
        catPanel.setLayout(new BoxLayout(catPanel, BoxLayout.Y_AXIS));
        catPanel.setOpaque(false);
        catPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        catPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel catLabel = new JLabel("Category");
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catLabel.setForeground(new Color(110, 130, 255));
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        catPanel.add(catLabel);
        catPanel.add(Box.createVerticalStrut(3));
        catPanel.add(categoryDropdown);

        card.add(catPanel);
        card.add(Box.createVerticalStrut(16));

        /* Amount Field */
        amountField = new JTextField();
        card.add(createInput("Amount (\u20B9)", amountField));
        card.add(Box.createVerticalStrut(16));

        /* Description Field */
        descField = new JTextField();
        card.add(createInput("Description", descField));
        card.add(Box.createVerticalStrut(28));

        /* Add Button */
        addButton = new JButton("Add Expense") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(30, 100, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(60, 140, 255));
                } else {
                    g2.setColor(new Color(40, 120, 230));
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        addButton.setFocusPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addButton.setForeground(Color.WHITE);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(this);

        card.add(addButton);

        content.add(card);

        /* ===================== ASSEMBLE ===================== */

        add(sidebar,  BorderLayout.WEST);
        add(content,  BorderLayout.CENTER);

        setVisible(true);
    }

    /* ---------- createInput (same as all screens) ---------- */

    private JPanel createInput(String labelText, JTextField field) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(110, 130, 255));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new MatteBorder(0, 0, 2, 0, new Color(90, 120, 255)));
        field.setOpaque(false);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
        panel.add(field);

        return panel;
    }

    /* ---------- Sidebar Button Helper ---------- */

    private JButton createSidebarButton(String text, String icon, boolean active) {

        JButton btn = new JButton(icon.isEmpty() ? text : icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(active);
        btn.setOpaque(active);
        btn.setBackground(active ? new Color(255, 255, 255, 40) : new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));

        if (!active) {
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
        }

        return btn;
    }

    /* ---------- ActionListener ---------- */

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == backButton) {

            new DashboardScreen(user);
            dispose();

        } else if (e.getSource() == addButton) {

            String date   = dateField.getText().trim();
            String cat    = (String) categoryDropdown.getSelectedItem();
            String amount = amountField.getText().trim();
            String desc   = descField.getText().trim();

            /* Validation */

            if (date.isEmpty() || amount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date and Amount are required!");
                return;
            }

            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date format must be YYYY-MM-DD");
                return;
            }

            double amountVal;
            try {
                amountVal = Double.parseDouble(amount);
                if (amountVal <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid positive number!");
                return;
            }

            /* Save to DB */

            try {
                ExpenseModel expense = new ExpenseModel(
                        user.getId(), date, cat, amountVal, desc
                );

                ExpenseDAO dao = new ExpenseDAO();
                boolean success = dao.addExpense(expense);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Expense added successfully!");
                    // Clear fields
                    dateField.setText("");
                    amountField.setText("");
                    descField.setText("");
                    categoryDropdown.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add expense!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    /* ---------- Test ---------- */

    public static void main(String[] args) {
        UserModel testUser = new UserModel("Rahul", "rahul@example.com", "pass123");
        new AddExpenseScreen(testUser);
    }
}