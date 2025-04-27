package Default;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RestaurantManagementSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}

class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Login - Restaurant System");
        setSize(400, 200);
        setLayout(new GridLayout(2, 1));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton customerBtn = new JButton("Login as Customer");
        JButton ownerBtn = new JButton("Login as Owner");

        customerBtn.addActionListener(e -> {
            String password = JOptionPane.showInputDialog("Enter Customer Password:");
            if ("123".equals(password)) {
                new MainFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong password for Customer!");
            }
        });

        ownerBtn.addActionListener(e -> {
            String password = JOptionPane.showInputDialog("Enter Owner Password:");
            if ("321".equals(password)) {
                new OwnerDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong password for Owner!");
            }
        });

        add(customerBtn);
        add(ownerBtn);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class MainFrame extends JFrame {
    private JTextArea displayArea;
    private JButton showMenuButton, orderButton, clearOrdersButton;
    private Menu menu;
    private OrderManager orderManager;

    public MainFrame() {
        setTitle("Customer - Restaurant Ordering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setBackground(new Color(240, 248, 255));
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        showMenuButton = new JButton("Show Menu");
        orderButton = new JButton("Place Order");
        clearOrdersButton = new JButton("Clear Orders");

        buttonPanel.add(showMenuButton);
        buttonPanel.add(orderButton);
        buttonPanel.add(clearOrdersButton);
        add(buttonPanel, BorderLayout.SOUTH);

        menu = new Menu();
        orderManager = OrderManager.getInstance();

        showMenuButton.addActionListener(e -> displayArea.setText(menu.getAllItems()));

        orderButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter your name:");
            String item = JOptionPane.showInputDialog("Enter item name:");
            int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity:"));
            Order o = new Order(name, item, qty, menu);
            orderManager.addOrder(o);
            displayArea.setText("Order placed:\n" + o + "\nTotal Revenue: ₹" + orderManager.getTotalRevenue());
        });

        clearOrdersButton.addActionListener(e -> {
            orderManager.clearOrders();
            displayArea.setText("Orders cleared. Revenue: ₹0");
        });

        getContentPane().setBackground(new Color(230, 230, 250));
        setVisible(true);
    }
}

class OwnerDashboard extends JFrame {
    private JTextArea orderView, empDetails, empAttendance, paymentHistory;
    private JButton markPresent, markLeave, paySalary;
    private List<Employee> employees;
    private Map<String, String> attendance;
    private List<String> paymentLog;

