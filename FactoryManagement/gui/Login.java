package FactoryManagement.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import FactoryManagement.controllers.LoginController;
import FactoryManagement.services.UserService;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel titleLabel, userLabel, passLabel;
    private JCheckBox rememberBox;
    private LoginController loginController;

    public Login(LoginController loginController) {
        this.loginController = loginController;

        setTitle("Inventory Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // ===== Title =====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(new Font("Calisto MT", Font.BOLD, 36));
        titleLabel.setForeground(new Color(44, 62, 80));
        titlePanel.add(titleLabel);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(40, 40, 40, 40)
        ));
        formPanel.setMaximumSize(new Dimension(500, 420));

        // ===== Username =====
        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        usernameField = new JTextField();
        styleTextField(usernameField);

        JPanel userPanel = new JPanel(new BorderLayout(0, 8));
        userPanel.setOpaque(false);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);

        // ===== Password =====
        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        passwordField = new JPasswordField();
        styleTextField(passwordField);

        JPanel passPanel = new JPanel(new BorderLayout(0, 8));
        passPanel.setOpaque(false);
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(passwordField, BorderLayout.CENTER);

        // ===== Stylish Remember Me =====
        rememberBox = createStyledCheckBox("Remember me");

        JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rememberPanel.setOpaque(false);
        rememberPanel.add(rememberBox);

        // ===== Login Button =====
        loginBtn = createStyledButton("Login");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(loginBtn);

        loginBtn.addActionListener(e -> handleLogin());

        // ===== Assemble =====
        formPanel.add(userPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(rememberPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonPanel);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(formPanel);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(center, BorderLayout.CENTER);
        add(mainPanel);

        pack();
        setLocationRelativeTo(null);

        setupPlaceholders();
        setupKeyboardNavigation();
        setupShortcuts();  
    }

    // ==================== Styles ====================

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox box = new JCheckBox(text);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setForeground(new Color(70, 70, 70));
        box.setOpaque(false);
        box.setFocusPainted(false);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Custom icons
        box.setIcon(new CheckBoxIcon(false));
        box.setSelectedIcon(new CheckBoxIcon(true));

        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                box.setForeground(new Color(44, 62, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                box.setForeground(new Color(70, 70, 70));
            }
        });

        return box;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 45));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        return button;
    }

    // ==================== Logic ====================

    private void handleLogin() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (loginController.login(username, password)) {
            UserService.getUserByUsername(username).ifPresent(user -> {
                if (rememberBox.isSelected())
                    loginController.rememberUser(user);
                else
                    loginController.disrememberUser(user);
            });
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== Placeholder ====================

    private void setupPlaceholders() {
        usernameField.setText("Enter Username");
        usernameField.setForeground(Color.GRAY);

        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Enter Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Enter Username");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        passwordField.setEchoChar((char) 0);
        passwordField.setText("Enter Password");
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals("Enter Password")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText("Enter Password");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
    }
    private void setupShortcuts() {
    KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(ctrlR, "toggleRemember");

    getRootPane().getActionMap()
            .put("toggleRemember", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rememberBox.setSelected(!rememberBox.isSelected());
                }
            });
}
    
    private void setupKeyboardNavigation() {
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    passwordField.requestFocus();
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loginBtn.doClick();
            }
        });
        rememberBox.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loginBtn.doClick();
            }
        });
    }

    // ==================== Custom Icon ====================

    static class CheckBoxIcon implements Icon {
        private final boolean checked;

        public CheckBoxIcon(boolean checked) {
            this.checked = checked;
        }

        public int getIconWidth() { return 18; }
        public int getIconHeight() { return 18; }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, 18, 18, 6, 6);

            g2.setColor(new Color(52, 73, 94));
            g2.drawRoundRect(x, y, 18, 18, 6, 6);

            if (checked) {
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x + 4, y + 9, x + 8, y + 13);
                g2.drawLine(x + 8, y + 13, x + 14, y + 5);
            }
        }
    }
}
