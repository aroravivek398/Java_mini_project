package ui.Auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import db.DbConnection;
import util.Validator;

public class ForgotPasswordScreen extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField passwordField;
    JButton resetButton;
    JLabel backLabel;

    public ForgotPasswordScreen() {

        setTitle("Reset Password");
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
        card.setPreferredSize(new Dimension(300,300));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25,25,25,25));
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reset Password");
        title.setFont(new Font("Serif",Font.BOLD,22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(new Color(90,120,255));

        emailField = new JTextField();
        emailField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        JLabel passLabel = new JLabel("New Password");
        passLabel.setForeground(new Color(90,120,255));

        passwordField = new JPasswordField();
        passwordField.setBorder(new MatteBorder(0,0,2,0,new Color(90,120,255)));

        resetButton = new JButton("Reset Password");
        resetButton.setFocusPainted(false);
        resetButton.setBorder(new LineBorder(Color.GRAY,1,true));
        resetButton.setMaximumSize(new Dimension(200,40));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton.addActionListener(this);

        backLabel = new JLabel("Back to Login");
        backLabel.setForeground(new Color(90,120,255));
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        backLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                new LoginScreen();
                dispose();
            }
        });

        card.add(emailLabel);
        card.add(emailField);
        card.add(Box.createVerticalStrut(15));

        card.add(passLabel);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(25));

        card.add(resetButton);
        card.add(Box.createVerticalStrut(20));

        card.add(backLabel);

        background.add(card);
        add(background);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

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

            DbConnection db = new DbConnection();

            if(db.emailExists(email)){

                db.updatePassword(email,password);

                JOptionPane.showMessageDialog(this,"Password Updated Successfully");

                new LoginScreen();
                dispose();

            }else{

                JOptionPane.showMessageDialog(this,"Email not found");
            }

        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,ex.getMessage());
        }
    }
}