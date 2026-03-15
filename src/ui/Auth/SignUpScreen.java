package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import db.DbConnection;
import model.UserModel;
import util.Validator;

public class SignUpScreen extends JFrame implements ActionListener {

    JTextField nameField, emailField;
    JPasswordField passwordField, confirmField;

    JButton signUpButton;
    JLabel loginLabel;

    public SignUpScreen() {

        setTitle("Register");
        setSize(420, 580);
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
        card.setPreferredSize(new Dimension(340, 490));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 35, 30, 35));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        /* ---------- Title ---------- */

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(24));

        /* ---------- Input Fields ---------- */

        nameField     = new JTextField();
        emailField    = new JTextField();
        passwordField = new JPasswordField();
        confirmField  = new JPasswordField();

        card.add(createInput("Full Name", nameField));
        card.add(Box.createVerticalStrut(14));
        card.add(createInput("Email", emailField));
        card.add(Box.createVerticalStrut(14));
        card.add(createInput("Password", passwordField));
        card.add(Box.createVerticalStrut(14));
        card.add(createInput("Re-enter Password", confirmField));
        card.add(Box.createVerticalStrut(24));

        /* ---------- Rounded Sign Up Button ---------- */

        signUpButton = new JButton("Sign Up") {
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
        signUpButton.setFocusPainted(false);
        signUpButton.setContentAreaFilled(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signUpButton.setPreferredSize(new Dimension(270, 42));
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(this);

        card.add(signUpButton);
        card.add(Box.createVerticalStrut(18));

        /* ---------- Login Link ---------- */

        loginLabel = new JLabel("Already a member? Login");
        loginLabel.setForeground(new Color(90, 120, 255));
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginScreen();
                dispose();
            }
        });

        card.add(loginLabel);

        background.add(card);
        add(background);
        getRootPane().setDefaultButton(signUpButton);
        setVisible(true);
    }

    /* ---------- Clean Input — small blue label + underline only ---------- */

    private JPanel createInput(String labelText, JComponent field) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        /* Small muted label above — like Login */
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(110, 130, 255));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        /* Field — transparent, just bottom border */
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

        if (e.getSource() == signUpButton) {

            String name     = nameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm  = new String(confirmField.getPassword());

            if (Validator.isEmpty(name) || Validator.isEmpty(email)
                    || Validator.isEmpty(password) || Validator.isEmpty(confirm)) {
                JOptionPane.showMessageDialog(this, "All fields are required");
                return;
            }
            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format");
                return;
            }
            if (!Validator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "Password must be at least 5 characters");
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }

            try {
                DbConnection db = new DbConnection();
                if (db.emailExists(email)) {
                    JOptionPane.showMessageDialog(this,
                            "Email already registered.\nPlease login instead.");
                    return;
                }
                UserModel user   = new UserModel(name, email, password);
                boolean  success = db.addUser(user);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    new LoginScreen();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new SignUpScreen();
    }
}