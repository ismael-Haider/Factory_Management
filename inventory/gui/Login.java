package inventory.gui;

//import inventory.controllers.LoginController;
import inventory.controllers.LoginController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Login extends JFrame {
//    LoginController loginController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel titleLabel, userLabel, passLabel;
    LoginController loginController;
    public Login(LoginController loginController) {
        this.loginController = loginController;

        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.decode("#ECF0F1"));

        titleLabel = new JLabel("Pro manage");
        titleLabel.setFont(new Font("Segoe Script", Font.BOLD, 36));
        titleLabel.setForeground(Color.decode("#2C3E50"));

        userLabel = new JLabel("User Name:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.ITALIC, 18));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick(); // simulate button click
                }
            }
            
        });

        loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loginBtn.setBackground(Color.decode("#2980B9"));
        loginBtn.setFocusable(false);
        loginBtn.setForeground(Color.WHITE);
        
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = Color.decode("#2980B9");
            private Color hoverColor = Color.decode("#1ABC9C");

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(hoverColor);
                loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(originalColor);
                loginBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // Reset cursor
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(Color.decode("#16A085")); // Optional: click effect
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (loginBtn.contains(evt.getPoint())) {
                    loginBtn.setBackground(hoverColor); // Back to hover color
                }
            }
        });
        
        
        
        
        
        
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            boolean success = loginController.login(username, password);
            System.out.println(username + " " + password);
            System.out.println(success);
            
            if(success){
                System.out.println("success");
                dispose();
            }else{
                System.out.println("fail");
            }
            // if (success) {
            //     dispose();
            //     ManagerFrame managerFrame = new ManagerFrame();
            //     managerFrame.setVisible(true);
            // }else if(username.equals())
            // else {
            //     JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error",
            //             JOptionPane.ERROR_MESSAGE);
            // }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        add(titleLabel, c);

        c.gridy++;
        add(userLabel, c);
        c.gridy++;
        add(usernameField, c);

        c.gridy++;
        add(passLabel, c);
        c.gridy++;
        add(passwordField, c);

        c.gridy++;
        add(loginBtn, c);

        pack();
        setLocationRelativeTo(null);
        // === Username Placeholder ===
        usernameField.setText("Enter Username");
        usernameField.setForeground(Color.GRAY);

        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Enter Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Enter Username");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        // === Password Placeholder ===
        passwordField.setEchoChar((char) 0);
        passwordField.setText("Enter Password");
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = String.valueOf(passwordField.getPassword());
                if (pass.equals("Enter Password")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('●');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String pass = String.valueOf(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText("Enter Password");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        // Enter to move to password
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }

}

