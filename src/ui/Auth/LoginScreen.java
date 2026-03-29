package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;

import db.DbConnection;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import model.UserModel;
import ui.DashboardScreen;
import util.Validator;

public class LoginScreen extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton;
    JLabel signupLabel, forgotLabel;

    public LoginScreen() {

        setTitle("Login");
        setSize(420, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       
        //background Gradient Color
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

        
        //Login card
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(300, 340));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

       

        //login Title
        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(24));

        

        // email and password field
        emailField    = new JTextField();
        passwordField = new JPasswordField();
        card.add(createInput("Email", emailField));
        card.add(Box.createVerticalStrut(14));
        card.add(createInput("Password", passwordField));
        card.add(Box.createVerticalStrut(10));

        
        
        //forgot password
        forgotLabel = new JLabel("Forgot Password?");  
        forgotLabel.setForeground(Color.GRAY);
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordScreen();
                dispose();
            }
        });

        card.add(forgotLabel);
        card.add(Box.createVerticalStrut(22));  

        
        
        
        //login button
        loginButton = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(30, 100, 200));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(30, 100, 200));
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
        loginButton.setRolloverEnabled(true);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginButton.setPreferredSize(new Dimension(240, 42));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        card.add(loginButton);
        card.add(Box.createVerticalStrut(18));
        
        
        

        
        //sign up button
        signupLabel = new JLabel("New User? Register");
        signupLabel.setForeground(new Color(90, 120, 255));
        signupLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupLabel.addMouseListener(new MouseAdapter() {
        	public void mouseEntered(MouseEvent e) {
                signupLabel.setForeground(new Color(60, 90, 220));
            }
            public void mouseExited(MouseEvent e) {
                signupLabel.setForeground(new Color(90, 120, 255));
            }
            public void mouseClicked(MouseEvent e) {
            	new SignUpScreen().setVisible(true);
                dispose();
            }
        });
        card.add(signupLabel);
        background.add(card);
        add(background);
        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }



    //panel for email and password
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
        field.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
        panel.add(field);

        return panel;
    }


    //action listener
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
//            String email    = emailField.getText().trim();
        	String email="nilesh@test.com";
//            String password = new String(passwordField.getPassword());
        	String password="n@123";
            if (Validator.isEmpty(email) || Validator.isEmpty(password)) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty");
                return;
            }
            if (!Validator.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Invalid email format");
                return;
            }
            try{
                DbConnection db = new DbConnection();
                UserModel    user = db.loginUser(email, password);
                if (user != null) {
                    new DashboardScreen(user);
                    dispose();
                }else {
                    JOptionPane.showMessageDialog(this, "Invalid Email or Password");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        new LoginScreen();
    }
}