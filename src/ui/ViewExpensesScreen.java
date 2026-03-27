package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import db.ExpenseDAO;
import model.ExpenseModel;
import model.UserModel;

public class ViewExpensesScreen extends JFrame implements ActionListener {

    private DashboardScreen dashboard;
    private UserModel user;

    // Filters
    private JComboBox<String> categoryFilter;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JButton applyFilterButton;
    private JButton clearFilterButton;

    // Table
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel emptyLabel;


    // ✅ Expense IDs store karne ke liye — row index se id milegi
    private ArrayList<Integer> expenseIds = new ArrayList<>();

    // Action buttons
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;

    // Colors
    private static final Color GRAD_TOP   = new Color(90, 120, 255);
    private static final Color GRAD_BTM   = new Color(150, 70, 210);
    private static final Color BG_COLOR   = new Color(245, 246, 252);
    private static final Color BORDER_CLR = new Color(210, 210, 230);
    private static final Color TEXT_COLOR = new Color(30, 30, 60);
    private static final Color ROW_ALT    = new Color(245, 245, 255);

    public ViewExpensesScreen(DashboardScreen dashboard, UserModel user) {
        this.dashboard = dashboard;
        this.user      = user;

        setTitle("View Expenses");
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

        JLabel filterTitle = new JLabel("Filters");
        filterTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        filterTitle.setForeground(Color.WHITE);
        filterTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appName);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(24));
        sidebar.add(filterTitle);
        sidebar.add(Box.createVerticalStrut(16));

        sidebar.add(createSidebarLabel("Category"));
        sidebar.add(Box.createVerticalStrut(6));
        String[] cats = {"All", "Food", "Transport", "Shopping", "Bills", "Health", "Other"};
        categoryFilter = new JComboBox<>(cats);
        categoryFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        categoryFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryFilter.setFocusable(false);
        sidebar.add(categoryFilter);
        sidebar.add(Box.createVerticalStrut(16));

        sidebar.add(createSidebarLabel("From Date (DD/MM/YYYY)"));
        sidebar.add(Box.createVerticalStrut(6));
        fromDateField = createSidebarTextField("DD/MM/YYYY");
        sidebar.add(fromDateField);
        sidebar.add(Box.createVerticalStrut(12));

        sidebar.add(createSidebarLabel("To Date (DD/MM/YYYY)"));
        sidebar.add(Box.createVerticalStrut(6));
        toDateField = createSidebarTextField("DD/MM/YYYY");
        sidebar.add(toDateField);
        sidebar.add(Box.createVerticalStrut(20));

        applyFilterButton = createSidebarButton("Apply Filter", true);
        applyFilterButton.addActionListener(this);
        sidebar.add(applyFilterButton);
        sidebar.add(Box.createVerticalStrut(10));

        clearFilterButton = createSidebarButton("Clear", false);
        clearFilterButton.addActionListener(this);
        sidebar.add(clearFilterButton);

        sidebar.add(Box.createVerticalGlue());

        backButton = createSidebarButton("Back to Dashboard", false);
        backButton.addActionListener(this);
        sidebar.add(backButton);

        /* ===================== MAIN CONTENT ===================== */

        JPanel content = new JPanel();
        content.setBackground(BG_COLOR);
        content.setOpaque(true);
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel("All Expenses");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        pageTitle.setForeground(TEXT_COLOR);

        JLabel pageSub = new JLabel("Sorted by date (latest first)");
        pageSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pageSub.setForeground(new Color(150, 150, 180));

        titleText.add(pageTitle);
        titleText.add(Box.createVerticalStrut(3));
        titleText.add(pageSub);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        editButton   = createTopButton("Edit",   new Color(100, 100, 230), Color.WHITE);
        deleteButton = createTopButton("Delete", new Color(220, 70,  100), Color.WHITE);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        titleRow.add(titleText,   BorderLayout.WEST);
        titleRow.add(actionPanel, BorderLayout.EAST);

        content.add(titleRow, BorderLayout.NORTH);

        /* ---- Table ---- */
        String[] columns = {"Sr. No.", "Category", "Amount (\u20B9)", "Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        expenseTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }
                return c;
            }
        };

        styleTable(expenseTable);

        scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(new LineBorder(BORDER_CLR, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);

        emptyLabel = new JLabel("No expenses found.");
        emptyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emptyLabel.setForeground(new Color(170, 170, 190));
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        tableWrapper.add(emptyLabel, BorderLayout.SOUTH);

        content.add(tableWrapper, BorderLayout.CENTER);

        add(sidebar,  BorderLayout.WEST);
        add(content,  BorderLayout.CENTER);

        loadFromDB();

        setVisible(true);
    }

    /* ===================== DB SE LOAD ===================== */

    private void loadFromDB() {
        try {
            ExpenseDAO dao = new ExpenseDAO();
            // ✅ FIX: DB se ORDER BY date DESC — latest pehle
            ArrayList<ExpenseModel> expenses = dao.getAllExpense(user.getId());
            loadExpenses(expenses);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading expenses: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            checkEmpty();
        }
    }

    /* ===================== EMPTY CHECK ===================== */

    private void checkEmpty() {
        boolean isEmpty = tableModel.getRowCount() == 0;
        scrollPane.setVisible(!isEmpty);
        emptyLabel.setVisible(isEmpty);
        revalidate();
        repaint();
    }

    /* ===================== LOAD DATA ===================== */

    public void loadExpenses(ArrayList<ExpenseModel> expenses) {
        tableModel.setRowCount(0);

        expenseIds.clear(); // ✅ pehle clear karo

        int i = 1;
        for (ExpenseModel exp : expenses) {
            tableModel.addRow(new Object[]{
                i++,
                exp.category,
                String.format("%.2f", exp.amount),

                formatDate(exp.date),   // ✅ FIX: YYYY-MM-DD → DD/MM/YYYY

                formatDate(exp.date),

                exp.description
            });
            expenseIds.add(exp.getId()); // ✅ har row ka id save karo
        }
        checkEmpty();
    }

    /* ===================== DATE HELPERS ===================== */


    private String formatDate(String date) {
        try {
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return date;
        }
    }



    private String convertToDBDate(String displayDate) {
        try {
            String[] parts = displayDate.split("/");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        } catch (Exception e) {
            return displayDate;
        }
    }

    /* ===================== TABLE STYLING ===================== */

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(180, 190, 255));
        table.setSelectionForeground(TEXT_COLOR);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(GRAD_TOP);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(260);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    }

    /* ===================== ACTION LISTENER ===================== */

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == applyFilterButton) {
            String selectedCat = (String) categoryFilter.getSelectedItem();
            String from = fromDateField.getText().trim();
            String to   = toDateField.getText().trim();

            boolean catFilter  = !"All".equals(selectedCat);
            boolean dateFilter = !from.equals("DD/MM/YYYY") && !to.equals("DD/MM/YYYY")
                                  && !from.isEmpty() && !to.isEmpty();

            try {
                ExpenseDAO dao = new ExpenseDAO();
                ArrayList<ExpenseModel> filtered;

                if (dateFilter) {

                    // ✅ FIX: DD/MM/YYYY → YYYY-MM-DD convert karke DB mein query karo
                    String fromDB = convertToDBDate(from);
                    String toDB   = convertToDBDate(to);
                    filtered = dao.searchByDate(user.getId(), fromDB, toDB);

                    // Category bhi apply karo agar select hai

                    filtered = dao.searchByDate(user.getId(), fromDB, toDB);

                    if (catFilter) {
                        filtered.removeIf(exp -> !exp.category.equalsIgnoreCase(selectedCat));
                    }
                } else if (catFilter) {
                    filtered = dao.getExpenseWithCategory(user.getId(), selectedCat);
                } else {
                    filtered = dao.getAllExpense(user.getId());
                }

                loadExpenses(filtered);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error applying filter: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == clearFilterButton) {
            categoryFilter.setSelectedIndex(0);
            fromDateField.setText("DD/MM/YYYY");
            fromDateField.setForeground(new Color(200, 210, 255));
            toDateField.setText("DD/MM/YYYY");
            toDateField.setForeground(new Color(200, 210, 255));
            loadFromDB();

        } else if (e.getSource() == editButton) {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select an expense to edit!",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            

        } else if (e.getSource() == deleteButton) {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select an expense to delete!",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
//            JOptionPane.showMessageDialog(this, "Delete Expense - Coming Soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            try {
            	ExpenseDAO dao=new ExpenseDAO();
				int expenseId = dao.getAllExpense(user.getId()).get(selectedRow).getId();
			
				boolean isdeleted = dao.deleteExpense(expenseId);
				if(isdeleted) {
					ArrayList<ExpenseModel> updated;
					updated = dao.getAllExpense(user.getId());
					loadExpenses(updated);
					JOptionPane.showMessageDialog(this, "Deleted Successfully", "Successfull Deletion", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(this, "Not Deleted", "Unsuccessfull Deletion", JOptionPane.INFORMATION_MESSAGE);
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}

        } else if (e.getSource() == backButton) {
            dispose();
        }
    }

    /* ===================== HELPER METHODS ===================== */

    private JLabel createSidebarLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(new Color(200, 210, 255));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField createSidebarTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBackground(new Color(110, 135, 255));
        field.setForeground(new Color(200, 210, 255));
        field.setCaretColor(Color.WHITE);
        field.setText(placeholder);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 60), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(new Color(200, 210, 255));
                    field.setText(placeholder);
                }
            }
        });
        return field;
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

    private JButton createTopButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 36));
        return btn;
    }
}