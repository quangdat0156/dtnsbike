package com.dtnsbike.controller.users;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dtnsbike.entity.Accounts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class ErrorController {

    String path = "users/common/";
    
	@RequestMapping("/pagenotfound.html")
    public String errorPagenotfound() {
        return path+"404_error";
    }
	
	@RequestMapping("/badrequest.html")
    public String errorBadrequest() {
        return path+"400_error";
    }

	@RequestMapping("/internalservererror.html")
    public String errorInternalServerError() {
        return path+"500_error";
    }
	
	@RequestMapping("/requesttimeout.html")
    public String errorRequestTimeOut() {
        return path+"408_error";
    }
	
	@RequestMapping("/methodnotallowed.html")
    public String errorMethodNotAllowed() {
        return path+"405_error";
    }
}
