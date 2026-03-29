package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;

import db.BudgetDAO;
import db.ExpenseDAO;
import model.BudgetModel;
import model.UserModel;

public class BudgetScreen extends JFrame implements ActionListener {

    private DashboardScreen dashboard;
    private UserModel user;

    private JButton backButton;
    private JButton saveButton;

    // Budget input fields — category wise
    private JTextField totalBudgetField;
    private JTextField foodField;
    private JTextField transportField;
    private JTextField shoppingField;
    private JTextField billsField;
    private JTextField healthField;
    private JTextField otherField;

    // Month/Year selector
    private JComboBox<String> monthSelector;
    private JComboBox<String> yearSelector;

    // Summary panel
    private JPanel summaryPanel;

    // Colors
    private static final Color GRAD_TOP   = new Color(90, 120, 255);
    private static final Color GRAD_BTM   = new Color(150, 70, 210);
    private static final Color BG_COLOR   = new Color(245, 246, 252);
    private static final Color TEXT_COLOR = new Color(30, 30, 60);
    private static final Color BORDER_CLR = new Color(210, 210, 230);
    private static final Color LABEL_CLR  = new Color(60, 60, 90);
    private static final Color SUCCESS    = new Color(50, 180, 130);
    private static final Color DANGER     = new Color(220, 70, 100);
    private static final Color WARNING    = new Color(255, 160, 50);

    private static final String[] MONTHS = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public BudgetScreen(DashboardScreen dashboard, UserModel user) {
        this.dashboard = dashboard;
        this.user      = user;

        setTitle("Budget");
        setSize(950, 650);
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
        sidebar.setPreferredSize(new Dimension(230, 650));
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

        JLabel sideTitle = new JLabel("Budget Planner");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        sideTitle.setForeground(Color.WHITE);
        sideTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sideSub = new JLabel("Set & track your limits");
        sideSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sideSub.setForeground(new Color(200, 210, 255));
        sideSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Month selector
        JLabel monthLabel = new JLabel("Month");
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        monthLabel.setForeground(new Color(200, 210, 255));
        monthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        monthSelector = new JComboBox<>(MONTHS);
        monthSelector.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthSelector.setFont(new Font("SansSerif", Font.PLAIN, 13));
        monthSelector.setBackground(Color.WHITE);
        monthSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        monthSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthSelector.setFocusable(false);

        // Year selector
        JLabel yearLabel = new JLabel("Year");
        yearLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        yearLabel.setForeground(new Color(200, 210, 255));
        yearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int currentYear = LocalDate.now().getYear();
        String[] years = {
            String.valueOf(currentYear - 1),
            String.valueOf(currentYear),
            String.valueOf(currentYear + 1)
        };
        yearSelector = new JComboBox<>(years);
        yearSelector.setSelectedIndex(1);
        yearSelector.setFont(new Font("SansSerif", Font.PLAIN, 13));
        yearSelector.setBackground(Color.WHITE);
        yearSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        yearSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        yearSelector.setFocusable(false);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(sideTitle);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(sideSub);
        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(monthLabel);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(monthSelector);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(yearLabel);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(yearSelector);
        sidebar.add(Box.createVerticalGlue());

        backButton = createSidebarButton("Back to Dashboard", false);
        backButton.addActionListener(this);
        sidebar.add(backButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_COLOR);
        content.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel pageTitle = new JLabel("Monthly Budget");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_COLOR);

