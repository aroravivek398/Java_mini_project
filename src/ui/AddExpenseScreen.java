package ui;

import javax.swing.*;
import javax.swing.border.*;

import db.ExpenseDAO;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import model.ExpenseModel;
import model.UserModel;

public class AddExpenseScreen extends JFrame implements ActionListener {

    private DashboardScreen dashboard;
    private UserModel user;

    private JTextField amountField;
    private JComboBox<String> categoryDropdown;
    private JPanel customCategoryPanel;
    private JTextField customCategoryField;
    private JTextField dateField;
    private JTextArea descriptionField;

    private JButton saveButton;
    private JButton cancelButton;

    private static final Color GRAD_TOP   = new Color(90, 120, 255);
    private static final Color GRAD_BTM   = new Color(150, 70, 210);
    private static final Color CARD_BG    = new Color(255, 255, 255, 220); 
    private static final Color LABEL_CLR  = new Color(60, 60, 90);
    private static final Color BORDER_CLR = new Color(210, 210, 230);
    private static final Color PH_COLOR   = new Color(180, 180, 205);
    private static final Color TEXT_COLOR = new Color(30, 30, 60);

    public AddExpenseScreen(DashboardScreen dashboard, UserModel user) {
        this.dashboard = dashboard;
        this.user      = user;

        setTitle("Add Expense");
        setSize(480, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, getWidth(), getHeight(), GRAD_BTM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new GridBagLayout()); 
        root.setOpaque(true);
        setContentPane(root);

        
        
        //add expense card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 235));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 32, 30, 32));

        // Card size
        card.setPreferredSize(new Dimension(400, 560));
        card.setMaximumSize(new Dimension(400, 560));

        
        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(GRAD_TOP);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Fill in the details below");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(150, 150, 180));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(24));

        
        card.add(createLabel("Amount (\u20B9)"));
        card.add(Box.createVerticalStrut(6));
        amountField = createTextField("e.g. 250");
        card.add(amountField);
        card.add(Box.createVerticalStrut(18));

        
        card.add(createLabel("Category"));
        card.add(Box.createVerticalStrut(6));
        String[] cats = {"Food", "Transport", "Shopping", "Bills", "Health", "Other (Custom)"};
        categoryDropdown = new JComboBox<>(cats);
        styleDropdown(categoryDropdown);
        categoryDropdown.addActionListener(this);
        card.add(categoryDropdown);
        card.add(Box.createVerticalStrut(10));

        
        customCategoryPanel = new JPanel();
        customCategoryPanel.setLayout(new BoxLayout(customCategoryPanel, BoxLayout.Y_AXIS));
        customCategoryPanel.setOpaque(false);
        customCategoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customCategoryPanel.setVisible(false);

        customCategoryPanel.add(createLabel("Custom Category"));
        customCategoryPanel.add(Box.createVerticalStrut(6));
        customCategoryField = createTextField("e.g. Gym, Rent...");
        customCategoryPanel.add(customCategoryField);
        customCategoryPanel.add(Box.createVerticalStrut(10));

        card.add(customCategoryPanel);

        
        card.add(createLabel("Date"));
        card.add(Box.createVerticalStrut(6));
        dateField = createTextField("DD/MM/YYYY");
        dateField.setForeground(TEXT_COLOR);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        card.add(dateField);
        card.add(Box.createVerticalStrut(18));

        
        card.add(createLabel("Description / Note"));
        card.add(Box.createVerticalStrut(6));

        descriptionField = new JTextArea(3, 1);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionField.setForeground(PH_COLOR);
        descriptionField.setText("Optional note...");
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBackground(Color.WHITE);
        descriptionField.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descriptionField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (descriptionField.getText().equals("Optional note...")) {
                    descriptionField.setText("");
                    descriptionField.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (descriptionField.getText().trim().isEmpty()) {
                    descriptionField.setForeground(PH_COLOR);
                    descriptionField.setText("Optional note...");
                }
            }
        });

        JScrollPane descScroll = new JScrollPane(descriptionField);
        descScroll.setBorder(null);
        descScroll.setOpaque(false);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));
        card.add(descScroll);
        card.add(Box.createVerticalStrut(28));

        
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        cancelButton = createActionButton("Cancel",       new Color(230, 230, 240), LABEL_CLR,  false);
        saveButton   = createActionButton("Save Expense", GRAD_TOP,                 Color.WHITE, true);

        cancelButton.addActionListener(this);
        saveButton.addActionListener(this);

        buttonRow.add(cancelButton);
        buttonRow.add(saveButton);
        card.add(buttonRow);

        
        root.add(card);

        setVisible(true);
    }

  
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == categoryDropdown) {
            boolean isCustom = "Other (Custom)".equals(categoryDropdown.getSelectedItem());
            customCategoryPanel.setVisible(isCustom);
            setSize(480, isCustom ? 710 : 640);
            revalidate();
            repaint();
            return;
        }

        if (e.getSource() == cancelButton) {
            dispose();

        }   else if (e.getSource() == saveButton) {
            if (!validateForm()) return;
            String category    = getSelectedCategory();
            double amount      = Double.parseDouble(amountField.getText().trim());
            
            String[] parts = dateField.getText().trim().split("/");
            String date    = parts[2] + "-" + parts[1] + "-" + parts[0];
            
            String description = descriptionField.getText().trim().equals("Optional note...") 
                                 ? "" 
                                 : descriptionField.getText().trim();

            ExpenseModel expense = new ExpenseModel(user.getId(), date, category, amount, description);

            try {
                ExpenseDAO dao   = new ExpenseDAO();
                boolean success  = dao.addExpense(expense);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Expense saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    DashboardScreen ds=new DashboardScreen(user);
                    dashboard.loadDashboardData();
                    dashboard.setVisible(true);
                    dispose();
                } else {
                    showError("Failed to save expense. Please try again!");
                }
            } catch (Exception ex) {
                showError("DB Error: " + ex.getMessage());
            }
        }
    }

    
    
    private boolean validateForm() {
        String amt = amountField.getText().trim();
        if (amt.isEmpty() || amt.equals("e.g. 250")) {
            showError("Amount cannot be empty!"); return false;
        }
        try {
            if (Double.parseDouble(amt) <= 0) {
                showError("Amount must be greater than 0!"); return false;
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric amount!"); return false;
        }

        if ("Other (Custom)".equals(categoryDropdown.getSelectedItem())) {
            String c = customCategoryField.getText().trim();
            if (c.isEmpty() || c.equals("e.g. Gym, Rent...")) {
                showError("Please enter a custom category!"); return false;
            }
        }

        String dt = dateField.getText().trim();
        if (dt.isEmpty()) { showError("Date cannot be empty!"); return false; }
        try {
            DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(dt);
        } catch (Exception ex) {
            showError("Enter date in DD/MM/YYYY format!"); return false;
        }

        return true;
    }

    public String getSelectedCategory() {
        if ("Other (Custom)".equals(categoryDropdown.getSelectedItem()))
            return customCategoryField.getText().trim();
        return (String) categoryDropdown.getSelectedItem();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

   
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(LABEL_CLR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(PH_COLOR);
        field.setText(placeholder);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(""); field.setForeground(TEXT_COLOR);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(PH_COLOR); field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private void styleDropdown(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
        box.setForeground(TEXT_COLOR);
        box.setBorder(new LineBorder(BORDER_CLR, 1, true));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setFocusable(false);
    }

    private JButton createActionButton(String text, Color bg, Color fg, boolean gradient) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (gradient) {
                    g2.setPaint(new GradientPaint(0, 0, GRAD_TOP, getWidth(), getHeight(), GRAD_BTM));
                } else {
                    g2.setColor(bg);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    

    
}