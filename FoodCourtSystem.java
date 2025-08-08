import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;


public class FoodCourtSystem extends JFrame{
    private static ArrayList<FoodItem>menu=new ArrayList<>();
    private static ArrayList<Order>orderHistory=new ArrayList<>();
    private static Queue<Student>studentQueue=new LinkedList<>();

    private static final String MENU_FILE="menu.txt";
    private static final String ORDERS_FILE="orders.txt";


    private static JLabel description1,description2,studentDescription1;
    private static JLabel imageLabel,groupImage;
  








    public static void addToOrder(Student student, String Foodid, int Quantity) {
        try {
            String id = Foodid;
            int quantity = Quantity;

            FoodItem selectedItem = null;

            for (FoodItem item : menu) {
                if (item.getId().equals(id) && item.getQuantity() >= quantity) {
                    selectedItem = item;
                    break;
                }
            }

            if (selectedItem != null) {
                student.addToOrder(selectedItem, quantity);
                selectedItem.setQuantity(selectedItem.getQuantity() - quantity);
                JOptionPane.showMessageDialog(null, "Item added to order", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Item not available or insufficient quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }








    public static void viewCurrentOrder(Student student) {
        Order currentOrder = student.getCurrentOrder();
        double total = 0;


        FoodCourtSystem studentCurrentOrderFrame = new FoodCourtSystem();
        studentCurrentOrderFrame.setTitle("Order History - Food Court DIU");
        studentCurrentOrderFrame.setSize(1150, 850);
        studentCurrentOrderFrame.setLocationRelativeTo(null);
        studentCurrentOrderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container scoc = studentCurrentOrderFrame.getContentPane();
        scoc.setLayout(null);

        JPanel leftCurrentOrder = new JPanel();
        leftCurrentOrder.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
        leftCurrentOrder.setBounds(0, 0, 450, 850);
        leftCurrentOrder.setLayout(null);
        scoc.add(leftCurrentOrder);

        JPanel rightCurrentOrder = new JPanel();
        rightCurrentOrder.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
        rightCurrentOrder.setBounds(450, 0, 700, 850);
        rightCurrentOrder.setLayout(null);
        scoc.add(rightCurrentOrder);

        JLabel titleLabel = new JLabel("Student Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 100, 200, 50);
        leftCurrentOrder.add(titleLabel);

        JLabel idLabel = new JLabel("Student ID : " + student.id, SwingConstants.LEFT);
        idLabel.setFont(new Font("Arial", Font.BOLD, 20));
        idLabel.setForeground(Color.WHITE);
        idLabel.setBounds(50, 300, 400, 50);
        leftCurrentOrder.add(idLabel);

        JLabel nameLabel = new JLabel("Student Name : " + student.name, SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(50, 330, 400, 50);
        leftCurrentOrder.add(nameLabel);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBounds(550, 750, 100, 40);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.RED);
        backButton.setFocusPainted(false);
        rightCurrentOrder.add(backButton);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(110, 250, 500, 400);
        rightCurrentOrder.add(scrollPane);

        JButton currentOrderOnlyButton = new JButton("Current Order");
        currentOrderOnlyButton.setFont(new Font("Arial", Font.BOLD, 30));
        currentOrderOnlyButton.setBounds(150, 10, 400, 70);
        currentOrderOnlyButton.setBackground(Color.BLACK);
        currentOrderOnlyButton.setForeground(Color.DARK_GRAY);
        currentOrderOnlyButton.setFocusPainted(false);
        rightCurrentOrder.add(currentOrderOnlyButton);

        StringBuilder sbc = new StringBuilder();
        sbc.append("\n");

        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items in current order.", "Current Order",
                    JOptionPane.INFORMATION_MESSAGE);
                    
        } else {
            studentCurrentOrderFrame.setVisible(true);
            sbc.append("   Item      price        Quantity       Amount       \n");
            sbc.append("-----------------------------------------------------------\n\n");
                        
            for (OrderItem item : currentOrder.getItems()) {
                sbc.append(String.format("   %-10s %-6.2fTaka      x%-4d     %-6.2fTaka\n\n",
                        item.getFoodItem().getName(),item.getPrice(), item.getQuantity(), item.getTotalAmount()));
                total += item.getTotalAmount();
            }
            sbc.append("\n  Total Amount: ").append(String.format("%.2f", total)).append(" Taka");

        }

        textArea.setText(sbc.toString());

        backButton.addActionListener(ahe -> {
            studentCurrentOrderFrame.setVisible(false);
            ;

        });
        

    }






    public static void checkOut(Student student) {
        Order currentOrder = student.getCurrentOrder();

        if (currentOrder == null || currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No items to checkout.", "Checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0;
        for (OrderItem item : currentOrder.getItems()) {
            total += item.getTotalAmount();
            
        }


        int confirm = JOptionPane.showConfirmDialog(null, "\nTotal Amount: "+total+"Taka\nProceed to payment?", 
            "Checkout Confirmation", JOptionPane.OK_CANCEL_OPTION);

        if (confirm == JOptionPane.OK_OPTION) {
            currentOrder.markAsPaid();
            orderHistory.add(currentOrder);
            saveData();

            student.checkOut();
            JOptionPane.showMessageDialog(null, "Payment successful! Thank you for your order.", 
                "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }









    public static void saveData(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(MENU_FILE))){
            for (FoodItem foodItem : menu) {
                writer.write(foodItem.toString());
                writer.newLine();
                
            }
            writer.close();

            System.out.println("Menu saved successfully with " + menu.size() + " items.");

        } catch (Exception e) {
           System.out.println("Error saving menu: " + e.getMessage());
        }





        try (BufferedWriter writer=new BufferedWriter(new FileWriter(ORDERS_FILE))){

            for (Order order : orderHistory) {
                writer.write(order.toString());
                writer.newLine();
            }
            writer.close();

            System.out.println("Order history saved successfully with " + orderHistory.size() + " orders.");


        } catch (Exception e) {
         System.out.println("Error saving orders: " + e.getMessage());

        }
    }








    public static void loadData(){

        menu.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    menu.add(FoodItem.fromString(line));
                }
            }
            System.out.println("Menu loaded successfully with " + menu.size() + " items.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }





        orderHistory.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    orderHistory.add(Order.fromString(line, menu));
                }
            }

            System.out.println("Order history loaded successfully with " + orderHistory.size() + " orders.");
        }
        catch (Exception e) {
            System.out.println("No existing orders file found. Starting with empty order history.");
        }


    }







   private static void processCustomerQueue() {
        while (!studentQueue.isEmpty()) {
            Student student = studentQueue.poll();
            System.out.println("\nNow serving: " + student.getName());
        }
        System.out.println("No more customers in queue.");
    }