        JLabel pageSub = new JLabel("Set your budget and compare with actual expenses");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(new Color(150, 150, 180));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(pageTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(pageSub);
        titlePanel.setBorder(new EmptyBorder(0, 0, 18, 0));

        content.add(titlePanel, BorderLayout.NORTH);

        /* ---- Two column layout: Input | Summary ---- */
        JPanel mainRow = new JPanel(new GridLayout(1, 2, 20, 0));
        mainRow.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(mainRow);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        /* ---- LEFT: Budget Input Card ---- */
        JPanel inputCard = createCard();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));

        JLabel inputTitle = new JLabel("Set Budget (\u20B9)");
        inputTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        inputTitle.setForeground(GRAD_TOP);
        inputTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputCard.add(inputTitle);
        inputCard.add(Box.createVerticalStrut(16));

        // Total budget
        inputCard.add(createFieldLabel("Total Monthly Budget"));
        inputCard.add(Box.createVerticalStrut(5));
        totalBudgetField = createInputField("e.g. 10000");
        inputCard.add(totalBudgetField);
        inputCard.add(Box.createVerticalStrut(14));

        JSeparator divider = new JSeparator();
        divider.setForeground(BORDER_CLR);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel catLabel = new JLabel("Category Wise Budget");
        catLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        catLabel.setForeground(LABEL_CLR);
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputCard.add(divider);
        inputCard.add(Box.createVerticalStrut(12));
        inputCard.add(catLabel);
        inputCard.add(Box.createVerticalStrut(10));

        // Category fields
        inputCard.add(createFieldLabel("Food"));
        inputCard.add(Box.createVerticalStrut(4));
        foodField = createInputField("e.g. 3000");
        inputCard.add(foodField);
        inputCard.add(Box.createVerticalStrut(10));

        inputCard.add(createFieldLabel("Transport"));
        inputCard.add(Box.createVerticalStrut(4));
        transportField = createInputField("e.g. 1500");
        inputCard.add(transportField);
        inputCard.add(Box.createVerticalStrut(10));

        inputCard.add(createFieldLabel("Shopping"));
        inputCard.add(Box.createVerticalStrut(4));
        shoppingField = createInputField("e.g. 2000");
        inputCard.add(shoppingField);
        inputCard.add(Box.createVerticalStrut(10));

        inputCard.add(createFieldLabel("Bills"));
        inputCard.add(Box.createVerticalStrut(4));
        billsField = createInputField("e.g. 2000");
        inputCard.add(billsField);
        inputCard.add(Box.createVerticalStrut(10));

        inputCard.add(createFieldLabel("Health"));
        inputCard.add(Box.createVerticalStrut(4));
        healthField = createInputField("e.g. 1000");
        inputCard.add(healthField);
        inputCard.add(Box.createVerticalStrut(10));

        inputCard.add(createFieldLabel("Other"));
        inputCard.add(Box.createVerticalStrut(4));
        otherField = createInputField("e.g. 500");
        inputCard.add(otherField);
        inputCard.add(Box.createVerticalStrut(18));

        // Save button
        saveButton = createActionButton("Save & Compare", GRAD_TOP, Color.WHITE);
        saveButton.addActionListener(this);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        inputCard.add(saveButton);

        /* ---- RIGHT: Summary Card ---- */
        summaryPanel = createCard();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));

        JLabel summaryTitle = new JLabel("Summary");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        summaryTitle.setForeground(GRAD_TOP);
        summaryTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel summaryHint = new JLabel("Click 'Save & Compare' to see results");
        summaryHint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryHint.setForeground(new Color(170, 170, 190));
        summaryHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryPanel.add(summaryTitle);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(summaryHint);

        mainRow.add(inputCard);
        mainRow.add(summaryPanel);

        content.add(scrollPane, BorderLayout.CENTER);

        
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        
        prefillExistingBudget();

        setVisible(true);
    }


    // ✅ DB se current month ka budget fetch karke fields mein fill karo
    private void prefillExistingBudget() {
        try {
            BudgetDAO budgetDao = new BudgetDAO();
            BudgetModel existing = budgetDao.getCurrentBudget(user.getId()); 
            if (existing != null) {
                setFieldValue(totalBudgetField, "e.g. 10000", existing.budget_amount);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Helper — field mein double value set karo aur placeholder hata do
    private void setFieldValue(JTextField field, String placeholder, double value) {
        if (value > 0) {
            field.setText(String.format("%.2f", value));
            field.setForeground(TEXT_COLOR);
        }
    }

    /* ===================== ACTION LISTENER ===================== */

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            saveAndCompare();
        } else if (e.getSource() == backButton) {
            dispose();
        }
    }

    /* ===================== SAVE & COMPARE ===================== */

    private void saveAndCompare() {

        // Validate fields
        double totalBudget     = parseField(totalBudgetField, "Total Budget");
        if (totalBudget < 0) return;

        double foodBudget      = parseField(foodField,       "Food");
        double transportBudget = parseField(transportField,  "Transport");
        double shoppingBudget  = parseField(shoppingField,   "Shopping");
        double billsBudget     = parseField(billsField,      "Bills");
        double healthBudget    = parseField(healthField,     "Health");
        double otherBudget     = parseField(otherField,      "Other");

        if (foodBudget < 0 || transportBudget < 0 || shoppingBudget < 0 ||
            billsBudget < 0 || healthBudget < 0 || otherBudget < 0) return;

        // Total budget zero nahi hona chahiye
        if (totalBudget == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a Total Monthly Budget!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedMonth = monthSelector.getSelectedIndex() + 1;
        int selectedYear  = Integer.parseInt((String) yearSelector.getSelectedItem());

        // ✅ FIX 2: Budget DB mein save karo
        try {
            BudgetModel budget = new BudgetModel(user.getId(), selectedMonth, selectedYear,totalBudget);
            new BudgetDAO().setBudget(user.getId(),budget);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving budget: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ FIX 3: DB se category-wise expenses fetch karo (Java filter nahi, DB query)
        double totalSpent = 0, foodSpent = 0, transportSpent = 0,
               shoppingSpent = 0, billsSpent = 0, healthSpent = 0, otherSpent = 0;

        try {
            ExpenseDAO expenseDao = new ExpenseDAO();
            foodSpent      = expenseDao.getMonthlyExpenseByCategory(user.getId(), "food",      selectedMonth, selectedYear);
            transportSpent = expenseDao.getMonthlyExpenseByCategory(user.getId(), "transport", selectedMonth, selectedYear);
            shoppingSpent  = expenseDao.getMonthlyExpenseByCategory(user.getId(), "shopping",  selectedMonth, selectedYear);
            billsSpent     = expenseDao.getMonthlyExpenseByCategory(user.getId(), "bills",     selectedMonth, selectedYear);
            healthSpent    = expenseDao.getMonthlyExpenseByCategory(user.getId(), "health",    selectedMonth, selectedYear);
            otherSpent     = expenseDao.getMonthlyExpenseByCategory(user.getId(), "other",     selectedMonth, selectedYear);
            totalSpent = expenseDao.getMonthlyTotal(user.getId(), selectedMonth, selectedYear);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading expenses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Summary panel update karo
        updateSummary(
            selectedMonth, selectedYear,
            totalBudget,      totalSpent,
            foodBudget,       foodSpent,
            transportBudget,  transportSpent,
            shoppingBudget,   shoppingSpent,
            billsBudget,      billsSpent,
            healthBudget,     healthSpent,
            otherBudget,      otherSpent
        );
    }

    /* ===================== UPDATE SUMMARY ===================== */

    private void updateSummary(
            int month, int year,
            double totalBudget, double totalSpent,
            double foodB,   double foodS,
            double transB,  double transS,
            double shopB,   double shopS,
            double billsB,  double billsS,
            double healthB, double healthS,
            double otherB,  double otherS) {

        summaryPanel.removeAll();

        JLabel title = new JLabel("Summary — " + MONTHS[month - 1] + " " + year);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(GRAD_TOP);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(title);
        summaryPanel.add(Box.createVerticalStrut(14));

        // Total row
        summaryPanel.add(createSummaryRow("Total", totalBudget, totalSpent, true));
        summaryPanel.add(Box.createVerticalStrut(6));

        JSeparator div = new JSeparator();
        div.setForeground(BORDER_CLR);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(div);
        summaryPanel.add(Box.createVerticalStrut(10));

        // Category rows — sirf wo dikhao jinka budget set hai
        if (foodB   > 0) { summaryPanel.add(createSummaryRow("Food",      foodB,   foodS,   false)); summaryPanel.add(Box.createVerticalStrut(8)); }
        if (transB  > 0) { summaryPanel.add(createSummaryRow("Transport", transB,  transS,  false)); summaryPanel.add(Box.createVerticalStrut(8)); }
        if (shopB   > 0) { summaryPanel.add(createSummaryRow("Shopping",  shopB,   shopS,   false)); summaryPanel.add(Box.createVerticalStrut(8)); }
        if (billsB  > 0) { summaryPanel.add(createSummaryRow("Bills",     billsB,  billsS,  false)); summaryPanel.add(Box.createVerticalStrut(8)); }
        if (healthB > 0) { summaryPanel.add(createSummaryRow("Health",    healthB, healthS, false)); summaryPanel.add(Box.createVerticalStrut(8)); }
        if (otherB  > 0) { summaryPanel.add(createSummaryRow("Other",     otherB,  otherS,  false)); summaryPanel.add(Box.createVerticalStrut(8)); }

        // Budget exceed warning
        if (totalSpent > totalBudget && totalBudget > 0) {
            summaryPanel.add(Box.createVerticalStrut(6));
            JLabel warning = new JLabel("\u26A0 Budget exceeded by \u20B9" +
                    String.format("%.2f", totalSpent - totalBudget) + "!");
            warning.setFont(new Font("SansSerif", Font.BOLD, 13));
            warning.setForeground(DANGER);
            warning.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(warning);
        } else if (totalBudget > 0) {
            // ✅ Budget Left bhi dikhao
            summaryPanel.add(Box.createVerticalStrut(6));
            JLabel safe = new JLabel("\u2705 Budget Left: \u20B9" +
                    String.format("%.2f", totalBudget - totalSpent));
            safe.setFont(new Font("SansSerif", Font.BOLD, 13));
            safe.setForeground(SUCCESS);
            safe.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(safe);
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    /* ===================== SUMMARY ROW ===================== */

    private JPanel createSummaryRow(String label, double budget, double spent, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("SansSerif", isBold ? Font.BOLD : Font.PLAIN, 13));
        nameLabel.setForeground(TEXT_COLOR);

        double percent = budget > 0 ? Math.min(spent / budget, 1.0) : 0;
        Color barColor = percent >= 1.0 ? DANGER : percent >= 0.8 ? WARNING : SUCCESS;

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int)(percent * 100));
        bar.setForeground(barColor);
        bar.setBackground(new Color(230, 230, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(0, 8));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));

        left.add(nameLabel);
        left.add(Box.createVerticalStrut(4));
        left.add(bar);

        String status = spent > budget && budget > 0 ? " \u26A0" : "";
        JLabel amtLabel = new JLabel(
            "\u20B9" + String.format("%.0f", spent) +
            " / \u20B9" + String.format("%.0f", budget) + status
        );
        amtLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        amtLabel.setForeground(spent > budget && budget > 0 ? DANGER : TEXT_COLOR);
        amtLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(left,     BorderLayout.CENTER);
        row.add(amtLabel, BorderLayout.EAST);

        return row;
    }

    /* ===================== HELPERS ===================== */

    private double parseField(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty() || text.startsWith("e.g.")) return 0;
        try {
            double val = Double.parseDouble(text);
            if (val < 0) {
                JOptionPane.showMessageDialog(this,
                        fieldName + " cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            return val;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid value in " + fieldName + "!", "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
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
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(LABEL_CLR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(180, 180, 205));
        field.setText(placeholder);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(new Color(180, 180, 205));
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, getWidth(), getHeight(), GRAD_BTM));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSidebarButton(String text, boolean filled) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (filled) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                    g2.setColor(Color.WHITE);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(filled ? GRAD_TOP : Color.WHITE);
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