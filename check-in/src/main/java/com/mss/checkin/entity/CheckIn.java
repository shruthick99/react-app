package com.mss.checkin.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CheckIn {

    private int id;
    private String checkinId;
    private String name;
    private String phoneNumber;
    private String email;
    private String filePath;
    private String status;
    private String createdDate;
    private String modifiedDate;
    private String modifiedBy;
    private String checkinType;
private String confirmationNumber;
    private MultipartFile document;

}
