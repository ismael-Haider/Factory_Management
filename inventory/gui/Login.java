package inventory.gui;

import inventory.controllers.LoginController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel titleLabel, userLabel, passLabel;
    private LoginController loginController;
    
    public Login(LoginController loginController) {
        this.loginController = loginController;

        setTitle("Inventory Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(new Font("Calisto MT", Font.BOLD, 36));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        
        // Form panel with card-like appearance
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        formPanel.setMaximumSize(new Dimension(500, 400));
        
        // Username field
        JPanel usernamePanel = new JPanel(new BorderLayout(0, 8));
        usernamePanel.setOpaque(false);
        
        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(new Color(60, 60, 60));
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        usernamePanel.add(userLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout(0, 8));
        passwordPanel.setOpaque(false);
        
        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passLabel.setForeground(new Color(60, 60, 60));
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        passwordPanel.add(passLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        loginBtn = createStyledButton("Login");
        
        // Action listener for login button
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            boolean success = loginController.login(username, password);
            
            if(success){
                System.out.println("Login successful");
                dispose();
            } else {
                System.out.println("Login failed");
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(loginBtn);
        
        // Add components to form panel
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);
        
        // Center form panel
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        formContainer.add(formPanel);
        
        // Add all components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formContainer, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Pack and center
        pack();
        setLocationRelativeTo(null);
        
        // Setup placeholder behaviors
        setupPlaceholders();
        
        // Setup keyboard navigation
        setupKeyboardNavigation();
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(41, 57, 74));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        // Enter key to press button
        button.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });
        
        return button;
    }
    
    private void setupPlaceholders() {
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
                    passwordField.setEchoChar('•');
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
    }
    
    private void setupKeyboardNavigation() {
        // Enter to move from username to password
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });

        // Enter in password field to login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }
        });
    }
}