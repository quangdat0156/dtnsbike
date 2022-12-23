package com.dtnsbike.service;

import java.util.Base64;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Autowired
    HttpServletRequest req;
    @Autowired
    HttpServletResponse resp;

    public Cookie getCookie(String name) {
        Cookie[] cookie = req.getCookies();
        Cookie xunNhi = null;
        if (cookie != null) {
            for (Cookie c : cookie) {
                if (c.getName().equalsIgnoreCase(name)) {
                    xunNhi = c;
                    break;
                }
            }
        }
        return xunNhi;
    }

    /*
     * Đọc giá trị của cookie từ request
     * 
     * @param name tên cookie cần đọc
     * 
     * @return chuỗi giá trị đọc được hoặc rỗng nếu không tồn tại
     */
    public String getValueCookie(String name) {
        Cookie xCookie = getCookie(name);
        String xunNhi = "";
        if (xCookie != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(xCookie.getValue());
            xunNhi = new String(decodedBytes);
        }
        return xunNhi;
    }

    /*
     * Tạo và gửi cookie về client
     * 
     * @param name tên cookie
     * 
     * @param value giá trị cookie
     * 
     * @param hours thời hạn (day)
     * 
     * @return đối tượng cookie đã tạo
     */
    public Cookie setCookie(String name, String value, int days) {
        value = Base64.getEncoder().encodeToString(value.getBytes());
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(86400 * days);
        cookie.setPath("/");
        resp.addCookie(cookie);
        return cookie;
    }

    /*
     * Xóa cookie khỏi client
     * 
     * @param name tên cookie cần xóa
     */
    public void remove(String name) {
        Cookie xCookie = getCookie(name);
        if (xCookie != null) {
            this.setCookie(xCookie.getName(), xCookie.getValue(), 0);
        }
    }
}
