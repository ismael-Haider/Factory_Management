package inventory.gui;

import inventory.models.User;
import inventory.models.Note;
import inventory.services.NoteService;
import inventory.controllers.LoginController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;

public class ManagerFrame extends JFrame {
    private User user;
    private DefaultListModel<String> notesModel;

    public ManagerFrame(User user) {
        this.user = user;
        initUI();
        loadNotes();
    }

    private void initUI() {
        setTitle("Manager Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(52, 73, 94));
        top.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Manager: " + user.getUserName());
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        top.add(lbl, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new inventory.gui.Login(new LoginController()).setVisible(true);
        });
        top.add(logout, BorderLayout.EAST);

        // Main split: left = form, right = notes list
        JSplitPane split = new JSplitPane();
        split.setResizeWeight(0.45);

        // Left form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("Notes"));
        JTextArea noteArea = new JTextArea(6, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        form.add(noteScroll);
        form.add(Box.createVerticalStrut(10));

        JButton saveBtn = new JButton("Save Evaluation");

        form.add(saveBtn);

        // Right notes list
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(10, 10, 10, 10));
        notesModel = new DefaultListModel<>();
        JList<String> notesList = new JList<>(notesModel);
        right.add(new JScrollPane(notesList), BorderLayout.CENTER);

        split.setLeftComponent(form);
        split.setRightComponent(right);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void loadNotes() {
        notesModel.clear();
        List<Note> notes = NoteService.getAllNotes();
        for (Note n : notes) {
            notesModel.addElement(n.toString());
        }
    }
}
