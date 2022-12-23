package com.dtnsbike.data;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class ExtensionImage {
    
    private List<String> list = Arrays.asList("jpg", "png", "jpeg", "webp", "gif");

    public List<String> getList() {
        return list;
    }

}
