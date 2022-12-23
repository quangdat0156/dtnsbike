package com.dtnsbike.config;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dtnsbike.service.SessionService;

@ControllerAdvice
public class FileMaxSizeConfig {

	@Autowired
	SessionService session;

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String uploads(Model m, RedirectAttributes ra, HttpServletRequest request) {

		ra.addAttribute("error", "File size exceeds the allowed limit");

		String uri = session.get("editURI");
		if (session.get("editUserID") != null) {
			return "redirect:/admin/account/form/edit/" + session.get("editUserID");
		}

		return "redirect:" + uri;
	}
}
