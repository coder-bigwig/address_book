package system;

import java.util.*;
import java.util.regex.*;

/**
 * ContactManager 类：系统入口，提供命令行界面与操作菜单
 */
public class ContactManager {
    private static Scanner scanner = new Scanner(System.in);
    private static ContactService service = new ContactService();
    
    public static void main(String[] args) {
        System.out.println("欢迎使用 Java 通讯录管理系统");
        while (true) {
            showMenu();
            int choice = getChoice();
            switch (choice) {
                case 1:
                    addContact();
                    break;
                case 2:
                    deleteContact();
                    break;
                case 3:
                    updateContact();
                    break;
                case 4:
                    queryContact();
                    break;
                case 5:
                    listContacts();
                    break;
                case 6:
                    searchContacts(); // Added search option
                    break;
                case 0:
                    System.out.println("退出系统，感谢使用！");
                    System.exit(0);
                    break;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
        }
    }
    
    /**
     * 显示操作菜单
     */
    private static void showMenu() {
        System.out.println("\n===== 通讯录管理系统菜单 =====");
        System.out.println("1. 添加联系人");
        System.out.println("2. 删除联系人");
        System.out.println("3. 修改联系人");
        System.out.println("4. 查询联系人 (按ID)");
        System.out.println("5. 列出所有联系人");
        System.out.println("6. 搜索联系人 (按姓名或电话)"); // Added search option
        System.out.println("0. 退出系统");
        System.out.println("================================");
        System.out.print("请选择操作：");
    }
    
    /**
     * 获取用户输入的菜单选项
     */
    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * 添加联系人操作
     */
    private static void addContact() {
        System.out.print("请输入姓名：");
        String name = scanner.nextLine();
        System.out.print("请输入电话号码：");
        String phone = scanner.nextLine();
        System.out.print("请输入邮箱：");
        String email = scanner.nextLine();
        System.out.print("请输入地址：");
        String address = scanner.nextLine();
        System.out.print("是否将该联系人加入黑名单？(是/否)：");
        String isBlacklistedInput = scanner.nextLine().trim().toLowerCase();

        boolean isBlacklisted = isBlacklistedInput.equals("是");

        boolean success = service.addContact(name, phone, email, address, isBlacklisted);
        if (success) {
            System.out.println("联系人添加成功！");
        } else {
            System.out.println("联系人添加失败，请检查输入！");
        }
    }
    
    /**
     * 删除联系人操作
     */
    private static void deleteContact() {
        System.out.print("请输入要删除的联系人编号：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            boolean success = service.deleteContact(id);
            if (success) {
                System.out.println("联系人删除成功！");
            } else {
                System.out.println("未找到该联系人或删除失败！");
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字编号！");
        }
    }
    
    /**
     * 修改联系人操作
     */
    private static void updateContact() {
        System.out.print("请输入要修改的联系人编号：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Contact existingContact = service.getContact(id);
            if (existingContact == null) {
                System.out.println("未找到编号为 " + id + " 的联系人！");
                return;
            }

            System.out.println("请输入新的信息（若不修改请输入空）：");
            System.out.print("姓名 (当前: " + existingContact.getName() + "): ");
            String name = scanner.nextLine();
            System.out.print("电话号码 (当前: " + existingContact.getPhone() + "): ");
            String phone = scanner.nextLine();
            System.out.print("邮箱 (当前: " + existingContact.getEmail() + "): ");
            String email = scanner.nextLine();
            System.out.print("地址 (当前: " + existingContact.getAddress() + "): ");
            String address = scanner.nextLine();
            System.out.print("是否将该联系人加入黑名单？(是/否)：");
            String isBlacklistedInput = scanner.nextLine().trim().toLowerCase();

            boolean isBlacklisted = isBlacklistedInput.equals("是");

            // Use existing values if input is empty
            name = name.trim().isEmpty() ? existingContact.getName() : name;
            phone = phone.trim().isEmpty() ? existingContact.getPhone() : phone;
            email = email.trim().isEmpty() ? existingContact.getEmail() : email;
            address = address.trim().isEmpty() ? existingContact.getAddress() : address;

            boolean success = service.updateContact(id, name, phone, email, address, isBlacklisted);
            if (success) {
                System.out.println("联系人信息更新成功！");
            } else {
                System.out.println("更新失败，请检查输入！");
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字编号！");
        }
    }
    
    /**
     * 根据编号查询联系人
     */
    private static void queryContact() {
        System.out.print("请输入联系人编号查询：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Contact c = service.getContact(id);
            if (c != null) {
                System.out.println("查询结果：");
                System.out.println(c);
            } else {
                System.out.println("未找到联系人！");
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字编号！");
        }
    }
    
    /**
     * 列出所有联系人
     */
    private static void listContacts() {
        List<Contact> list = service.getAllContacts();
        if (list.isEmpty()) {
            System.out.println("暂无联系人数据！");
        } else {
            System.out.println("所有联系人：");
            for (Contact c : list) {
                System.out.println(c);
            }
        }
    }

    /**
     * 搜索联系人操作
     */
    private static void searchContacts() {
        System.out.print("请输入搜索关键字 (姓名或电话)：");
        String keyword = scanner.nextLine();
        List<Contact> results = service.searchContacts(keyword);
        if (results.isEmpty()) {
            System.out.println("未找到匹配的联系人！");
        } else {
            System.out.println("搜索结果：");
            for (Contact c : results) {
                System.out.println(c);
            }
        }
    }
}