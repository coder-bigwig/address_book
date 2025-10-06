package system;

import java.util.*;
import java.util.regex.*;

/**
 * ContactService 类：封装联系人管理的业务逻辑，包含添加、删除、更新、查询操作
 */
class ContactService {
    private ContactDao dao;

    public ContactService() {
        dao = new ContactDao();
    }

    /**
     * 添加新联系人，包含输入校验
     */
    public boolean addContact(String name, String phone, String email, String address, boolean isBlacklisted) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("姓名不能为空！");
            return false;
        }

        if (!isValidPhone(phone)) {
            System.out.println("电话号码格式不正确！电话号码应为7-11位数字");
            return false;
        }

        if (!isValidEmail(email)) {
            System.out.println("邮箱格式不正确或不能只使用QQ邮箱！");
            return false;
        }

        Contact contact = new Contact(0, name, phone, 
                                       email != null && email.trim().isEmpty() ? null : email, 
                                       address != null && address.trim().isEmpty() ? null : address, isBlacklisted);
        contact.setBlacklisted(isBlacklisted); // 设置黑名单状态
        dao.addContact(contact);
        return true;
    }
    /**
     * 删除联系人
     */
    public boolean deleteContact(int id) {
        return dao.deleteContact(id);
    }

    /**
     * 更新联系人信息
     */
    public boolean updateContact(int id, String name, String phone, String email, String address, boolean isBlacklisted) {
        Contact contact = dao.getContact(id);
        if (contact == null) {
            System.out.println("未找到编号为 " + id + " 的联系人！");
            return false;
        }

        if (name != null && !name.trim().isEmpty()) {
            contact.setName(name);
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (isValidPhone(phone)) {
                contact.setPhone(phone);
            } else {
                System.out.println("电话号码格式不正确！电话号码应为7-11位数字");
                return false;
            }
        }

        if (email != null) {
            if (email.trim().isEmpty()) {
                contact.setEmail(null);
            } else if (isValidEmail(email)) {
                contact.setEmail(email);
            } else {
                System.out.println("邮箱格式不正确或不能只使用QQ邮箱！");
                return false;
            }
        }

        if (address != null) {
            contact.setAddress(address.trim().isEmpty() ? null : address);
        }

        contact.setBlacklisted(isBlacklisted); // 更新黑名单状态
        return dao.updateContact(contact);
    }

    /**
     * 根据编号查询联系人
     */
    public Contact getContact(int id) {
        return dao.getContact(id);
    }

    /**
     * 查询所有联系人
     */
    public List<Contact> getAllContacts() {
        List<Contact> contacts = dao.getAllContacts();
        // 按姓名字母排序
        Collections.sort(contacts, Comparator.comparing(Contact::getName));
        return contacts;
    }

    /**
     * 模糊查询联系人，根据姓名或电话包含关键字进行查询
     */
    public List<Contact> searchContacts(String keyword) {
        List<Contact> results = dao.searchContacts(keyword);
        // 按姓名字母排序
        Collections.sort(results, Comparator.comparing(Contact::getName));
        return results;
    }

    /**
     * 电话号码校验：7-11位数字
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return phone.matches("\\d{7,11}");
    }

    /**
     * 邮箱校验：可以为空，如果不为空则需要符合邮箱格式且不能只是QQ邮箱
     */
    private boolean isValidEmail(String email) {
        // 邮箱可以为空
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        
        // 基本邮箱格式验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            return false;
        }
        
        // 不能只是QQ邮箱（如果只有QQ邮箱则不通过）
        // 这里假设用户至少需要提供一个非QQ的邮箱
        if (email.toLowerCase().endsWith("@qq.com") || email.toLowerCase().endsWith("@qq.cn")) {
            System.out.println("不能只使用QQ邮箱，请提供其他邮箱地址！");
            return false;
        }
        
        return true;
    }

	public Contact findContactByNameAndPhone(String name, String phone) {
		List<Contact> contacts = getAllContacts();
		for (Contact contact : contacts) {
			if (contact.getName().equals(name) && contact.getPhone().equals(phone)) {
				return contact;
			}
		}
		return null;
	}
	public boolean addToBlacklist(int id) {
	    Contact contact = dao.getContact(id);
	    if (contact == null) {
	        System.out.println("未找到编号为 " + id + " 的联系人！");
	        return false;
	    }
	    contact.setBlacklisted(true);
	    return dao.updateContact(contact);
	}
}