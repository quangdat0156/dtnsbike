package com.dtnsbike.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class FieldErrorService {

    public List<String> getCodeFieldErrors(String field, BindingResult rs) {
        List<FieldError> list = rs.getFieldErrors(field);
        List<String> listCode = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listCode.add(list.get(i).getCode());
        }
        return listCode;
    }

    public Boolean checkCodeFieldErrors(String field, String code, BindingResult rs) {
        List<String> list = this.getCodeFieldErrors(field, rs);
        return list.contains(code);
    }
}
