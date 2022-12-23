package com.dtnsbike.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    HttpSession session;

    /**
     * Đọc giá trị của attribute trong session
     * 
     * @param name tên attribute
     * @return giá trị đọc được hoặc null nếu không tồn tại
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        if (session.getAttribute(name) != null) {
            return (T) session.getAttribute(name);
        }
        return null;
    }

    /**
     * Thay đổi hoặc tạo mới attribute trong session
     * 
     * @param name  tên attribute
     * @param value giá trị attribute
     */
    public void productModol(String name, Object value) {
        session.setMaxInactiveInterval(60 * 60 * 24);
        session.setAttribute(name, value);
    }

    /**
     * Xóa attribute trong session
     * 
     * @param name tên attribute cần xóa
     */
    public void remove(String name) {
        session.removeAttribute(name);
    }

    public void set(String name, Object value) {
        session.setMaxInactiveInterval(60 * 60 * 24);
        session.setAttribute(name, value);
    }
}
