package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import db.DbConnection;
import util.Validator;

public class ForgotPasswordScreen extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField passwordField;
    JButton resetButton;
    JLabel backLabel;

    public ForgotPasswordScreen() {

        setTitle("Reset Password");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* ---------- Gradient Background ---------- */

        JPanel background = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(90, 120, 255),
                        getWidth(), getHeight(), new Color(180, 80, 200)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new GridBagLayout());

        /* ---------- White Card ---------- */

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(340, 340));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        /* ---------- Title ---------- */

        JLabel title = new JLabel("Reset Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(28));

        /* ---------- Fields ---------- */

        emailField    = new JTextField();
        passwordField = new JPasswordField();

        card.add(createInput("Email", emailField));
        card.add(Box.createVerticalStrut(14));
        card.add(createInput("New Password", passwordField));
        card.add(Box.createVerticalStrut(28));

        /* ---------- Rounded Reset Button ---------- */

        resetButton = new JButton("Reset Password") {
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
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        resetButton.setFocusPainted(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resetButton.setForeground(Color.WHITE);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        resetButton.setPreferredSize(new Dimension(270, 42));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(this);

        card.add(resetButton);
        card.add(Box.createVerticalStrut(18));

        /* ---------- Back to Login Link ---------- */

        backLabel = new JLabel("Back to Login");
        backLabel.setForeground(new Color(90, 120, 255));
        backLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginScreen();
                dispose();
            }
        });

        card.add(backLabel);

        background.add(card);
        add(background);
        setVisible(true);
    }

    /* ---------- Same createInput as SignUp ---------- */

    private JPanel createInput(String labelText, JComponent field) {

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

        if (field instanceof JTextField) {
            ((JTextField) field).setHorizontalAlignment(JTextField.LEFT);
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
        panel.add(field);

        return panel;
    }

    /* ---------- Action ---------- */

    @Override
    public void actionPerformed(ActionEvent e) {

        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (Validator.isEmpty(email) || Validator.isEmpty(password)) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format");
            return;
        }

        try {
            DbConnection db = new DbConnection();

            if (db.emailExists(email)) {
                db.updatePassword(email, password);
                JOptionPane.showMessageDialog(this, "Password Updated Successfully");
                new LoginScreen();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}