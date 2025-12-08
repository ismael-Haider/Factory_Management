import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.ImageIcon;
import java.awt.Image;
import inventory.controllers.LoginController;

import java.awt.Color;

public class Login extends JFrame {
    LoginController loginController;

    ImageIcon notShow = new ImageIcon("src/notshow.png");
    ImageIcon show = new ImageIcon("src/show.png");
    String userPlaceholder = "enter username";
    String passPlaceholder = "enter Password";

    public Login(LoginController loginController) {
        this.loginController = loginController;
        setTitle("Login");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JTextField userField = new JTextField("enter username");
        userField.setBounds(100, 100, 200, 30);
        // userField.setFocusable(false);
        userField.setForeground(Color.GRAY);
        userField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (userField.getText().equals("enter username")) {
                    userField.setText("");
                    userField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent evt) {
                if (userField.getText().isEmpty()) {
                    userField.setForeground(Color.GRAY);
                    userField.setText("enter username");
                }
            }
        });

        JPasswordField passField = new JPasswordField(passPlaceholder);
        passField.setBounds(100, 175, 170, 30);
        passField.setForeground(Color.GRAY);
        passField.setEchoChar((char) 0);
        // passField.setFocusable(false);
        passField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                String passText = new String(passField.getPassword());
                if (passText.equals(passPlaceholder)) {
                    passField.setText("");
                    passField.setForeground(Color.BLACK);
                    passField.setEchoChar('•');
                }
            }

            public void focusLost(FocusEvent evt) {
                String passText = new String(passField.getPassword());
                if (passText.isEmpty()) {
                    passField.setForeground(Color.GRAY);
                    passField.setText("enter Password");
                    passField.setEchoChar((char) 0);
                }
            }
        });

        JButton echoChaeButton = new JButton("");
        echoChaeButton.setBounds(270, 175, 30, 30);
        echoChaeButton.setFocusable(false);
        echoChaeButton.setIcon(resizeImage(show, 30, 30));
        echoChaeButton.addActionListener(e -> {

            if (passField.getEchoChar() == '•') {
                passField.setEchoChar((char) 0);
                echoChaeButton.setIcon(resizeImage(notShow, 30, 30));
            } else {
                passField.setEchoChar('•');
                echoChaeButton.setIcon(resizeImage(show, 30, 30));
            }
        });
        add(echoChaeButton);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 300, 100, 30);
        loginBtn.setFocusable(false);
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);

        add(userField);
        add(passField);
        add(loginBtn);

        // هون التعديل تبع controller==================================================
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());
            boolean success = loginController.login(username, password);
            if (success) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }

            setPlassholder(userField, userPlaceholder);
            setPlassholder(passField, passPlaceholder);

        });
        setVisible(true);
    }

    private void setPlassholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    public static ImageIcon resizeImage(ImageIcon icon, int w, int h) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

}
