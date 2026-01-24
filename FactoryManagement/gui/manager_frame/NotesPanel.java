package FactoryManagement.gui.manager_frame;

import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

import FactoryManagement.controllers.ManagerController;
import FactoryManagement.models.Note;

public class NotesPanel extends JPanel {
    private JPanel messagesPanel;
    private JButton addBtn, refreshBtn;
    private JTextArea noteArea;
    private ManagerController controller;
    private JScrollPane messagesScrollPane;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private boolean isPlaceholderActive = true;

    public NotesPanel(ManagerController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(245, 245, 245));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel topBar = initTopBar();
        JPanel messagesContainer = initMessagesContainer();
        JPanel bottomBar = initBottomBar();

        mainContainer.add(topBar, BorderLayout.NORTH);
        mainContainer.add(messagesContainer, BorderLayout.CENTER);
        mainContainer.add(bottomBar, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
        loadAllNotes();
    }

    private JPanel initTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBackground(new Color(245, 245, 245));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel inputLabel = new JLabel("New Note:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 16));
        inputLabel.setForeground(new Color(60, 60, 60));

        noteArea = new JTextArea(5, 60);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(new Font("Arial", Font.PLAIN, 16));
        noteArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        noteArea.setText("Enter your note here...");
        noteArea.setForeground(Color.GRAY);
        isPlaceholderActive = true;

        noteArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderActive) {
                    noteArea.setText("");
                    noteArea.setForeground(Color.BLACK);
                    isPlaceholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = noteArea.getText().trim();
                if (text.isEmpty()) {
                    noteArea.setText("Enter your note here...");
                    noteArea.setForeground(Color.GRAY);
                    isPlaceholderActive = true;
                }
            }
        });

        noteArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPlaceholderActive) {
                    noteArea.setText("");
                    noteArea.setForeground(Color.BLACK);
                    isPlaceholderActive = false;
                }
            }
        });

        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setBorder(null);
        noteScroll.setPreferredSize(new Dimension(800, 120));

        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(noteScroll, BorderLayout.CENTER);

        topBar.add(inputPanel, BorderLayout.CENTER);

        return topBar;
    }

    private JPanel initMessagesContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(255, 255, 204));

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(255, 255, 204));

        messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        messagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        messagesScrollPane.setPreferredSize(new Dimension(1100, 380));

        container.add(messagesScrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel initBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(new Color(245, 245, 245));
        bottomBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        addBtn = createStandardButton("Add Note");
        refreshBtn = createStandardButton("Refresh");

        addBtn.addActionListener(e -> addNote());
        refreshBtn.addActionListener(e -> loadAllNotes());

        bottomBar.add(addBtn);
        bottomBar.add(refreshBtn);

        return bottomBar;
    }

    private JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(140, 45));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 60, 80), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(41, 57, 74));
            }
        });

        return button;
    }

    private void loadAllNotes() {
        messagesPanel.removeAll();

        List<Note> notes = controller.getAllNotes();

        if (notes.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notes yet. Add your first note!");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            messagesPanel.add(emptyLabel);
        } else {
            for (int i = notes.size() - 1; i >= 0; i--) {
                Note note = notes.get(i);
                addNoteToPanel(note);
                if (i > 0) {
                    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                    separator.setForeground(new Color(180, 180, 180));
                    separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
                    separator.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    messagesPanel.add(separator);
                }
            }
        }

        messagesPanel.add(Box.createVerticalGlue());

        messagesPanel.revalidate();
        messagesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = messagesScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMinimum());
        });
    }

    private void addNoteToPanel(Note note) {
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BorderLayout(15, 10));
        notePanel.setBackground(new Color(238, 238, 140));
        notePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25),
                BorderFactory.createLineBorder(new Color(218, 218, 120), 2)));
        notePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JTextArea noteText = new JTextArea(note.getNote());
        noteText.setLineWrap(true);
        noteText.setWrapStyleWord(true);
        noteText.setEditable(false);
        noteText.setFont(new Font("Arial", Font.PLAIN, 16));
        noteText.setBackground(new Color(238, 238, 140));
        noteText.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        JScrollPane textScroll = new JScrollPane(noteText);
        textScroll.setBorder(null);
        textScroll.setBackground(new Color(238, 238, 140));

        JLabel dateLabel = new JLabel(note.getLdt().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.DARK_GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        notePanel.add(textScroll, BorderLayout.CENTER);
        notePanel.add(dateLabel, BorderLayout.SOUTH);

        notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        messagesPanel.add(notePanel);

        messagesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void addNote() {
        String text = noteArea.getText().trim();
        if (!text.isEmpty() && !isPlaceholderActive) {
            controller.addNote(text);

            noteArea.setText("");
            loadAllNotes();
            noteArea.requestFocus(false);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please enter a note.",
                    "Empty Note",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}