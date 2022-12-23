package com.dtnsbike.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConvertPageService {

    public Page<?> toPage(List<?> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        if (start > list.size())
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public List<Integer> listPage(List<?> list, Integer pageNumber, Integer elementNumber) {
        List<Integer> listPage = new ArrayList<>();
        Pageable pageable = PageRequest.of(1, elementNumber);
        Page<?> page = this.toPage(list, pageable);
        if (page.getTotalPages() >= 3) {
            list = new ArrayList<>();
            if (pageNumber == page.getTotalPages()) {
                list = new ArrayList<>();
                listPage.add(0, pageNumber - 2);
                listPage.add(1, pageNumber - 1);
                listPage.add(2, pageNumber - 0);
            } else {
                int x = pageNumber;
                if ((x - 1) <= 1) {
                    list = new ArrayList<>();
                    listPage.add(0, 1);
                    listPage.add(1, 2);
                    listPage.add(2, 3);
                } else {
                    list = new ArrayList<>();
                    listPage.add(0, pageNumber - 1);
                    listPage.add(1, pageNumber);
                    listPage.add(2, pageNumber + 1);
                }
            }
        } else {
            list = new ArrayList<>();
            for (int i = 0; i < page.getTotalPages(); i++) {
                listPage.add(i + 1);
            }
        }
        return listPage;
    }

    public Boolean checkTotalPages(List<?> list, Integer pageNumber, Integer elementNumber) {
        Pageable pageable = PageRequest.of(0, elementNumber);
        Page<?> page = this.toPage(list, pageable);
        Boolean check = true;
        if (pageNumber-1 >= page.getTotalPages()) {
            check = false;
        }
        
        return check;
    }
}
