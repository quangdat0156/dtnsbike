package com.dtnsbike.model;

import org.springframework.web.multipart.MultipartFile;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImgAccount {
    
    private MultipartFile file;
}
