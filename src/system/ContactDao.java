package system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ContactDao 类：用于联系人数据的存储与读取 (MySQL 数据库实现)
 */
class ContactDao {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ContactManager";
    private static final String USER = "root"; // 替换为你的数据库用户名
    private static final String PASS = "123456"; // 替换为你的数据库密码
	private String affectedRows;

    public ContactDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            createTable();
            System.out.println("数据库连接成功！");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC 驱动未找到: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS contacts (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "name VARCHAR(100) NOT NULL," +
                     "phone VARCHAR(15) NOT NULL," +
                     "email VARCHAR(100)," +
                     "address VARCHAR(255)" +
                     ")";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * 添加联系人
     * @param contact 要添加的联系人对象
     */
    public void addContact(Contact contact) {
		String sql = "INSERT INTO contacts(name, phone, email, address, is_blacklisted) VALUES(?,?,?,?,?)";
		try (Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, contact.getName());
			pstmt.setString(2, contact.getPhone());
			pstmt.setString(3, contact.getEmail());
			pstmt.setString(4, contact.getAddress());
			pstmt.setBoolean(5, contact.isBlacklisted());
			pstmt.executeUpdate();

            // 获取自增ID并设置给Contact对象
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                contact.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("添加联系人失败: " + e.getMessage());
        }
    }

    /**
     * 删除联系人，根据联系人编号删除
     * @param id 联系人编号
     * @return true 删除成功，false 未找到联系人
     */
    public boolean deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("删除联系人失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新联系人信息，根据联系人编号更新对应信息
     * @param updated 联系人对象，编号用于匹配
     * @return true 更新成功，false 未找到联系人
     */
    public boolean updateContact(Contact updated) {
    	String sql = "UPDATE contacts SET name = ?, phone = ?, email = ?, address = ?, is_blacklisted = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updated.getName());
            pstmt.setString(2, updated.getPhone());
            pstmt.setString(3, updated.getEmail());
            pstmt.setString(4, updated.getAddress());
            pstmt.setInt(6, updated.getId());
            System.out.println("isBlacklisted: " + updated.isBlacklisted());
            pstmt.setBoolean(5, updated.isBlacklisted());
            System.out.println("Affected rows: " + affectedRows);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("更新联系人失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据联系人编号查询联系人信息
     * @param id 联系人编号
     * @return 联系人对象，若未找到返回 null
     */
    public Contact getContact(int id) {
        String sql = "SELECT id, name, phone, email, address, is_blacklisted FROM contacts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Contact(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"), // 在这里添加逗号
                    rs.getBoolean("is_blacklisted")
                );
            }
        } catch (SQLException e) {
            System.err.println("查询联系人失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取所有联系人数据
     * @return 联系人列表
     */
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, address, is_blacklisted FROM contacts";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contacts.add(new Contact(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getBoolean("is_blacklisted") // 添加对 is_blacklisted 的处理
                ));
            }
        } catch (SQLException e) {
            System.err.println("获取所有联系人失败: " + e.getMessage());
        }
        return contacts;
    }

    /**
     * 模糊查询联系人，根据姓名或电话包含关键字进行查询
     */
    public List<Contact> searchContacts(String keyword) {
        List<Contact> results = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, address, is_blacklisted FROM contacts WHERE name LIKE ? OR phone LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Contact(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getBoolean("is_blacklisted") // 添加对 is_blacklisted 的处理
                ));
            }
        } catch (SQLException e) {
            System.err.println("模糊查询联系人失败: " + e.getMessage());
        }
        return results;
    }

    // 在ContactDao中不再需要saveContacts和loadContacts方法，因为数据直接操作数据库
}