    public static void main(String[] args) {
        loadData();



        ImageIcon diuIcon = new ImageIcon("Logo.png");
        ImageIcon groupIcon = new ImageIcon("groupPhoto.png");

        Image scaledDiuLogo = diuIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledDiuIcon = new ImageIcon(scaledDiuLogo);

        Image scaledGroupPhoto = groupIcon.getImage().getScaledInstance(600, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledGroupIcon = new ImageIcon(scaledGroupPhoto);

        FoodCourtSystem frame = new FoodCourtSystem();
        frame.setTitle("Food Court DIU");
        frame.setSize(1150, 850);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(diuIcon.getImage());

        Container c = frame.getContentPane();
        c.setLayout(null);

        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
        leftPanel.setBounds(0, 0, 700, 850);
        leftPanel.setLayout(null);
        c.add(leftPanel);

        imageLabel = new JLabel(scaledDiuIcon);
        imageLabel.setBounds(275, 30, 150, 80);
        leftPanel.add(imageLabel);

        groupImage = new JLabel(scaledGroupIcon);
        groupImage.setBounds(0, 370, 685, 300);
        leftPanel.add(groupImage);



        description1 = new JLabel("Welcome to Food Court Management System", SwingConstants.CENTER);
        description1.setFont(new Font("Arial", Font.BOLD, 24));
        description1.setForeground(Color.WHITE);
        description1.setOpaque(false);
        description1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
        description1.setBounds(50, 130, 600, 50);
        leftPanel.add(description1);

        description2 = new JLabel("<html><div style='text-align: left;'>"
                + "This is Java-based OOP project, developed for the OOP course at Daffodil International University (DIU), "
                + "aims to automate and streamline the food ordering process in the university's food court. "
                + "It digitizes ordering to make the process faster, more organized, and efficient for both students and staff."
                + "<br><br><br><br>This project is built by a group of five students.They are :"
                + "</div></html>");
        description2.setFont(new Font("Arial", Font.PLAIN, 14));
        description2.setForeground(Color.WHITE);
        description2.setOpaque(false);
        description2.setToolTipText("Click to read full project goal.");
        description2.setBounds(50, 200, 600, 150);
        leftPanel.add(description2);

        
        
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
        rightPanel.setBounds(700, 0, 450, 850);
        rightPanel.setLayout(null);
        c.add(rightPanel);

        
        JButton studentLoginButton = new JButton("Student Login");
        studentLoginButton.setFont(new Font("Arial", Font.BOLD, 18));
        studentLoginButton.setBounds(125, 300, 200, 50);
        rightPanel.add(studentLoginButton);

        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setFont(new Font("Arial", Font.BOLD, 18));
        adminLoginButton.setBounds(125, 380, 200, 50);
        rightPanel.add(adminLoginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBounds(300, 750, 100, 40); 
        exitButton.setBackground(Color.BLACK);
        exitButton.setForeground(Color.RED);
        exitButton.setFocusPainted(false);
        rightPanel.add(exitButton);

        
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Confirmation",2,JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                saveData();
                System.exit(0);
            }
        });