    public OwnerDashboard() {
        setTitle("Owner Dashboard");
        setSize(1000, 700);
        setLayout(new GridLayout(2, 2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        employees = EmployeeDirectory.getEmployees();
        attendance = new HashMap<>();
        paymentLog = new ArrayList<>();

        orderView = new JTextArea("-- Orders --\n" + OrderManager.getInstance().getOrderHistory());
        empDetails = new JTextArea("-- Employee List --\n" + EmployeeDirectory.getAllEmployeeDetails());
        empAttendance = new JTextArea("-- Attendance Log --\n");
        paymentHistory = new JTextArea("-- Payment History --\n");

        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(new JScrollPane(empDetails), BorderLayout.CENTER);

        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        markPresent = new JButton("Mark Present");
        markLeave = new JButton("Mark Leave");
        paySalary = new JButton("Pay Salary");

        topPanel.add(markPresent);
        topPanel.add(markLeave);
        topPanel.add(paySalary);
        panel2.add(topPanel, BorderLayout.NORTH);
        panel2.add(new JScrollPane(empAttendance), BorderLayout.CENTER);

        add(panel1);
        add(panel2);
        add(new JScrollPane(orderView));

        JPanel panel4 = new JPanel(new BorderLayout());
        panel4.add(new JScrollPane(paymentHistory), BorderLayout.CENTER);
        add(panel4);

        markPresent.addActionListener(e -> markAttendance("Present"));
        markLeave.addActionListener(e -> markAttendance("Leave"));
        paySalary.addActionListener(e -> processSalary());

        getContentPane().setBackground(new Color(245, 245, 255));
        setVisible(true);
    }

    private void markAttendance(String status) {
        String name = JOptionPane.showInputDialog("Employee Name:");
        attendance.put(name, status);
        empAttendance.append(name + " - " + status + "\n");
    }

    private void processSalary() {
        try {
            String input = JOptionPane.showInputDialog("Enter salary in format: Name, Amount");
            String[] parts = input.split(",");
            String name = parts[0].trim();
            double base = Double.parseDouble(parts[1].trim());
            String status = attendance.getOrDefault(name, "");
            if (status.equals("Leave")) base *= 0.98;
            paymentLog.add(name + " paid ₹" + base);
            paymentHistory.append(name + " paid ₹" + base + "\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid format. Use: Name, Amount");
        }
    }
}

class MenuItem {
    private String name;
    private double price;
    public MenuItem(String n, double p) { name = n; price = p; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String toString() { return name + " - ₹" + price; }
}

class Menu {
    private List<MenuItem> items = new ArrayList<>();
    public Menu() {
        items.add(new MenuItem("Paneer Butter Masala", 220));
        items.add(new MenuItem("Aloo Gobi", 170));
        items.add(new MenuItem("Chole Bhature", 160));
        items.add(new MenuItem("Dal Makhani", 190));
        items.add(new MenuItem("Veg Biryani", 200));
        items.add(new MenuItem("Chicken Biryani", 250));
        // New dishes added
        items.add(new MenuItem("Butter Naan", 40));
        items.add(new MenuItem("Masala Dosa", 100));
        items.add(new MenuItem("Samosa (2 pcs)", 50));
        items.add(new MenuItem("Tandoori Chicken", 300));
        items.add(new MenuItem("Rajma Chawal", 180));
        items.add(new MenuItem("Fish Curry", 280));
        items.add(new MenuItem("Egg Curry", 160));
        items.add(new MenuItem("Palak Paneer", 210));
        items.add(new MenuItem("Garlic Naan", 50));
        items.add(new MenuItem("Mixed Veg Curry", 190));
    }
    public MenuItem getItemByName(String name) {
        for (MenuItem item : items) if (item.getName().equalsIgnoreCase(name)) return item;
        return null;
    }
    public String getAllItems() {
        StringBuilder sb = new StringBuilder("-- Menu --\n");
        for (MenuItem item : items) sb.append(item).append("\n");
        return sb.toString();
    }
}

class Order {
    private String customerName, itemName;
    private int quantity;
    private double totalCost;
    public Order(String c, String i, int q, Menu m) {
        customerName = c; itemName = i; quantity = q;
        MenuItem item = m.getItemByName(i);
        totalCost = item != null ? item.getPrice() * q : 0;
    }
    public double getTotalCost() { return totalCost; }
    public String toString() { return customerName + " ordered " + quantity + " x " + itemName + " = ₹" + totalCost; }
}

class OrderManager {
    private static OrderManager instance;
    private List<Order> orders;
    private OrderManager() { orders = new ArrayList<>(); }
    public static OrderManager getInstance() {
        if (instance == null) instance = new OrderManager();
        return instance;
    }
    public void addOrder(Order o) { orders.add(o); }
    public double getTotalRevenue() {
        return orders.stream().mapToDouble(Order::getTotalCost).sum();
    }
    public void clearOrders() { orders.clear(); }
    public String getOrderHistory() {
        StringBuilder sb = new StringBuilder();
        for (Order o : orders) sb.append(o).append("\n");
        return sb.toString();
    }
}

class Employee {
    private String name, role, workTime, qualification;
    private double salary;
    public Employee(String n, String r, String wt, double s, String q) {
        name = n; role = r; workTime = wt; salary = s; qualification = q;
    }
    public String getName() { return name; }
    public String toString() {
        return name + " | Role: " + role + " | Time: " + workTime + " | Salary: ₹" + salary + " | Qualification: " + qualification;
    }
}

class EmployeeDirectory {
    public static List<Employee> getEmployees() {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee("Ankit Sharma", "Chef", "9AM - 5PM", 30000, "Diploma in Hotel Mgmt"));
        list.add(new Employee("Riya Sen", "Manager", "10AM - 6PM", 40000, "MBA in Hospitality"));
        list.add(new Employee("Ajay Verma", "Waiter", "12PM - 10PM", 18000, "High School"));
        list.add(new Employee("Sonal Mehta", "Delivery Boy", "2PM - 10PM", 15000, "12th Pass"));
        return list;
    }

    public static String getAllEmployeeDetails() {
        StringBuilder sb = new StringBuilder();
        for (Employee emp : getEmployees()) sb.append(emp).append("\n");
        return sb.toString();
    }
}
