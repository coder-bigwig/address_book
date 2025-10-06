package system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 通讯录管理系统GUI主类，负责用户界面展示和交互
 */
public class ContactManagerGUI extends JFrame {
    // 业务逻辑层服务
    private ContactService service;
    
    // 界面组件
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;    //输入搜索关键词的文本框
    private JTextField nameField;      //输入或编辑联系人姓名的文本框。
    private JTextField phoneField;     //输入或编辑联系人电话的文本框。
    private JTextField emailField;
    private JTextField addressField;
    private JCheckBox blacklistedCheckBox; // 添加黑名单复选框
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton returnButton; //返回键按钮
    
    // 用于标识当前选中的联系人ID，常见于联系人管理、表格操作等场景
    private int selectedContactId = -1;

    /**
     * 构造函数，初始化GUI和业务服务
     */
    public ContactManagerGUI() {
        service = new ContactService();
        initializeGUI();   //初始化图形用户界面（GUI），例如创建窗口、按钮、表格等组件
        loadAllContacts();  //从数据库加载所有联系人
        setVisible(false); // 初始时不显示主界面
    }

    /**
     * 初始化GUI界面
     */
    private void initializeGUI() {
        setTitle("Java 通讯录管理系统");
        //setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //完全退出应用程序
        setLayout(new BorderLayout());                    // BorderLayout为窗口的布局管理器，分为 5个区域：NORTH、SOUTH、EAST、WEST、CENTER
        
        // 创建主面板
        createMainPanel();
        
        // 默认所有按钮可见
        addButton.setVisible(true);
        updateButton.setVisible(true);
        deleteButton.setVisible(true);
        
        setSize(1000, 750);          //将主窗口的尺寸设置为 宽度 1000 像素，高度 750 像素
        setLocationRelativeTo(null);    //设置窗口居中显示
        setResizable(true);             //允许窗口调整大小
    }

    /**
     * 创建主面板，包含搜索区、表格区和输入区
     */
    private void createMainPanel() {
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);