        studentLoginButton.addActionListener(e -> {
            frame.dispose();
            
            FoodCourtSystem studentFrame = new FoodCourtSystem();
            studentFrame.setTitle("Food Court DIU");
            studentFrame.setSize(1150, 850);
            studentFrame.setLocationRelativeTo(null);
            studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            studentFrame.setIconImage(diuIcon.getImage());

            Container sc = studentFrame.getContentPane();
            sc.setLayout(null);

            JPanel leftStudentPanel = new JPanel();
            leftStudentPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
            leftStudentPanel.setBounds(0, 0, 450, 850);
            leftStudentPanel.setLayout(null);
            sc.add(leftStudentPanel);

            JPanel rightStudentPanel = new JPanel();
            rightStudentPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
            rightStudentPanel.setBounds(450, 0, 700, 850);
            rightStudentPanel.setLayout(null);
            sc.add(rightStudentPanel);

            studentDescription1 = new JLabel("Student Panel", SwingConstants.CENTER);
            studentDescription1.setFont(new Font("Arial", Font.BOLD, 24));
            studentDescription1.setForeground(Color.DARK_GRAY);
            studentDescription1.setOpaque(false);
            studentDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
            studentDescription1.setBounds(100, 100, 200, 50);
            leftStudentPanel.add(studentDescription1);

            JLabel studentIdLabel = new JLabel("Enter Student ID :");
            studentIdLabel.setFont(new Font("Arial", Font.PLAIN, 19));
            studentIdLabel.setBounds(225, 300, 300, 50);
            rightStudentPanel.add(studentIdLabel);

            JTextField studentIdField = new JTextField();
            studentIdField.setFont(new Font("Arial", Font.PLAIN, 19));
            studentIdField.setBounds(225, 350, 300, 50);
            rightStudentPanel.add(studentIdField);

            JLabel studentNameLabel = new JLabel("Enter Your Name :");
            studentNameLabel.setFont(new Font("Arial", Font.PLAIN, 19));
            studentNameLabel.setBounds(225, 400, 300, 50);
            rightStudentPanel.add(studentNameLabel);

            JTextField studentNameField = new JTextField();
            studentNameField.setFont(new Font("Arial", Font.PLAIN, 19));
            studentNameField.setBounds(225, 450, 300, 50);
            rightStudentPanel.add(studentNameField);

            JButton studentEnterButton = new JButton("Enter");
            studentEnterButton.setFont(new Font("Arial", Font.BOLD, 16));
            studentEnterButton.setBounds(225, 520, 150, 40);
            rightStudentPanel.add(studentEnterButton);

            studentEnterButton.addActionListener(see -> {
                String studentId = studentIdField.getText();
                String studentName = studentNameField.getText();

                if (studentId.isEmpty() || studentName.isEmpty()) {
                    JOptionPane.showMessageDialog(studentFrame, "Please enter both ID and Name", "Error Massage", 2);
                    return;
                }

                Student customer = new Student(studentId, studentName);
                studentQueue.add(customer);
                processCustomerQueue();

                studentFrame.setVisible(false);

                FoodCourtSystem studentOptionFrame = new FoodCourtSystem();
                studentOptionFrame.setTitle("Food Court DIU");
                studentOptionFrame.setSize(1150, 850);
                studentOptionFrame.setLocationRelativeTo(null);
                studentOptionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                studentOptionFrame.setIconImage(diuIcon.getImage());

                Container soc = studentOptionFrame.getContentPane();
                soc.setLayout(null);

                JPanel leftStudentOptionPanel = new JPanel();
                leftStudentOptionPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                leftStudentOptionPanel.setBounds(0, 0, 450, 850);
                leftStudentOptionPanel.setLayout(null);
                soc.add(leftStudentOptionPanel);

                JPanel rightStudentOptionPanel = new JPanel();
                rightStudentOptionPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                rightStudentOptionPanel.setBounds(450, 0, 700, 850);
                rightStudentOptionPanel.setLayout(null);
                soc.add(rightStudentOptionPanel);

                JButton studentBackOptionButton = new JButton("Back");
                studentBackOptionButton.setFont(new Font("Arial", Font.BOLD, 16));
                studentBackOptionButton.setBounds(550, 750, 100, 40);
                studentBackOptionButton.setBackground(Color.BLACK);
                studentBackOptionButton.setForeground(Color.RED);
                studentBackOptionButton.setFocusPainted(false);
                rightStudentOptionPanel.add(studentBackOptionButton);

                studentBackOptionButton.addActionListener(se -> {
                    int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit from Student Panel?", "Exit Confirmation", 2, JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        studentOptionFrame.setVisible(false);
                        frame.setVisible(true);
                    }
                });

                JLabel studentOptionDescription1 = new JLabel("Student Panel", SwingConstants.CENTER);
                studentOptionDescription1.setFont(new Font("Arial", Font.BOLD, 24));
                studentOptionDescription1.setForeground(Color.WHITE);
                studentOptionDescription1.setOpaque(false);
                studentOptionDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                studentOptionDescription1.setBounds(100, 100, 200, 50);
                leftStudentOptionPanel.add(studentOptionDescription1);

                JLabel studentOptionDescription2 = new JLabel("Student ID : " + studentId, SwingConstants.LEFT);
                studentOptionDescription2.setFont(new Font("Arial", Font.BOLD, 20));
                studentOptionDescription2.setForeground(Color.WHITE);
                studentOptionDescription2.setOpaque(false);
                studentOptionDescription2.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                studentOptionDescription2.setBounds(50, 300, 400, 50);
                leftStudentOptionPanel.add(studentOptionDescription2);

                JLabel studentOptionDescription3 = new JLabel("Student Name : " + studentName, SwingConstants.LEFT);
                studentOptionDescription3.setFont(new Font("Arial", Font.BOLD, 20));
                studentOptionDescription3.setForeground(Color.WHITE);
                studentOptionDescription3.setOpaque(false);
                studentOptionDescription3.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                studentOptionDescription3.setBounds(50, 330, 400, 50);
                leftStudentOptionPanel.add(studentOptionDescription3);

                JButton studentViewMenuButton = new JButton("View Menu");
                studentViewMenuButton.setFont(new Font("Arial", Font.BOLD, 22));
                studentViewMenuButton.setBounds(150, 240, 400, 70);
                rightStudentOptionPanel.add(studentViewMenuButton);

                studentViewMenuButton.addActionListener(sve -> {
                    studentOptionFrame.setVisible(false);

                    String[] columnNames = {"ID", "Name", "Price (Taka)", "Qty"};
                    Object[][] data = new Object[menu.size()][4];

                    for (int i = 0; i < menu.size(); i++) {
                        FoodItem item = menu.get(i);
                        data[i][0] = item.getId();
                        data[i][1] = item.getName();
                        data[i][2] = String.format("%.2f", item.getPrice());
                        data[i][3] = item.getQuantity();
                    }

                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    table.setFillsViewportHeight(true);

                    table.setFont(new Font("Arial", Font.PLAIN, 20));
                    table.setRowHeight(30);

                    JTableHeader header = table.getTableHeader();
                    header.setFont(new Font("Arial", Font.BOLD, 22));
                    header.setForeground(Color.WHITE);
                    header.setBackground(Color.getHSBColor(0.45f, 0.69f, 0.75f));
                    header.setPreferredSize(new Dimension(header.getWidth(), 30));

                    FoodCourtSystem studentmenuFrame = new FoodCourtSystem();
                    studentmenuFrame.setTitle("Food Court DIU");
                    studentmenuFrame.setSize(1150, 850);
                    studentmenuFrame.setLocationRelativeTo(null);
                    studentmenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    studentmenuFrame.setIconImage(diuIcon.getImage());

                    Container smvc = studentmenuFrame.getContentPane();
                    smvc.setLayout(null);

                    JPanel leftStudentmenuPanel = new JPanel();
                    leftStudentmenuPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                    leftStudentmenuPanel.setBounds(0, 0, 450, 850);
                    leftStudentmenuPanel.setLayout(null);
                    smvc.add(leftStudentmenuPanel);

                    JPanel rightStudentmenuPanel = new JPanel();
                    rightStudentmenuPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                    rightStudentmenuPanel.setBounds(450, 0, 700, 850);
                    rightStudentmenuPanel.setLayout(null);
                    smvc.add(rightStudentmenuPanel);

                    JButton studentBackmenuButton = new JButton("Back");
                    studentBackmenuButton.setFont(new Font("Arial", Font.BOLD, 16));
                    studentBackmenuButton.setBounds(550, 750, 100, 40);
                    studentBackmenuButton.setBackground(Color.BLACK);
                    studentBackmenuButton.setForeground(Color.RED);
                    studentBackmenuButton.setFocusPainted(false);
                    rightStudentmenuPanel.add(studentBackmenuButton);

                    JLabel studentmenuDescription1 = new JLabel("Student Panel", SwingConstants.CENTER);
                    studentmenuDescription1.setFont(new Font("Arial", Font.BOLD, 24));
                    studentmenuDescription1.setForeground(Color.WHITE);
                    studentmenuDescription1.setOpaque(false);
                    studentmenuDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentmenuDescription1.setBounds(100, 100, 200, 50);
                    leftStudentmenuPanel.add(studentmenuDescription1);

                    JLabel studentmenuDescription2 = new JLabel("Student ID : " + studentId, SwingConstants.LEFT);
                    studentmenuDescription2.setFont(new Font("Arial", Font.BOLD, 20));
                    studentmenuDescription2.setForeground(Color.WHITE);
                    studentmenuDescription2.setOpaque(false);
                    studentmenuDescription2.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentmenuDescription2.setBounds(50, 300, 400, 50);
                    leftStudentmenuPanel.add(studentmenuDescription2);

                    JLabel studentmenuDescription3 = new JLabel("Student Name : " + studentName, SwingConstants.LEFT);
                    studentmenuDescription3.setFont(new Font("Arial", Font.BOLD, 20));
                    studentmenuDescription3.setForeground(Color.WHITE);
                    studentmenuDescription3.setOpaque(false);
                    studentmenuDescription3.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentmenuDescription3.setBounds(50, 330, 400, 50);
                    leftStudentmenuPanel.add(studentmenuDescription3);

                    JButton studentViewMenuOnlyButton = new JButton("Menu");
                    studentViewMenuOnlyButton.setFont(new Font("Arial", Font.BOLD, 22));
                    studentViewMenuOnlyButton.setBounds(150, 10, 400, 70);
                    rightStudentmenuPanel.add(studentViewMenuOnlyButton);

                    studentmenuFrame.setVisible(true);

                    scrollPane.setBounds(50, 200, 600, 500);
                    rightStudentmenuPanel.add(scrollPane);

                    studentBackmenuButton.addActionListener(se -> {
                        studentOptionFrame.setVisible(true);
                        studentmenuFrame.setVisible(false);
                    });

                    studentmenuFrame.setVisible(true);
                });

                JButton studentAddToOrderButton = new JButton("Add To Order");
                studentAddToOrderButton.setFont(new Font("Arial", Font.BOLD, 22));
                studentAddToOrderButton.setBounds(150, 320, 400, 70);
                rightStudentOptionPanel.add(studentAddToOrderButton);

                studentAddToOrderButton.addActionListener(sao -> {
                    studentOptionFrame.setVisible(false);

                    String[] columnNames = {"ID", "Name", "Price (Taka)", "Qty"};
                    Object[][] data = new Object[menu.size()][4];

                    for (int i = 0; i < menu.size(); i++) {
                        FoodItem item = menu.get(i);
                        data[i][0] = item.getId();
                        data[i][1] = item.getName();
                        data[i][2] = String.format("%.2f", item.getPrice());
                        data[i][3] = item.getQuantity();
                    }

                    JTable table = new JTable(data, columnNames);
                    JScrollPane scrollPane = new JScrollPane(table);
                    table.setFillsViewportHeight(true);

                    table.setFont(new Font("Arial", Font.PLAIN, 20));
                    table.setRowHeight(30);

                    JTableHeader header = table.getTableHeader();
                    header.setFont(new Font("Arial", Font.BOLD, 22));
                    header.setForeground(Color.WHITE);
                    header.setBackground(Color.getHSBColor(0.45f, 0.69f, 0.75f));
                    header.setPreferredSize(new Dimension(header.getWidth(), 30));

                    FoodCourtSystem studentAddOrderFrame = new FoodCourtSystem();
                    studentAddOrderFrame.setTitle("Food Court DIU");
                    studentAddOrderFrame.setSize(1150, 850);
                    studentAddOrderFrame.setLocationRelativeTo(null);
                    studentAddOrderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    studentAddOrderFrame.setIconImage(diuIcon.getImage());

                    Container saoc = studentAddOrderFrame.getContentPane();
                    saoc.setLayout(null);

                    JPanel leftStudentAddOrderPanel = new JPanel();
                    leftStudentAddOrderPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                    leftStudentAddOrderPanel.setBounds(0, 0, 450, 850);
                    leftStudentAddOrderPanel.setLayout(null);
                    saoc.add(leftStudentAddOrderPanel);

                    JPanel rightStudentAddOrderPanel = new JPanel();
                    rightStudentAddOrderPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                    rightStudentAddOrderPanel.setBounds(450, 0, 700, 850);
                    rightStudentAddOrderPanel.setLayout(null);
                    saoc.add(rightStudentAddOrderPanel);

                    JButton studentBackAddOrderButton = new JButton("Back");
                    studentBackAddOrderButton.setFont(new Font("Arial", Font.BOLD, 16));
                    studentBackAddOrderButton.setBounds(550, 750, 100, 40);
                    studentBackAddOrderButton.setBackground(Color.BLACK);
                    studentBackAddOrderButton.setForeground(Color.RED);
                    studentBackAddOrderButton.setFocusPainted(false);
                    rightStudentAddOrderPanel.add(studentBackAddOrderButton);

                    JLabel studentAddOrderDescription1 = new JLabel("Student Panel", SwingConstants.CENTER);
                    studentAddOrderDescription1.setFont(new Font("Arial", Font.BOLD, 24));
                    studentAddOrderDescription1.setForeground(Color.WHITE);
                    studentAddOrderDescription1.setOpaque(false);
                    studentAddOrderDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentAddOrderDescription1.setBounds(100, 100, 200, 50);
                    leftStudentAddOrderPanel.add(studentAddOrderDescription1);

                    JLabel studentAddOrderDescription2 = new JLabel("Student ID : " + studentId, SwingConstants.LEFT);
                    studentAddOrderDescription2.setFont(new Font("Arial", Font.BOLD, 20));
                    studentAddOrderDescription2.setForeground(Color.WHITE);
                    studentAddOrderDescription2.setOpaque(false);
                    studentAddOrderDescription2.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentAddOrderDescription2.setBounds(50, 300, 400, 50);
                    leftStudentAddOrderPanel.add(studentAddOrderDescription2);

                    JLabel studentAddOrderDescription3 = new JLabel("Student Name : " + studentName, SwingConstants.LEFT);
                    studentAddOrderDescription3.setFont(new Font("Arial", Font.BOLD, 20));
                    studentAddOrderDescription3.setForeground(Color.WHITE);
                    studentAddOrderDescription3.setOpaque(false);
                    studentAddOrderDescription3.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    studentAddOrderDescription3.setBounds(50, 330, 400, 50);
                    leftStudentAddOrderPanel.add(studentAddOrderDescription3);

                    JButton studentViewAddOrderOnlyButton = new JButton("Order Food");
                    studentViewAddOrderOnlyButton.setFont(new Font("Arial", Font.BOLD, 22));
                    studentViewAddOrderOnlyButton.setBounds(150, 10, 400, 70);
                    rightStudentAddOrderPanel.add(studentViewAddOrderOnlyButton);

                    studentAddOrderFrame.setVisible(true);

                    scrollPane.setBounds(50, 100, 600, 200);
                    rightStudentAddOrderPanel.add(scrollPane);

                    JLabel addOrderIdLabel = new JLabel("Enter Food ID :");
                    addOrderIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                    addOrderIdLabel.setBounds(225, 480, 200, 30);
                    rightStudentAddOrderPanel.add(addOrderIdLabel);

                    JTextField addOrderIdField = new JTextField();
                    addOrderIdField.setFont(new Font("Arial", Font.PLAIN, 16));
                    addOrderIdField.setBounds(225, 510, 200, 30);
                    rightStudentAddOrderPanel.add(addOrderIdField);

                    JLabel addOrderquantityLabel = new JLabel("Enter Food Quantity :");
                    addOrderquantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                    addOrderquantityLabel.setBounds(225, 550, 200, 30);
                    rightStudentAddOrderPanel.add(addOrderquantityLabel);

                    JTextField addOrderquantityField = new JTextField();
                    addOrderquantityField.setFont(new Font("Arial", Font.PLAIN, 16));
                    addOrderquantityField.setBounds(225, 580, 200, 30);
                    rightStudentAddOrderPanel.add(addOrderquantityField);

                    JButton addOrderButton = new JButton("Add to Order");
                    addOrderButton.setFont(new Font("Arial", Font.BOLD, 16));
                    addOrderButton.setBounds(225, 620, 150, 40);
                    rightStudentAddOrderPanel.add(addOrderButton);

                    addOrderButton.addActionListener(saof -> {
                        try {
                            String curentOrderId = addOrderIdField.getText();
                            int currentOrderQuantity = Integer.parseInt(addOrderquantityField.getText());
                            
                            if (curentOrderId.isEmpty() || currentOrderQuantity <= 0) {
                                JOptionPane.showMessageDialog(studentAddOrderFrame, 
                                    "Please enter valid ID and quantity", 
                                    "Invalid Input", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            
                            addToOrder(customer, curentOrderId, currentOrderQuantity);
                            studentAddOrderFrame.setVisible(false);
                            studentOptionFrame.setVisible(true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(studentAddOrderFrame, 
                                "Please enter a valid quantity", 
                                "Invalid Input", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    studentBackAddOrderButton.addActionListener(se -> {
                        studentOptionFrame.setVisible(true);
                        studentAddOrderFrame.setVisible(false);
                    });

                    studentAddOrderFrame.setVisible(true);
                });

                JButton studentViewCurrentOrderButton = new JButton("View Current Order");
                studentViewCurrentOrderButton.setFont(new Font("Arial", Font.BOLD, 22));
                studentViewCurrentOrderButton.setBounds(150, 400, 400, 70);
                rightStudentOptionPanel.add(studentViewCurrentOrderButton);

                studentViewCurrentOrderButton.addActionListener(sco -> {
                    viewCurrentOrder(customer);
                });

                JButton studentCheckoutButton = new JButton("Check Out");
                studentCheckoutButton.setFont(new Font("Arial", Font.BOLD, 22));
                studentCheckoutButton.setBounds(150, 480, 400, 70);
                rightStudentOptionPanel.add(studentCheckoutButton);

                studentCheckoutButton.addActionListener(sce -> {
                    checkOut(customer);
                    studentOptionFrame.setVisible(false);
                    frame.setVisible(true);
                });

                studentOptionFrame.setVisible(true);
            });

            JButton studentExitButton = new JButton("Back");
            studentExitButton.setFont(new Font("Arial", Font.BOLD, 16));
            studentExitButton.setBounds(550, 750, 100, 40);
            studentExitButton.setBackground(Color.BLACK);
            studentExitButton.setForeground(Color.RED);
            studentExitButton.setFocusPainted(false);
            rightStudentPanel.add(studentExitButton);

            studentExitButton.addActionListener(se -> {
                studentFrame.dispose();
                frame.setVisible(true);
            });

            studentFrame.setVisible(true);
        });
      





        adminLoginButton.addActionListener(e-> {
            frame.dispose(); 

            FoodCourtSystem adminFrame = new FoodCourtSystem();
            adminFrame.setTitle("Food Court DIU");
            adminFrame.setSize(1150, 850);
            adminFrame.setLocationRelativeTo(null);
            adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            adminFrame.setIconImage(diuIcon.getImage());

            Container ac = adminFrame.getContentPane();
            ac.setLayout(null);

            JPanel leftAdminPanel = new JPanel();
            leftAdminPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
            leftAdminPanel.setBounds(0, 0, 450, 850);
            leftAdminPanel.setLayout(null);
            ac.add(leftAdminPanel);

            JPanel rightAdminPanel = new JPanel();
            rightAdminPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
            rightAdminPanel.setBounds(450, 0, 700, 850);
            rightAdminPanel.setLayout(null);
            ac.add(rightAdminPanel);

            JButton adminExitButton = new JButton("Back");
            adminExitButton.setFont(new Font("Arial", Font.BOLD, 16));
            adminExitButton.setBounds(550, 750, 100, 40); 
            adminExitButton.setBackground(Color.BLACK);
            adminExitButton.setForeground(Color.RED);
            adminExitButton.setFocusPainted(false);
            rightAdminPanel.add(adminExitButton);


            JLabel adminDescription1= new JLabel("Admin Pannel", SwingConstants.CENTER);
            adminDescription1.setFont(new Font("Arial", Font.BOLD, 24));
            adminDescription1.setForeground(Color.DARK_GRAY);
            adminDescription1.setOpaque(false);
            adminDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
            adminDescription1.setBounds(100,100, 200, 50);
            leftAdminPanel.add(adminDescription1);


            JLabel AdminIdLabel = new JLabel("Enter Admin ID :");
            AdminIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            AdminIdLabel.setBounds(225, 280, 200, 30);
            rightAdminPanel.add(AdminIdLabel);


            JTextField adminIdField = new JTextField();
            adminIdField.setFont(new Font("Arial", Font.PLAIN, 16));
            adminIdField.setBounds(225, 310, 200, 30);
            rightAdminPanel.add(adminIdField);

            JLabel adminNameLabel = new JLabel("Enter Your Name :");
            adminNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            adminNameLabel.setBounds(225, 350, 200, 30);
            rightAdminPanel.add(adminNameLabel);


            JTextField adminNameField= new JTextField();
            adminNameField.setFont(new Font("Arial", Font.PLAIN, 16));
            adminNameField.setBounds(225, 380, 200, 30);
            rightAdminPanel.add(adminNameField);

            JLabel passwordLabel = new JLabel("Enter Password:");
            passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            passwordLabel.setBounds(225, 420, 200, 30);
            rightAdminPanel.add(passwordLabel);


            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
            passwordField.setBounds(225, 450, 200, 30);
            passwordField.setEchoChar('●'); 
            rightAdminPanel.add(passwordField);

            JCheckBox viewPassword = new JCheckBox("View Password");
            viewPassword.setFont(new Font("Arial", Font.PLAIN, 14));
            viewPassword.setBounds(225, 490, 150, 20);
            viewPassword.setOpaque(false);
            rightAdminPanel.add(viewPassword);


            viewPassword.addActionListener(aep -> {
                if (viewPassword.isSelected()) {
                    passwordField.setEchoChar((char) 0); 
                } else {
                    passwordField.setEchoChar('●'); 
                }
            });



            passwordField.addActionListener(aep -> {
                String enteredAdminPassword = new String(passwordField.getPassword());
                String adminId = adminIdField.getText(); 
                String adminName=adminNameField.getText();

              

                if (Admin.checkPassword(enteredAdminPassword)) {

                    Admin admin = new Admin(adminId, adminName);



                    adminFrame.dispose();

                    FoodCourtSystem adminOptionFrame = new FoodCourtSystem();
                    adminOptionFrame.setTitle("Food Court DIU");
                    adminOptionFrame.setSize(1150, 850);
                    adminOptionFrame.setLocationRelativeTo(null);
                    adminOptionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    adminOptionFrame.setIconImage(diuIcon.getImage());

                    Container aoc = adminOptionFrame.getContentPane();
                    aoc.setLayout(null);

                    JPanel leftAdminOptionPanel = new JPanel();
                    leftAdminOptionPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                    leftAdminOptionPanel.setBounds(0, 0, 450, 850);
                    leftAdminOptionPanel.setLayout(null);
                    aoc.add(leftAdminOptionPanel);

                    JPanel rightAdminOptionPanel = new JPanel();
                    rightAdminOptionPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                    rightAdminOptionPanel.setBounds(450, 0, 700, 850);
                    rightAdminOptionPanel.setLayout(null);
                    aoc.add(rightAdminOptionPanel);

                   JButton adminBackOptionButton = new JButton("Back");
                   adminBackOptionButton.setFont(new Font("Arial", Font.BOLD, 16));
                   adminBackOptionButton.setBounds(550, 750, 100, 40); 
                   adminBackOptionButton.setBackground(Color.BLACK);
                   adminBackOptionButton.setForeground(Color.RED);
                   adminBackOptionButton.setFocusPainted(false);
                   rightAdminOptionPanel.add(adminBackOptionButton);

                    JLabel adminOptionDescription1= new JLabel("Admin Pannel", SwingConstants.CENTER);
                    adminOptionDescription1.setFont(new Font("Arial", Font.BOLD, 24));
                    adminOptionDescription1.setForeground(Color.WHITE);
                    adminOptionDescription1.setOpaque(false);
                    adminOptionDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    adminOptionDescription1.setBounds(100,100, 200, 50);
                    leftAdminOptionPanel.add(adminOptionDescription1);

                    JLabel adminOptionDescription2= new JLabel("Admin ID : "+adminId, SwingConstants.LEFT);
                    adminOptionDescription2.setFont(new Font("Arial", Font.BOLD, 20));
                    adminOptionDescription2.setForeground(Color.WHITE);
                    adminOptionDescription2.setOpaque(false);
                    adminOptionDescription2.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    adminOptionDescription2.setBounds(50,300, 400, 50);
                    leftAdminOptionPanel.add(adminOptionDescription2);

                    JLabel adminOptionDescription3= new JLabel("Admin Name : "+adminName, SwingConstants.LEFT);
                    adminOptionDescription3.setFont(new Font("Arial", Font.BOLD, 20));
                    adminOptionDescription3.setForeground(Color.WHITE);
                    adminOptionDescription3.setOpaque(false);
                    adminOptionDescription3.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                    adminOptionDescription3.setBounds(50,330, 400, 50);
                    leftAdminOptionPanel.add(adminOptionDescription3);

                   JButton adminViewProductButton = new JButton("View All Item");
                   adminViewProductButton.setFont(new Font("Arial", Font.BOLD, 22));
                   adminViewProductButton.setBounds(150, 240, 400, 70);
                   rightAdminOptionPanel.add(adminViewProductButton);

                   JButton adminAddItemButton = new JButton("Add new food item");
                   adminAddItemButton.setFont(new Font("Arial", Font.BOLD, 22));
                   adminAddItemButton.setBounds(150, 320, 400, 70);
                   rightAdminOptionPanel.add(adminAddItemButton);

                   JButton adminincreaseItemButton = new JButton("Increase item quantity");
                   adminincreaseItemButton.setFont(new Font("Arial", Font.BOLD, 22));
                   adminincreaseItemButton.setBounds(150, 400, 400, 70);
                   rightAdminOptionPanel.add(adminincreaseItemButton);

                   JButton adminViewHistoryButton = new JButton("View order history");
                   adminViewHistoryButton.setFont(new Font("Arial", Font.BOLD, 22));
                   adminViewHistoryButton.setBounds(150, 480, 400, 70);
                   rightAdminOptionPanel.add(adminViewHistoryButton);

                   adminBackOptionButton.addActionListener(ae -> {
                        int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit from admin?", "Exit Confirmation",2,JOptionPane.YES_NO_OPTION);
                        if(choice ==JOptionPane.YES_NO_OPTION){
                        adminOptionFrame.dispose();
                        frame.setVisible(true);   
                        }
                         
                    });

















                   adminViewProductButton.addActionListener(sve->{
                     adminOptionFrame.setVisible(false);;
                    

                     String[] columnNames = { "ID", "Name", "Price (Taka)", "Qty" };

                     Object[][] data = new Object[menu.size()][4];

                     for (int i = 0; i < menu.size(); i++) {
                         FoodItem item = menu.get(i);
                         data[i][0] = item.getId();
                         data[i][1] = item.getName();
                         data[i][2] = String.format("%.2f", item.getPrice());
                         data[i][3] = item.getQuantity();
                        }


                     JTable table = new JTable(data, columnNames);
                     JScrollPane scrollPane = new JScrollPane(table);
                     table.setFillsViewportHeight(true);

     
                     table.setFont(new Font("Arial", Font.PLAIN, 20));
                     table.setRowHeight(30);
                   

                     JTableHeader header = table.getTableHeader();
                     header.setFont(new Font("Arial", Font.BOLD, 22)); 
                     header.setForeground(Color.WHITE);               
                     header.setBackground(Color.getHSBColor(0.45f, 0.69f,0.75f));           
                     header.setPreferredSize(new Dimension(header.getWidth(), 30)); 



                     FoodCourtSystem adminmenuFrame = new FoodCourtSystem();
                     adminmenuFrame.setTitle("Food Court DIU");
                     adminmenuFrame.setSize(1150, 850);
                     adminmenuFrame.setLocationRelativeTo(null);
                     adminmenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                     adminmenuFrame.setIconImage(diuIcon.getImage());

                     Container amvc = adminmenuFrame.getContentPane();
                     amvc.setLayout(null);

                     JPanel leftAdminmenuPanel = new JPanel();
                     leftAdminmenuPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                     leftAdminmenuPanel.setBounds(0, 0, 450, 850);
                     leftAdminmenuPanel.setLayout(null);
                     amvc.add(leftAdminmenuPanel);

                     JPanel rightAdminmenuPanel = new JPanel();
                     rightAdminmenuPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                     rightAdminmenuPanel.setBounds(450, 0, 700, 850);
                     rightAdminmenuPanel.setLayout(null);
                     amvc.add(rightAdminmenuPanel);

                     JButton adminBackmenuButton = new JButton("Back");
                     adminBackmenuButton.setFont(new Font("Arial", Font.BOLD, 16));
                     adminBackmenuButton.setBounds(550, 750, 100, 40); 
                     adminBackmenuButton.setBackground(Color.BLACK);
                     adminBackmenuButton.setForeground(Color.RED);
                     adminBackmenuButton.setFocusPainted(false);
                     rightAdminmenuPanel.add(adminBackmenuButton);

                 

                        JLabel adminmenuDescription1= new JLabel("Admin Pannel", SwingConstants.CENTER);
                        adminmenuDescription1.setFont(new Font("Arial", Font.BOLD, 24));
                        adminmenuDescription1.setForeground(Color.WHITE);
                        adminmenuDescription1.setOpaque(false);
                        adminmenuDescription1.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                        adminmenuDescription1.setBounds(100,100, 200, 50);
                        leftAdminmenuPanel.add(adminmenuDescription1);

                        JLabel admminmenuDescription2= new JLabel("AMDIN ID : "+adminId, SwingConstants.LEFT);
                        admminmenuDescription2.setFont(new Font("Arial", Font.BOLD, 20));
                        admminmenuDescription2.setForeground(Color.WHITE);
                        admminmenuDescription2.setOpaque(false);
                        admminmenuDescription2.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                        admminmenuDescription2.setBounds(50,300, 400, 50);
                        leftAdminmenuPanel.add(admminmenuDescription2);

                        JLabel adminmenuDescription3= new JLabel("Admin Name : "+adminName, SwingConstants.LEFT);
                        adminmenuDescription3.setFont(new Font("Arial", Font.BOLD, 20));
                        adminmenuDescription3.setForeground(Color.WHITE);
                        adminmenuDescription3.setOpaque(false);
                        adminmenuDescription3.setToolTipText("This Java-based OOP project, developed for the OOP course at DIU.");
                        adminmenuDescription3.setBounds(50,330, 400, 50);
                        leftAdminmenuPanel.add(adminmenuDescription3);

                       JButton adminViewMenuOnlyButton = new JButton("Menu");
                       adminViewMenuOnlyButton.setFont(new Font("Arial", Font.BOLD, 22));
                       adminViewMenuOnlyButton.setBounds(150, 10, 400, 70);
                       rightAdminmenuPanel.add(adminViewMenuOnlyButton);

                       adminmenuFrame.setVisible(true);

                       scrollPane.setBounds(50,200,600,500);

                       rightAdminmenuPanel.add(scrollPane);


                        adminBackmenuButton.addActionListener(se -> {
                           adminOptionFrame.setVisible(true);
                           adminmenuFrame.setVisible(false);
                         
                
                        });


                       adminmenuFrame.setVisible(true);


                    });


                 

                    adminAddItemButton.addActionListener(ae -> {
                        adminOptionFrame.setVisible(false);
    
                        FoodCourtSystem addItemFrame = new FoodCourtSystem();
                        addItemFrame.setTitle("Add New Food Item");
                        addItemFrame.setSize(1150, 850);
                        addItemFrame.setLocationRelativeTo(null);
                        addItemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        addItemFrame.setIconImage(diuIcon.getImage());

                        Container aic = addItemFrame.getContentPane();
                        aic.setLayout(null);

                        JPanel leftAddItemPanel = new JPanel();
                        leftAddItemPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                        leftAddItemPanel.setBounds(0, 0, 450, 850);
                        leftAddItemPanel.setLayout(null);
                        aic.add(leftAddItemPanel);
     
                        JPanel rightAddItemPanel = new JPanel();
                        rightAddItemPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                        rightAddItemPanel.setBounds(450, 0, 700, 850);
                        rightAddItemPanel.setLayout(null);
                        aic.add(rightAddItemPanel);

                        JButton backButton = new JButton("Back");
                        backButton.setFont(new Font("Arial", Font.BOLD, 16));
                        backButton.setBounds(550, 750, 100, 40);
                        backButton.setBackground(Color.BLACK);
                        backButton.setForeground(Color.RED);
                        backButton.setFocusPainted(false);
                        rightAddItemPanel.add(backButton);

                        JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
                        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                        titleLabel.setForeground(Color.WHITE);
                        titleLabel.setBounds(100, 100, 200, 50);
                        leftAddItemPanel.add(titleLabel);

                        JLabel idLabel = new JLabel("Admin ID : " + adminId, SwingConstants.LEFT);
                        idLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        idLabel.setForeground(Color.WHITE);
                        idLabel.setBounds(50, 300, 400, 50);
                        leftAddItemPanel.add(idLabel);

                        JLabel nameLabel = new JLabel("Admin Name : " + adminName, SwingConstants.LEFT);
                        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        nameLabel.setForeground(Color.WHITE);
                        nameLabel.setBounds(50, 330, 400, 50);
                        leftAddItemPanel.add(nameLabel);

                        JButton addItemonlyViewButton = new JButton("Add New Food Item");
                        addItemonlyViewButton.setFont(new Font("Arial", Font.BOLD, 24));
                        addItemonlyViewButton.setBounds(150, 30, 400, 50);
                        rightAddItemPanel.add(addItemonlyViewButton);

                        JLabel itemIdLabel = new JLabel("Item ID:");
                        itemIdLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemIdLabel.setBounds(150, 260, 100, 50);
                        rightAddItemPanel.add(itemIdLabel);

                        JTextField itemIdField = new JTextField();
                        itemIdField.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemIdField.setBounds(250, 260, 300, 50);
                        rightAddItemPanel.add(itemIdField);

                        JLabel itemNameLabel = new JLabel("Item Name:");
                        itemNameLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemNameLabel.setBounds(150, 340, 100, 50);
                        rightAddItemPanel.add(itemNameLabel);

                        JTextField itemNameField = new JTextField();
                        itemNameField.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemNameField.setBounds(250, 340, 300, 50);
                        rightAddItemPanel.add(itemNameField);

                        JLabel itemPriceLabel = new JLabel("Item Price:");
                        itemPriceLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemPriceLabel.setBounds(150, 420, 100, 50);
                        rightAddItemPanel.add(itemPriceLabel);

                        JTextField itemPriceField = new JTextField();
                        itemPriceField.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemPriceField.setBounds(250, 420, 300, 50);
                        rightAddItemPanel.add(itemPriceField);

                        JLabel itemQuantityLabel = new JLabel("Quantity:");
                        itemQuantityLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemQuantityLabel.setBounds(150, 500, 100, 50);
                        rightAddItemPanel.add(itemQuantityLabel);

                        JTextField itemQuantityField = new JTextField();
                        itemQuantityField.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemQuantityField.setBounds(250, 500, 300, 50);
                        rightAddItemPanel.add(itemQuantityField);

                        JButton addItemButton = new JButton("Add Item");
                        addItemButton.setFont(new Font("Arial", Font.BOLD, 19));
                        addItemButton.setBounds(300, 600, 150, 50);
                        rightAddItemPanel.add(addItemButton);

                        addItemButton.addActionListener(aie -> {
                            try {
                                String id = itemIdField.getText();
                                String name = itemNameField.getText();
                                double price = Double.parseDouble(itemPriceField.getText());
                                int quantity = Integer.parseInt(itemQuantityField.getText());

                                if (id.isEmpty() || name.isEmpty()) {
                                    JOptionPane.showMessageDialog(addItemFrame, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                FoodItem newItem = new FoodItem(id, name, price, quantity);
                                menu.add(newItem);
                                JOptionPane.showMessageDialog(addItemFrame, "Item added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            
                                itemIdField.setText("");
                                itemNameField.setText("");
                                itemPriceField.setText("");
                                itemQuantityField.setText("");
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(addItemFrame, "Please enter valid numbers for price and quantity", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });

                        backButton.addActionListener(aie -> {
                            addItemFrame.dispose();
                            adminOptionFrame.setVisible(true);
                        });

                        addItemFrame.setVisible(true);
                    });



                    adminincreaseItemButton.addActionListener(ae -> {
                        adminOptionFrame.setVisible(false);
    
                        String[] columnNames = {"ID", "Name", "Price (Taka)", "Qty"};
                        Object[][] data = new Object[menu.size()][4];

                        for (int i = 0; i < menu.size(); i++) {
                            FoodItem item = menu.get(i);
                            data[i][0] = item.getId();
                            data[i][1] = item.getName();
                            data[i][2] = String.format("%.2f", item.getPrice());
                            data[i][3] = item.getQuantity();
                        }

                        JTable table = new JTable(data, columnNames);
                        JScrollPane scrollPane = new JScrollPane(table);
                        table.setFillsViewportHeight(true);

                        table.setFont(new Font("Arial", Font.PLAIN, 20));
                        table.setRowHeight(30);

                        JTableHeader header = table.getTableHeader();
                        header.setFont(new Font("Arial", Font.BOLD, 22));
                        header.setForeground(Color.WHITE);
                        header.setBackground(Color.getHSBColor(0.45f, 0.69f, 0.75f));
                        header.setPreferredSize(new Dimension(header.getWidth(), 30));

                        FoodCourtSystem increaseQuantityFrame = new FoodCourtSystem();
                        increaseQuantityFrame.setTitle("Increase Item Quantity");
                        increaseQuantityFrame.setSize(1150, 850);
                        increaseQuantityFrame.setLocationRelativeTo(null);
                        increaseQuantityFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        increaseQuantityFrame.setIconImage(diuIcon.getImage());

                        Container iqc = increaseQuantityFrame.getContentPane();
                        iqc.setLayout(null);

                        JPanel leftIncreasePanel = new JPanel();
                        leftIncreasePanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                        leftIncreasePanel.setBounds(0, 0, 450, 850);
                        leftIncreasePanel.setLayout(null);
                        iqc.add(leftIncreasePanel);

                        JPanel rightIncreasePanel = new JPanel();
                        rightIncreasePanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                        rightIncreasePanel.setBounds(450, 0, 700, 850);
                        rightIncreasePanel.setLayout(null);
                        iqc.add(rightIncreasePanel);

                        JButton backButton = new JButton("Back");
                        backButton.setFont(new Font("Arial", Font.BOLD, 16));
                        backButton.setBounds(550, 750, 100, 40);
                        backButton.setBackground(Color.BLACK);
                        backButton.setForeground(Color.RED);
                        backButton.setFocusPainted(false);
                        rightIncreasePanel.add(backButton);

                        JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
                        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                        titleLabel.setForeground(Color.WHITE);
                        titleLabel.setBounds(100, 100, 200, 50);
                        leftIncreasePanel.add(titleLabel);

                        JLabel idLabel = new JLabel("ADMIN ID : " + adminId, SwingConstants.LEFT);
                        idLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        idLabel.setForeground(Color.WHITE);
                        idLabel.setBounds(50, 300, 400, 50);
                        leftIncreasePanel.add(idLabel);

                        JLabel nameLabel = new JLabel("Admin Name : " + adminName, SwingConstants.LEFT);
                        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                        nameLabel.setForeground(Color.WHITE);
                        nameLabel.setBounds(50, 330, 400, 50);
                        leftIncreasePanel.add(nameLabel);

                        JButton increaseItemOnlyviewButton = new JButton("Increase Item Quantity");
                        increaseItemOnlyviewButton.setFont(new Font("Arial", Font.BOLD, 24));
                        increaseItemOnlyviewButton.setBounds(150, 30, 400, 50);
                        rightIncreasePanel.add(increaseItemOnlyviewButton);

                        scrollPane.setBounds(50, 120, 600, 300);
                        rightIncreasePanel.add(scrollPane);

                        JLabel itemIdLabel = new JLabel("Item ID :");
                        itemIdLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemIdLabel.setBounds(150, 460, 150, 50);
                        rightIncreasePanel.add(itemIdLabel);

                        JTextField itemIdField = new JTextField();
                        itemIdField.setFont(new Font("Arial", Font.PLAIN, 19));
                        itemIdField.setBounds(300, 460, 200, 50);
                        rightIncreasePanel.add(itemIdField);

                        JLabel quantityLabel = new JLabel("New Item Add:");
                        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 19));
                        quantityLabel.setBounds(150, 520, 150, 50);
                        rightIncreasePanel.add(quantityLabel);

                        JTextField quantityField = new JTextField();
                        quantityField.setFont(new Font("Arial", Font.PLAIN, 19));
                        quantityField.setBounds(300, 520, 200, 50);
                        rightIncreasePanel.add(quantityField);

                        JButton increaseButton = new JButton("Increase Quantity");
                        increaseButton.setFont(new Font("Arial", Font.BOLD, 19));
                        increaseButton.setBounds(300, 650, 200, 40);
                        rightIncreasePanel.add(increaseButton);

                        increaseButton.addActionListener(ie -> {
                            try {
                                String id = itemIdField.getText();
                                int quantity = Integer.parseInt(quantityField.getText());

                                if (quantity <= 0) {
                                    JOptionPane.showMessageDialog(increaseQuantityFrame, "Quantity must be positive",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                boolean found = false;
                                for (FoodItem item : menu) {
                                    if (item.getId().equals(id)) {
                                        item.setQuantity(item.getQuantity() + quantity);
                                        found = true;
                                        JOptionPane.showMessageDialog(increaseQuantityFrame,
                                                "Quantity increased successfully", "Success",
                                                JOptionPane.INFORMATION_MESSAGE);

                                        
                                        for (int i = 0; i < menu.size(); i++) {
                                            if (menu.get(i).getId().equals(id)) {
                                                table.setValueAt(menu.get(i).getQuantity(), i, 3);
                                                break;
                                            }
                                        }

                                        
                                        itemIdField.setText("");
                                        quantityField.setText("");
                                        break;
                                    }
                                }

                                if (!found) {
                                    JOptionPane.showMessageDialog(increaseQuantityFrame, "Item not found", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(increaseQuantityFrame, "Please enter a valid quantity",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });

                        backButton.addActionListener(aine -> {
                            increaseQuantityFrame.dispose();
                            adminOptionFrame.setVisible(true);
                        });

                        increaseQuantityFrame.setVisible(true);
                    });





























                  adminViewHistoryButton.addActionListener(sve -> {
                     adminOptionFrame.setVisible(false);

                     FoodCourtSystem adminHistoryFrame = new FoodCourtSystem();
                     adminHistoryFrame.setTitle("Order History - Food Court DIU");
                     adminHistoryFrame.setSize(1150, 850);
                     adminHistoryFrame.setLocationRelativeTo(null);
                     adminHistoryFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                     adminHistoryFrame.setIconImage(diuIcon.getImage());

                     Container ahc = adminHistoryFrame.getContentPane();
                     ahc.setLayout(null);

                     JPanel historyLeftPanel = new JPanel();
                     historyLeftPanel.setBackground(Color.getHSBColor(0.5014f, 0.4f, 0.667f));
                     historyLeftPanel.setBounds(0, 0, 450, 850);
                     historyLeftPanel.setLayout(null);
                     ahc.add(historyLeftPanel);

                     JPanel histoyRightPanel = new JPanel();
                     histoyRightPanel.setBackground(Color.getHSBColor(0.520f, 0.168f, 0.980f));
                     histoyRightPanel.setBounds(450, 0, 700, 850);
                     histoyRightPanel.setLayout(null);
                     ahc.add(histoyRightPanel);


                     JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
                     titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                     titleLabel.setForeground(Color.WHITE);
                     titleLabel.setBounds(100, 100, 200, 50);
                     historyLeftPanel.add(titleLabel);

                     JLabel idLabel = new JLabel("ADMIN ID : " + adminId, SwingConstants.LEFT);
                     idLabel.setFont(new Font("Arial", Font.BOLD, 20));
                     idLabel.setForeground(Color.WHITE);
                     idLabel.setBounds(50, 300, 400, 50);
                     historyLeftPanel.add(idLabel);

                     JLabel nameLabel = new JLabel("Admin Name : " + adminName, SwingConstants.LEFT);
                     nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                     nameLabel.setForeground(Color.WHITE);
                     nameLabel.setBounds(50, 330, 400, 50);
                     historyLeftPanel.add(nameLabel);

  
                     JButton backButton = new JButton("Back");
                     backButton.setFont(new Font("Arial", Font.BOLD, 16));
                     backButton.setBounds(550, 750, 100, 40);
                     backButton.setBackground(Color.BLACK);
                     backButton.setForeground(Color.RED);
                     backButton.setFocusPainted(false);
                     histoyRightPanel.add(backButton);


                     JTextArea textArea = new JTextArea();
                     textArea.setEditable(false);
                     textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                     JScrollPane scrollPane = new JScrollPane(textArea);
                     scrollPane.setBounds(130, 150, 450, 530);
                     histoyRightPanel.add(scrollPane);


                     JButton historyViewOnlyButton= new JButton("History");
                     historyViewOnlyButton.setFont(new Font("Arial", Font.BOLD, 30));
                     historyViewOnlyButton.setBounds(150, 10, 400, 70);
                     historyViewOnlyButton.setBackground(Color.BLACK);
                     historyViewOnlyButton.setForeground(Color.DARK_GRAY);
                     historyViewOnlyButton.setFocusPainted(false);
                     histoyRightPanel.add(historyViewOnlyButton);


    
                     StringBuilder sb = new StringBuilder();
                     sb.append("\n\n\n");

                       if (orderHistory.isEmpty()) {
                            sb.append("No one order yet.\n");
                        } 
                        else {
                             for (Order order : orderHistory) {
                                 sb.append("   Order ID: ").append(order.getOrderId()).append("\n");
                                 sb.append("   Student ID: ").append(order.getStrudentId()).append("\n");
                                 sb.append("   Order Date: ").append(order.getOrDateTime()
                                 .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
                                 sb.append("    Items:\n\n");

                                 double total = 0;
                                 for (OrderItem item : order.getItems()) {
                                      sb.append(String.format("  %-20s x%-5d %-8.2f Taka\n",
                                      item.getFoodItem().getName(),
                                      item.getQuantity(),
                                      item.getTotalAmount()));
                                       total += item.getTotalAmount();
                                    }

                                 sb.append("\n   Total Amount: ").append(String.format("%.2f Taka", total)).append("\n");
                                 sb.append("   Status: ").append(order.isPaid() ? "Paid" : "Pending").append("\n");
                                 sb.append("-----------------------------------------------------\n\n\n");
                                }
                            }

                            textArea.setText(sb.toString());

   
                            backButton.addActionListener(ahe -> {
                                adminHistoryFrame.setVisible(false);; 
                                adminOptionFrame.setVisible(true); 
                            });
                            adminHistoryFrame.setVisible(true);
                    });






                    adminOptionFrame.setVisible(true);
  
                } else {
 
                    JOptionPane.showMessageDialog(frame, "Incorrect password. Try again.","Worng Password",2);
                }
            });



        
            adminExitButton.addActionListener(ae -> {
                adminFrame.dispose();
                frame.setVisible(true);
                
            });




            adminFrame.setVisible(true);
        });


        frame.setVisible(true);

    }



}
