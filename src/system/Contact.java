package system;

import java.util.*;
import java.io.*;
import java.util.regex.*;
 
/**
 * Contact 类：表示单个联系人信息
 */
class Contact implements Serializable {
    // 为了持久化，添加 serialVersionUID
    private static final long serialVersionUID = 1L;
    
    // 联系人唯一编号
    private int id;
    // 姓名
    private String name;
    // 电话号码
    private String phone;
    // 邮箱地址
    private String email;
    // 住址
    private String address;
    //黑名单
    private boolean isBlacklisted;
    
    /**
     * 构造方法，初始化联系人对象
     * @param id 联系人编号
     * @param name 姓名
     * @param phone 电话号码
     * @param email 邮箱地址
     * @param address 住址
     */
    public Contact(int id, String name, String phone, String email, String address,boolean isBlacklisted) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.isBlacklisted = isBlacklisted;
    }
    
    // Getter 方法
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public boolean isBlacklisted() { return isBlacklisted;}
    
    // Setter 方法
    public void setId(int id) { this.id = id; } // Added setId method
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setBlacklisted(boolean blacklisted) { isBlacklisted  = blacklisted; }
    
    @Override
    public String toString() {
        return "编号: " + id + ", 姓名: " + name + ", 电话: " + phone + ", 邮箱: " + email + ", 地址: " + address;
    }
}
