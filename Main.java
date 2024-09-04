package main.java;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// per Appp

public class Main extends JFrame {

    JTable table;
    DefaultTableModel model;

    public Main() {
        setTitle("TODO List Demo App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // Tabellenmodell erstellen
        model = new DefaultTableModel(new Object[]{"#", "Task Name", "Status", "Edit", "Remove"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Nur die Spalten für die Buttons sind editierbar
            }
        };
        table = new JTable(model);

        // Edit-Button und Remove-Button in der Tabelle einfügen
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), this, table, "Edit"));
        table.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        table.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox(), this, table, "Remove"));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logik für das Hinzufügen einer neuen Aufgabe
                String taskName = JOptionPane.showInputDialog("Enter task name:");
                if (taskName != null && !taskName.trim().isEmpty()) {
                    addTask(model.getRowCount() + 1, taskName, "Todo");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(addButton);
        add(panel, BorderLayout.SOUTH);
    }

    private void addTask(int id, String name, String status) {
        model.addRow(new Object[]{id, name, status, "Edit", "Remove"});
    }

    public void showEditDialog(int rowIndex) {
        String currentTaskName = (String) model.getValueAt(rowIndex, 1);
        String currentStatus = (String) model.getValueAt(rowIndex, 2);

        // Dialog zur Bearbeitung erstellen
        JDialog dialog = new JDialog(this, "Edit Task", true);
        dialog.setLayout(new GridLayout(3, 2));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        // Task Name
        dialog.add(new JLabel("Task Name:"));
        JTextField taskNameField = new JTextField(currentTaskName);
        dialog.add(taskNameField);

        // Status
        dialog.add(new JLabel("Status:"));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Todo", "In Progress", "Complete"});
        statusComboBox.setSelectedItem(currentStatus);
        dialog.add(statusComboBox);

        // Buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        dialog.add(saveButton);
        dialog.add(cancelButton);

        // Aktionen für Buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setValueAt(taskNameField.getText(), rowIndex, 1);
                model.setValueAt(statusComboBox.getSelectedItem(), rowIndex, 2);
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}

// Renderer für die Buttons in der Tabelle
class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// Editor für die Buttons in der Tabelle
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private Main app;
    private JTable table;
    private String actionType;

    public ButtonEditor(JCheckBox checkBox, Main app, JTable table, String actionType) {
        super(checkBox);
        this.app = app;
        this.table = table;
        this.actionType = actionType;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                if (actionType.equals("Edit")) {
                    // Zeigt das Bearbeitungsdialogfeld an
                    app.showEditDialog(selectedRow);
                } else if (actionType.equals("Remove")) {
                    // Use invokeLater to delay the removal of the row until after editing has stopped
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            app.model.removeRow(selectedRow);
                        }
                    });
                }
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}