        JPanel decorationPanel = createDecorationPanel();
        add(decorationPanel, BorderLayout.EAST);
    }

    /**
     * 创建右侧装饰面板
     */
    private JPanel createDecorationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBackground(new Color(245, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建三个装饰性面板
        JPanel imagePanel1 = createDecorationSubPanel("📱 联系人管理", "🌟<br>加油！<br>管理您的<br>联系人信息");
        JPanel imagePanel2 = createDecorationSubPanel("📞 快速操作", "✨<br>轻松添加<br>编辑删除<br>联系人");
        JPanel imagePanel3 = createDecorationSubPanel("🔍 智能搜索", "🎯<br>快速查找<br>您需要的<br>联系人");

        // 添加面板和间距
        panel.add(imagePanel1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(imagePanel2);
        panel.add(Box.createVerticalStrut(10));
        panel.add(imagePanel3);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * 创建装饰性子面板（用于右侧装饰面板的内部面板）
     * @param title 面板标题
     * @param text 面板显示的HTML文本内容
     * @return 配置好的子面板
     */
    private JPanel createDecorationSubPanel(String title, String text) {     // 带有标题和文本内容的装饰性面板
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        panel.setPreferredSize(new Dimension(180, 150));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JLabel label = new JLabel();
        label.setText("<html><div style='text-align: center; color: #666;'>" + text + "</div></html>");
        label.setHorizontalAlignment(JLabel.CENTER);          // 文本水平居中
        panel.add(label);                                     // 将标签添加到面板
        return panel;
    }

    /**
     * 创建搜索面板
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 搜索联系人"));
        panel.setBackground(new Color(250, 250, 250));

        // 创建搜索组件
        JLabel searchLabel = new JLabel("搜索关键字：");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("🔍 搜索");
        JButton showAllButton = new JButton("📋 显示全部");

        // 设置按钮样式
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.DARK_GRAY);
        searchButton.setFocusPainted(false);
        showAllButton.setBackground(new Color(60, 179, 113));
        showAllButton.setForeground(Color.DARK_GRAY);
        showAllButton.setFocusPainted(false);

        // 添加事件监听
        searchButton.addActionListener(e -> searchContacts());
        showAllButton.addActionListener(e -> loadAllContacts());
        searchField.addActionListener(e -> searchContacts());

        // 添加组件到面板
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(showAllButton);

        return panel;
    }

    /**
     * 创建联系人表格面板
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 联系人列表"));
        panel.setBackground(new Color(250, 250, 250));

        // 创建表格模型
        String[] columnNames = {"ID", "姓名", "电话", "邮箱", "地址", "黑名单"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 创建表格并设置样式
        contactTable = new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setRowHeight(30);
        contactTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        contactTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        contactTable.getTableHeader().setBackground(new Color(230, 230, 250));

        // 设置列宽
        contactTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID列
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(100); // 姓名
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(120); // 电话
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(200); // 邮箱
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(200); // 地址
        contactTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 黑名单

        // 添加表格点击事件
        contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectContact();
            }
        });

        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建输入面板
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("✏️ 联系人信息"));
        panel.setBackground(new Color(250, 250, 250));

        // 创建输入字段面板
        JPanel fieldsPanel = createFieldsPanel();
        panel.add(fieldsPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 创建联系人输入字段面板（包含姓名、电话、邮箱、地址和黑名单复选框）
     * @return 配置好的GridBagLayout面板
     */
    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 250, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // 姓名字段
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // 电话字段
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("电话:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);

        // 邮箱字段
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("邮箱:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // 地址字段
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("地址:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        panel.add(addressField, gbc);

        // 黑名单复选框
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("黑名单:"), gbc);
        gbc.gridx = 1;
        blacklistedCheckBox = new JCheckBox("加入黑名单");
        panel.add(blacklistedCheckBox, gbc);

        return panel;
    }

    /**
     * 创建操作按钮面板（包含添加/更新/删除/清空按钮）
     * @return 配置好的按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(250, 250, 250));

        // 初始化按钮
        addButton = createStyledButton("➕ 添加", new Color(50, 205, 50));
        updateButton = createStyledButton("✏️ 更新", new Color(65, 105, 225));
        deleteButton = createStyledButton("❌ 删除", new Color(220, 20, 60));
        clearButton = createStyledButton("🧹 清空", new Color(169, 169, 169));
        returnButton = createStyledButton("🔙 返回", new Color(255, 165, 0)); // 添加返回按钮

        // 添加事件监听
        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        clearButton.addActionListener(e -> clearFields());
        returnButton.addActionListener(e -> returnToFunctionSelection()); // 添加返回事件监听

        // 添加按钮到面板
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(returnButton);

        return panel;
    }

    // 添加返回到功能选择界面的方法
    private void returnToFunctionSelection() {
        this.dispose(); // 关闭当前窗口
        showFunctionSelection(); // 显示功能选择界面
    }
    
    /**
     * 创建风格统一的按钮
     * @param text 按钮文字
     * @param bgColor 背景颜色
     * @return 配置好的JButton
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * 处理表格行选择事件，将选中联系人的数据填充到输入字段
     */
    private void selectContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 从表格中获取ID
            selectedContactId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            
            // 从数据库获取完整的联系人信息
            Contact contact = service.getContact(selectedContactId);
            if (contact != null) {
                nameField.setText(contact.getName());
                phoneField.setText(contact.getPhone());
                emailField.setText(contact.getEmail() != null ? contact.getEmail() : "");
                addressField.setText(contact.getAddress() != null ? contact.getAddress() : "");
                blacklistedCheckBox.setSelected(contact.isBlacklisted());
            }
        }
    }

    /**
     * 添加联系人
     */
    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        boolean isBlacklisted = blacklistedCheckBox.isSelected();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "姓名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "电话号码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = service.addContact(name, phone, 
                                           email.isEmpty() ? null : email, 
                                           address.isEmpty() ? null : address,
                                           isBlacklisted);
        if (success) {
            JOptionPane.showMessageDialog(this, "联系人添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadAllContacts();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "联系人添加失败，请检查输入格式！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 更新联系人
     */
    private void updateContact() {
        if (selectedContactId == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要更新的联系人！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        boolean isBlacklisted = blacklistedCheckBox.isSelected();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "姓名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "电话号码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println("isBlacklisted from GUI: " + isBlacklisted);

        boolean success = service.updateContact(selectedContactId, name, phone, 
                                              email.isEmpty() ? null : email, 
                                              address.isEmpty() ? null : address,
                                              isBlacklisted);
        if (success) {
            JOptionPane.showMessageDialog(this, "联系人更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadAllContacts();
            clearFields();
            selectedContactId = -1;
        } else {
            JOptionPane.showMessageDialog(this, "联系人更新失败，请检查输入格式！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除联系人
     */
    private void deleteContact() {
        if (selectedContactId == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的联系人！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, 
                                                 "确定要删除这个联系人吗？", 
                                                 "确认删除", 
                                                 JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            boolean success = service.deleteContact(selectedContactId);
            if (success) {
                JOptionPane.showMessageDialog(this, "联系人删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadAllContacts();
                clearFields();
                selectedContactId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "联系人删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 清空输入字段
     */
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        blacklistedCheckBox.setSelected(false);
        selectedContactId = -1;
        contactTable.clearSelection();
    }

    /**
     * 加载所有联系人到表格
     */
    private void loadAllContacts() {
        List<Contact> contacts = service.getAllContacts();
        updateTable(contacts);
    }

    /**
     * 搜索联系人
     */
    private void searchContacts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入搜索关键字", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Contact> results = service.searchContacts(keyword);
        updateTable(results);
    }

    /**
     * 更新表格数据
     */
    private void updateTable(List<Contact> contacts) {
        tableModel.setRowCount(0); // 清空表格内容
        for (Contact contact : contacts) {
            Object[] row = {
                contact.getId(),        // ID
                contact.getName(),      // 姓名
                contact.getPhone(),     // 电话
                contact.getEmail() != null ? contact.getEmail() : "", // 邮箱
                contact.getAddress() != null ? contact.getAddress() : "", // 地址
                contact.isBlacklisted() ? "是" : "否" // 黑名单状态
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 创建并显示启动闪屏界面
     */
    private static void createAndShowSplashScreen() {
    	 // 创建启动页面
        JFrame splashFrame = new JFrame("Java 通讯录管理系统");
        splashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splashFrame.setSize(400, 400);
        splashFrame.setLayout(new BorderLayout());

        ImageIcon originalIcon = new ImageIcon(".\\src\\system\\进入界面.jpg");
        Image originalImage = originalIcon.getImage();
        int targetWidth = 450;
        int targetHeight = (int) (originalIcon.getIconHeight() * ((double) targetWidth / originalIcon.getIconWidth()));
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        JLabel splashLabel = new JLabel(new ImageIcon(scaledImage));
        splashFrame.add(splashLabel, BorderLayout.CENTER);
        splashFrame.pack();

        if (originalIcon.getIconWidth() == 0 || originalIcon.getIconHeight() == 0) {
            System.out.println("图片加载失败，请检查路径！");
        }

        splashFrame.setLayout(null);
        JButton enterButton = new JButton("通讯录");
        enterButton.setBounds(135, 635, 80, 24);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setOpaque(false);
        enterButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        enterButton.setForeground(Color.BLACK);

        enterButton.addActionListener(e -> {
            splashFrame.dispose();
            showFunctionSelection(); // 改为显示功能选择界面
        });

        splashFrame.add(enterButton);
        splashFrame.add(splashLabel);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);
    }
	/**
	 * 显示功能选择界面
	 */
	private static void showFunctionSelection() {

		JFrame selectionFrame = new JFrame("功能选择");
		selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selectionFrame.setSize(400, 300);
		selectionFrame.setLayout(new GridLayout(4, 1, 10, 10));
		selectionFrame.getContentPane().setBackground(new Color(0, 0, 139));

		// 创建四个功能按钮
		JButton addButton = createFunctionButton("添加联系人", new Color(0, 100, 0));      // 深绿色
		JButton deleteButton = createFunctionButton("删除联系人", new Color(139, 0, 0));    // 深红色
		JButton updateButton = createFunctionButton("修改联系人", new Color(0, 0, 139));    // 深蓝色
		JButton queryButton = createFunctionButton("查询联系人", new Color(153, 76, 0));     // 深橙色/棕色
		JButton listAllButton = createFunctionButton("列出所有联系人", new Color(153, 76, 0)); // 深橙色/棕色
		JButton exitButton = createFunctionButton("退出系统", new Color(153, 76, 0));          // 深橙色/棕色

		// 添加按钮点击事件
		addButton.addActionListener(e -> {
			selectionFrame.dispose();
			ContactManagerGUI gui = new ContactManagerGUI();
			gui.showAddContactOnly(); // 只显示添加联系人功能
			gui.setVisible(true);
		});

		deleteButton.addActionListener(e -> {
			selectionFrame.dispose();
			ContactManagerGUI gui = new ContactManagerGUI();
			gui.showDeleteContactOnly(); // 只显示删除联系人功能
			gui.setVisible(true);
		});

		updateButton.addActionListener(e -> {
			selectionFrame.dispose();
			ContactManagerGUI gui = new ContactManagerGUI();
			gui.showUpdateContactOnly(); // 只显示修改联系人功能
			gui.setVisible(true);
		});

		queryButton.addActionListener(e -> {
			selectionFrame.dispose();
			ContactManagerGUI gui = new ContactManagerGUI();
			gui.showQueryContactOnly(); // 只显示查询联系人功能
			gui.setVisible(true);
		});

		listAllButton.addActionListener(e -> {
		    selectionFrame.dispose();
		    ContactManagerGUI gui = new ContactManagerGUI();
		    gui.showListAllContactsOnly(); // 显示所有联系人功能
		    gui.setVisible(true);
		});

		exitButton.addActionListener(e -> {
		    System.exit(0); // 退出系统
		});
		
		// 添加按钮到界面
		selectionFrame.add(addButton);
		selectionFrame.add(deleteButton);
		selectionFrame.add(updateButton);
		selectionFrame.add(queryButton);
		selectionFrame.add(listAllButton);
		selectionFrame.add(exitButton);

		selectionFrame.setLocationRelativeTo(null);
		selectionFrame.setVisible(true);
	}
    // 辅助方法：创建统一风格的功能按钮
    private static JButton createFunctionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.DARK_GRAY);
        button.setFont(new Font("微软雅黑", Font.CENTER_BASELINE, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }
    

    /**
     * 限制界面只显示添加联系人功能
     */
    public void showAddContactOnly() {
        setFunctionalVisibility(true, false, false);
        setTitle("添加联系人");
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // 隐藏搜索和表格面板
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBorder() instanceof javax.swing.border.TitledBorder) {
                    javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) panel.getBorder();
                    if ("🔍 搜索联系人".equals(border.getTitle()) || "📋 联系人列表".equals(border.getTitle())) {
                        panel.setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * 限制界面只显示删除联系人功能
     */
    public void showDeleteContactOnly() {
        setFunctionalVisibility(false, false, true);
        setTitle("删除联系人");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 隐藏输入字段，只保留必要的显示功能
        nameField.setEditable(false);
        phoneField.setEditable(false);
        emailField.setEditable(false);
        addressField.setEditable(false);
        blacklistedCheckBox.setEnabled(false);
    }

    // 只显示修改联系人功能
    public void showUpdateContactOnly() {
        addButton.setVisible(false);
        updateButton.setVisible(true);
        deleteButton.setVisible(false);
        searchField.setVisible(true);
        contactTable.setVisible(true);
        setTitle("修改联系人");
        setSize(800, 500);
    }

    // 只显示查询联系人功能
    public void showQueryContactOnly() {
        addButton.setVisible(false);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);
        searchField.setVisible(true);
        contactTable.setVisible(true);
        setTitle("查询联系人");
        setSize(800, 500);
    }

    // 添加显示所有联系人的方法
    public void showListAllContactsOnly() {
        setFunctionalVisibility(false, false, false);
        setTitle("查看所有联系人");
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 隐藏输入字段面板
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getBorder() instanceof javax.swing.border.TitledBorder) {
                    javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder) panel.getBorder();
                    if ("✏️ 联系人信息".equals(border.getTitle())) {
                        panel.setVisible(false);
                    }
                }
            }
        }
    }
    /**
     * 统一控制功能组件的可见性
     * @param addVisible 添加按钮可见性
     * @param updateVisible 更新按钮可见性
     * @param deleteVisible 删除按钮可见性
     */
    private void setFunctionalVisibility(boolean addVisible, boolean updateVisible, boolean deleteVisible) {
        addButton.setVisible(addVisible);
        updateButton.setVisible(updateVisible);
        deleteButton.setVisible(deleteVisible);
        returnButton.setVisible(true); // 返回按钮始终可见
    }

    /**
     * 主程序入口
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // 创建启动页面
            createAndShowSplashScreen();
        });
    }
}