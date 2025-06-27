package bugdettracker;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Budget extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private ArrayList<Income> incomeList = new ArrayList<>();
    private ArrayList<Expense> expenseList = new ArrayList<>();
    private LimitChecker limitChecker = new LimitChecker();
    private DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Type", "Amount", "Category/Source", "Date"}, 0);

    // Main Method
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Budget frame = new Budget();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Constructor for JFrame Setup
    public Budget() {
        // JFrame properties setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 600);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        initialize();  // Initialize panels and event listeners
    }

    // Method to Initialize the Panels and Setup Navigation
    private void initialize() {
        // Navigation panel setup
        JPanel navPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] btnLabels = {"Add Income", "Add Expense", "Balance", "Report", "Limit", "Manage Records", "Sort", "Reset", "Summary", "Exit"};
        JButton[] buttons = new JButton[btnLabels.length];

        for (int i = 0; i < btnLabels.length; i++) {
            buttons[i] = new JButton(btnLabels[i]);
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 13));
            buttons[i].setBackground(new Color(245, 224, 66)); // Cool Yellow Button Color
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
            buttons[i].setFocusPainted(false);
            navPanel.add(buttons[i]);
        }

        // Adding the panels to the card layout
        cardPanel.add(getIncomePanel(), "income");
        cardPanel.add(getExpensePanel(), "expense");
        cardPanel.add(getBalancePanel(), "balance");
        cardPanel.add(getReportPanel(), "report");
        cardPanel.add(getLimitPanel(), "limit");
        cardPanel.add(getManagePanel(), "manage");
        cardPanel.add(getSortPanel(), "sort");

        // Event listeners for the buttons
        buttons[0].addActionListener(e -> cardLayout.show(cardPanel, "income"));
        buttons[1].addActionListener(e -> cardLayout.show(cardPanel, "expense"));
        buttons[2].addActionListener(e -> cardLayout.show(cardPanel, "balance"));
        buttons[3].addActionListener(e -> cardLayout.show(cardPanel, "report"));
        buttons[4].addActionListener(e -> cardLayout.show(cardPanel, "limit"));
        buttons[5].addActionListener(e -> cardLayout.show(cardPanel, "manage"));
        buttons[6].addActionListener(e -> cardLayout.show(cardPanel, "sort"));
        buttons[7].addActionListener(e -> {
            incomeList.clear();
            expenseList.clear();
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "All data has been reset!");
        });
        buttons[8].addActionListener(e -> {
            int totalIncome = incomeList.stream().mapToInt(Income::getAmount).sum();
            int totalExpense = expenseList.stream().mapToInt(Expense::getAmount).sum();
            String topCategory = expenseList.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingInt(Expense::getAmount)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("None");
            JOptionPane.showMessageDialog(this, "Session Summary:\nIncome: Rs. " + totalIncome +
                "\nExpense: Rs. " + totalExpense +
                "\nBalance: Rs. " + (totalIncome - totalExpense) +
                "\nTop Category: " + topCategory);
        });
        buttons[9].addActionListener(e -> System.exit(0));

        // Adding the navigation panel to the frame
        contentPane.add(navPanel, BorderLayout.NORTH);
        contentPane.add(cardPanel, BorderLayout.CENTER);
    }

    // Panel for adding income
    private JPanel getIncomePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField amountField = new JTextField();
        JComboBox<String> sourceBox = new JComboBox<>(new String[]{"Salary", "Freelance", "Other"});
        JButton saveBtn = new JButton("Save Income");

        saveBtn.addActionListener(e -> {
            try {
                int amt = Integer.parseInt(amountField.getText());
                String source = (String) sourceBox.getSelectedItem();
                incomeList.add(new Income(amt, source));
                tableModel.addRow(new Object[]{"Income", amt, source, "-"}); // Adding to table
                JOptionPane.showMessageDialog(this, "Income saved!");
                amountField.setText(""); // Resetting the amount field
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceBox);
        panel.add(new JLabel(""));
        panel.add(saveBtn);

        return panel;
    }

    // Panel for adding expenses
    private JPanel getExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Food", "Travel", "Utilities"});
        JButton saveBtn = new JButton("Save Expense");

        saveBtn.addActionListener(e -> {
            try {
                int amt = Integer.parseInt(amountField.getText());
                String cat = (String) catBox.getSelectedItem();
                String date = dateField.getText();
                expenseList.add(new Expense(amt, cat, date));
                tableModel.addRow(new Object[]{"Expense", amt, cat, date});
                JOptionPane.showMessageDialog(this, "Expense saved!");
                amountField.setText("");
                dateField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Date (dd/mm/yyyy):"));
        panel.add(dateField);
        panel.add(new JLabel("Category:"));
        panel.add(catBox);
        panel.add(new JLabel(""));
        panel.add(saveBtn);

        return panel;
    }

    // Panel for calculating balance
    private JPanel getBalancePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        JLabel incomeLbl = new JLabel("Total Income: ");
        JLabel expenseLbl = new JLabel("Total Expense: ");
        JLabel balanceLbl = new JLabel("Balance: ");
        JButton calcBtn = new JButton("Calculate");

        calcBtn.addActionListener(e -> {
            int income = incomeList.stream().mapToInt(Income::getAmount).sum();
            int expense = expenseList.stream().mapToInt(Expense::getAmount).sum();
            incomeLbl.setText("Total Income: Rs. " + income);
            expenseLbl.setText("Total Expense: Rs. " + expense);
            balanceLbl.setText("Balance: Rs. " + (income - expense));
        });

        panel.add(incomeLbl);
        panel.add(expenseLbl);
        panel.add(balanceLbl);
        panel.add(calcBtn);

        return panel;
    }

    // Panel for generating report
    private JPanel getReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        JButton generate = new JButton("Generate Report");

        generate.addActionListener(e -> {
            Map<String, Integer> report = new HashMap<>();
            for (Expense exp : expenseList) {
                report.put(exp.getCategory(), report.getOrDefault(exp.getCategory(), 0) + exp.getAmount());
            }
            StringBuilder sb = new StringBuilder("Category-wise Report:\n");
            report.forEach((k, v) -> sb.append(k).append(": Rs. ").append(v).append("\n"));
            area.setText(sb.toString());
        });

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.add(generate, BorderLayout.SOUTH);
        return panel;
    }

    // Panel for setting limits
    private JPanel getLimitPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Food", "Travel", "Utilities"});
        JTextField limitField = new JTextField();
        JLabel status = new JLabel("Status:");
        JButton check = new JButton("Check Limit");

        check.addActionListener(e -> {
            try {
                String cat = (String) catBox.getSelectedItem();
                int limit = Integer.parseInt(limitField.getText());
                limitChecker.setLimit(cat, limit);
                int spent = expenseList.stream().filter(x -> x.getCategory().equals(cat)).mapToInt(Expense::getAmount).sum();
                status.setText("Status: " + limitChecker.checkLimit(cat, spent));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        panel.add(new JLabel("Select Category:"));
        panel.add(catBox);
        panel.add(new JLabel("Set Limit:"));
        panel.add(limitField);
        panel.add(check);
        panel.add(status);
        return panel;
    }

    // Panel for managing records
    private JPanel getManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JButton deleteBtn = new JButton("Delete Selected");

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String type = (String) tableModel.getValueAt(row, 0);
                int amount = (int) tableModel.getValueAt(row, 1);
                String label = (String) tableModel.getValueAt(row, 2);
                tableModel.removeRow(row);
                if (type.equals("Income")) {
                    incomeList.removeIf(i -> i.getAmount() == amount && i.getSource().equals(label));
                } else {
                    expenseList.removeIf(i -> i.getAmount() == amount && i.getCategory().equals(label));
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    // Panel for sorting records
    private JPanel getSortPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        JButton sortAmountBtn = new JButton("Sort by Amount");
        JButton sortDateBtn = new JButton("Sort by Date");

        sortAmountBtn.addActionListener(ev -> {
            List<Expense> sorted = new ArrayList<>(expenseList);
            sorted.sort(Comparator.comparingInt(Expense::getAmount));
            area.setText("--- Expenses Sorted by Amount ---\n");
            for (Expense ex : sorted) {
                area.append(ex.getCategory() + " - Rs." + ex.getAmount() + " - " + ex.getDate() + "\n");
            }
        });

        sortDateBtn.addActionListener(ev -> {
            List<Expense> sorted = new ArrayList<>(expenseList);
            sorted.sort(Comparator.comparing(Expense::getDate));
            area.setText("--- Expenses Sorted by Date ---\n");
            for (Expense ex : sorted) {
                area.append(ex.getCategory() + " - Rs." + ex.getAmount() + " - " + ex.getDate() + "\n");
            }
        });

        JPanel btnPanel = new JPanel(new GridLayout(1, 2));
        btnPanel.add(sortAmountBtn);
        btnPanel.add(sortDateBtn);

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // Classes for Income, Expense, LimitChecker
    static class Income {
        private int amount;
        private String source;

        public Income(int amount, String source) {
            this.amount = amount;
            this.source = source;
        }

        public int getAmount() {
            return amount;
        }

        public String getSource() {
            return source;
        }
    }

    static class Expense {
        private int amount;
        private String category;
        private String date;

        public Expense(int amount, String category, String date) {
            this.amount = amount;
            this.category = category;
            this.date = date;
        }

        public int getAmount() {
            return amount;
        }

        public String getCategory() {
            return category;
        }

        public String getDate() {
            return date;
        }
    }

    static class LimitChecker {
        private Map<String, Integer> setLimits = new HashMap<>();

        public void setLimit(String category, int limit) {
            setLimits.put(category, limit);
        }

        public String checkLimit(String category, int currentExpense) {
            int limit = setLimits.getOrDefault(category, Integer.MAX_VALUE);
            if (currentExpense >= limit) {
                return "❌ Limit Exceeded!";
            } else if (currentExpense >= 0.9 * limit) {
                return "⚠ Near Limit!";
            } else {
                return "✅ Safe";
            }
        }
    }
}
