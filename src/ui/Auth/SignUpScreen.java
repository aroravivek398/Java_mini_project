package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

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
        setSize(420,520);
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
        card.setPreferredSize(new Dimension(320,400));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25,25,25,25));
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Serif",Font.BOLD,22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JLabel nameLabel = new JLabel("Full Name");
        nameField = new JTextField();
        nameField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        JLabel emailLabel = new JLabel("Email");
        emailField = new JTextField();
        emailField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        JLabel passLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        JLabel confirmLabel = new JLabel("Re-enter Password");
        confirmField = new JPasswordField();
        confirmField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        signUpButton = new JButton("Sign Up");
        signUpButton.setFocusPainted(false);
        signUpButton.setMaximumSize(new Dimension(200,40));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(this);

        loginLabel = new JLabel("Already a member? Login");
        loginLabel.setForeground(new Color(90,120,255));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new LoginScreen();
                dispose();
            }
        });

        card.add(nameLabel);
        card.add(nameField);
        card.add(Box.createVerticalStrut(15));

        card.add(emailLabel);
        card.add(emailField);
        card.add(Box.createVerticalStrut(15));

        card.add(passLabel);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(15));

        card.add(confirmLabel);
        card.add(confirmField);
        card.add(Box.createVerticalStrut(25));

        card.add(signUpButton);
        card.add(Box.createVerticalStrut(20));

        card.add(loginLabel);

        background.add(card);

        add(background);
        getRootPane().setDefaultButton(signUpButton);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==signUpButton){

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            // empty check
            if(Validator.isEmpty(name) || Validator.isEmpty(email)
                    || Validator.isEmpty(password) || Validator.isEmpty(confirm)){

                JOptionPane.showMessageDialog(this,"All fields are required");
                return;
            }

            // email format check
            if(!Validator.isValidEmail(email)){

                JOptionPane.showMessageDialog(this,"Invalid email format");
                return;
            }

            // password length check
            if(!Validator.isValidPassword(password)){

                JOptionPane.showMessageDialog(this,"Password must be at least 5 characters");
                return;
            }

            // password match check
            if(!password.equals(confirm)){

                JOptionPane.showMessageDialog(this,"Passwords do not match");
                return;
            }

            try{

                DbConnection db = new DbConnection();

                // check email already exists
                if(db.emailExists(email)){

                    JOptionPane.showMessageDialog(
                            this,
                            "Email already registered.\nPlease login instead."
                    );
                    return;
                }

                UserModel user = new UserModel(name,email,password);

                boolean success = db.addUser(user);

                if(success){

                    JOptionPane.showMessageDialog(this,"Account created successfully!");

                    new LoginScreen();
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(this,"Registration failed");
                }

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new SignUpScreen();
    }
}