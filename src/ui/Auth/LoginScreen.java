package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import db.DbConnection;
import model.UserModel;
import util.Validator;

public class LoginScreen extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField passwordField;
    JButton loginButton;
    JLabel signupLabel, forgotLabel;

    public LoginScreen() {

        setTitle("Login");
        setSize(420,450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel background = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0,0,new Color(90,120,255),
                        getWidth(),getHeight(),new Color(180,80,200)
                );
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };

        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(300,320));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25,25,25,25));
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Serif",Font.BOLD,24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JLabel userLabel = new JLabel("Email");
        userLabel.setForeground(new Color(90,120,255));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = new JTextField();
        emailField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(new Color(90,120,255));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));

        forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setForeground(Color.GRAY);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordScreen();
                dispose();
            }
        });

        // Gradient Login Button
        loginButton = new JButton("Login") {

            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(40,120,255),
                        getWidth(),getHeight(),new Color(0,80,200)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);

                g2.dispose();

                super.paintComponent(g);
            }

            public boolean isOpaque() {
                return false;
            }
        };

        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(200,40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginButton.addActionListener(this);

        signupLabel = new JLabel("New User? Register");
        signupLabel.setForeground(new Color(90,120,255));
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                new SignUpScreen();
                dispose();
            }
        });

        card.add(userLabel);
        card.add(emailField);
        card.add(Box.createVerticalStrut(15));

        card.add(passLabel);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        card.add(forgotLabel);
        card.add(Box.createVerticalStrut(20));

        card.add(loginButton);
        card.add(Box.createVerticalStrut(20));

        card.add(signupLabel);

        background.add(card);
        add(background);

        getRootPane().setDefaultButton(loginButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==loginButton){

            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if(Validator.isEmpty(email) || Validator.isEmpty(password)){
                JOptionPane.showMessageDialog(this,"Fields cannot be empty");
                return;
            }

            if(!Validator.isValidEmail(email)){
                JOptionPane.showMessageDialog(this,"Invalid email format");
                return;
            }

            try{

                UserModel user = new UserModel("",email,password);
                DbConnection db = new DbConnection();

                boolean valid = db.checkUser(user);

                if(valid){

                    JOptionPane.showMessageDialog(this,"Login Successful");

                    // new DashboardScreen();
                    // dispose();

                }else{
                    JOptionPane.showMessageDialog(this,"Invalid Email or Password");
                }

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}