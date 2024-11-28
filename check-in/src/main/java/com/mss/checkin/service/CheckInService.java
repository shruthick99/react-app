package com.mss.checkin.service;

import com.mss.checkin.entity.CheckIn;
import com.mss.checkin.entity.Status;
import com.mss.checkin.util.MailManager;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class CheckInService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    MailManager mailManager;


public Map<String,Object> addCheckIn(CheckIn checkIn){
    Map<String, Object> response = new HashMap<String, Object>();
    response.put("success", false);
    response.put("message", "Invalid inputs!");
// System.out.println("checkinId--"+checkIn.getCheckinId());
     //int id
    String checkinId = checkIn.getCheckinId();
     String name = checkIn.getName();
     String phoneNumber = checkIn.getPhoneNumber();
     String email = checkIn.getEmail();
     String filePath = checkIn.getFilePath();
     String checkinType = checkIn.getCheckinType();


    MultipartFile document = checkIn.getDocument();
    // System.out.println("Document--"+document.getOriginalFilename());
    // System.out.println("Document size--"+document.getContentType());

     if(checkinId == null || checkinId.isEmpty()){
         response.put("message", "Checkin Id mandatory");
         return response;
     }else  if(name == null || name.isEmpty()){
         response.put("message", "Name is manaatory");
         return response;
     }else  if(phoneNumber == null || phoneNumber.isEmpty()){
         response.put("message", "Phone Number is mandatory");
         return response;
     }else  if(email == null || email.isEmpty()){
         response.put("message", "Email is mandatory");
         return response;
     }else  if(checkinType == null || checkinType.isEmpty()){
         response.put("message", "Checkin Type is mandatory");
         return response;
     }


    String checkInQuery = "SELECT Id FROM tblLKPCheckin WHERE `CheckinId`=?";
    List<Map<String, Object>> checkinList = jdbcTemplate.queryForList(checkInQuery, checkinId);

    if(checkinList.isEmpty()){
        response.put("message", "Invalid check in id");
        return response;
    }

    String fileName = document.getOriginalFilename();

// String confirmationNumber="";
    UUID uuid = UUID.randomUUID();
    String confirmationNumber = "CONF-"+uuid.toString();

if(document !=null && document.getOriginalFilename() != null){
    String destFolderPath = "D:\\checkin-files";
    File destFile = new File(destFolderPath);
if(!destFile.exists()){
    destFile.mkdirs();
}
try {

    File convFile = new File(destFolderPath+"/"+document.getOriginalFilename());
    document.transferTo(convFile);


    //FileUtils.copyFile(document.getResource().getFile(), destFile);
}catch(Exception e){
    e.printStackTrace();
}

}

    String insertQuery="INSERT INTO `tblCheckIn` (Name,PhoneNumber,Email,CheckInId,FilePath,CheckInType,ConfirmationNumber) VALUES(:Name,:PhoneNumber,:Email,:CheckInId,:FilePath,:CheckInType,:ConfirmationNumber)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
            .addValue("Name", name)
            .addValue("PhoneNumber", phoneNumber)
            .addValue("Email", email)
            .addValue("CheckInId", checkinId)
            .addValue("FilePath", fileName)
            .addValue("CheckInType", checkinType)
            .addValue("ConfirmationNumber", confirmationNumber);


    int result1 = namedParameterJdbcTemplate.update(insertQuery, sqlParameterSource, keyHolder);
    int autoCheckInId = keyHolder.getKey().intValue();



if(autoCheckInId>0){
    response.put("success", true);
    response.put("confNumber", confirmationNumber);
    response.put("message", "Successfully submitted check in form");
    String to = email;
    String  subject="Check-In Confirmation";
    String body="Thank you for submitting check in form. Here is your confirmation number "+confirmationNumber;

    mailManager.sendEmail(to,subject,body);
}


    return response;



}


    public Map<String,Object> getCheckInList() {
        Map<String,Object> response = new HashMap<>();

        List<CheckIn> checkInList = new ArrayList<>();
        List<Map<String, Object>> dataList = null;
        CheckIn checkIn = new CheckIn();


        int waitingCount=0;
        int inboundCount = 0;
        int outboundCount = 0;
        int totalCount = 0;
      //  String status ="";
        response.put("success", false);
        response.put("message", "Invalid inputs!");
        response.put("data", checkInList);
        {
            //DATE_FORMAT(CreatedDate,'%m/%d/%Y %H:%i:%S') As CreatedDate
            //DATE_FORMAT(ModifiedDate,'%m/%d/%Y %H:%i:%S') As ModifiedDate
            try {
                String dataQuery = "SELECT `Id`,`Name`,`PhoneNumber`,`Email`,`CheckInId`,`FilePath`,DATE_FORMAT(CreatedDate,'%m/%d/%Y %H:%i:%S') As CreatedDate,`Status`,`ModifiedBy`,DATE_FORMAT(ModifiedDate,'%m/%d/%Y %H:%i:%S') As ModifiedDate,`CheckInType`,ConfirmationNumber FROM `tblCheckIn` ORDER BY `Id` DESC";
                dataList = jdbcTemplate.queryForList(dataQuery);

                for (Map row : dataList) {
                    checkIn = new CheckIn();
                    checkIn.setId((Integer)row.get("Id"));
                    checkIn.setName((String)row.get("Name"));
                    checkIn.setPhoneNumber((String)row.get("PhoneNumber"));
                    checkIn.setEmail((String)row.get("Email"));
                    checkIn.setCheckinId((String)row.get("CheckInId"));
                    checkIn.setFilePath((String)row.get("FilePath"));
                    checkIn.setStatus((String)row.get("Status"));
                    checkIn.setModifiedBy((String)row.get("ModifiedBy"));
                    checkIn.setModifiedDate((String)row.get("ModifiedDate"));
                    checkIn.setCheckinType((String)row.get("CheckInType"));
                    checkIn.setCreatedDate((String)row.get("CreatedDate"));
                    checkIn.setConfirmationNumber((String)row.get("ConfirmationNumber"));
//checkIn

                    String status = (String)row.get("Status");

                    if("P".equals(status)) {
                        waitingCount++;
                    }
                    String checkInType = (String)row.get("CheckInType");
                    if("I".equals(checkInType)){
                        inboundCount++;
                    } else if("O".equals(checkInType)){
                        outboundCount++;
                    }



                    checkInList.add(checkIn);

                }

                response.put("success", true);
                response.put("message", "Successfully retrieved the checkin list");
                response.put("data", checkInList);
                response.put("waitingCount", waitingCount);
                response.put("inboundCount", inboundCount);
                response.put("outboundCount", outboundCount);
                response.put("totalCount", checkInList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }



    public Map<String,Object> updateStatus(Status checkInStatus, int id) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        response.put("message", "Invalid inputs!");
        String updateStatusQuery="UPDATE `tblCheckIn` SET `Status`=:STATUS,`ModifiedBy`=:ModifiedBy,`ModifiedDate`=NOW() WHERE `Id`=:Id";

        String status = checkInStatus.getCheckInStatus();
        String userId = checkInStatus.getUserId();
if(id<=0){
    response.put("message", "Invalid inputs!");
    return response;
}else if(status==null || status.isEmpty() ){
    response.put("message", "Status mandatory");
    return response;
}else if(userId==null || userId.isEmpty() ){
    response.put("message", "User Id mandatory");
    return response;
}




        MapSqlParameterSource updateSqlParameterSource = new MapSqlParameterSource()

                .addValue("STATUS", status).addValue("ModifiedBy", userId)

                .addValue("Id", id);

        int result1 = namedParameterJdbcTemplate.update(updateStatusQuery, updateSqlParameterSource);
        if(result1>0){
            response.put("success", true);
            response.put("message", "Successfully updated the status");

            String checkInQuery = "SELECT `Email`,ConfirmationNumber FROM `tblCheckIn` WHERE Id=?";
            List<Map<String, Object>> checkinList = jdbcTemplate.queryForList(checkInQuery, id);

            for (Map row : checkinList) {
                String email  = (String)row.get("Email");
                String confirmationNumber = (String)row.get("ConfirmationNumber");
                String to =email;
                String subject ="Check In completed";
                String body="Your check in is completed now. Here is your confirmation number "+confirmationNumber;

                mailManager.sendEmail(to,subject,body);
            }





        }


        return response;
    }




    public Map<String,Object> getCheckInByConfNumber(String confNumber) {
        Map<String,Object> response = new HashMap<>();

        List<CheckIn> checkInList = new ArrayList<>();
        List<Map<String, Object>> dataList = null;
        CheckIn checkIn = new CheckIn();


        int waitingCount=0;
        int inboundCount = 0;
        int outboundCount = 0;
        int totalCount = 0;
        //  String status ="";
        response.put("success", false);
        response.put("message", "Invalid inputs!");
        response.put("data", checkIn);
        {
            //DATE_FORMAT(CreatedDate,'%m/%d/%Y %H:%i:%S') As CreatedDate
            //DATE_FORMAT(ModifiedDate,'%m/%d/%Y %H:%i:%S') As ModifiedDate
            try {


if(confNumber == null || confNumber.isEmpty()){
    response.put("message", "Confirmation number is mandatory");
    return response;
}
                String dataQuery = "SELECT `Id`,`Name`,`PhoneNumber`,`Email`,`CheckInId`,`FilePath`,DATE_FORMAT(CreatedDate,'%m/%d/%Y %H:%i:%S') As CreatedDate," +
                        "`Status`,`ModifiedBy`,DATE_FORMAT(ModifiedDate,'%m/%d/%Y %H:%i:%S') As ModifiedDate,`CheckInType`,ConfirmationNumber FROM `tblCheckIn` WHERE ConfirmationNumber=?" ;

                dataList = jdbcTemplate.queryForList(dataQuery,confNumber);
if(dataList.isEmpty()){
    response.put("message", "No check in records found");
    return response;
}
                for (Map row : dataList) {
                    checkIn = new CheckIn();
                    checkIn.setId((Integer)row.get("Id"));
                    checkIn.setName((String)row.get("Name"));
                    checkIn.setPhoneNumber((String)row.get("PhoneNumber"));
                    checkIn.setEmail((String)row.get("Email"));
                    checkIn.setCheckinId((String)row.get("CheckInId"));
                    checkIn.setFilePath((String)row.get("FilePath"));
                    checkIn.setStatus((String)row.get("Status"));
                    checkIn.setModifiedBy((String)row.get("ModifiedBy"));
                    checkIn.setModifiedDate((String)row.get("ModifiedDate"));
                    checkIn.setCheckinType((String)row.get("CheckInType"));
                    checkIn.setCreatedDate((String)row.get("CreatedDate"));
                    checkIn.setConfirmationNumber((String)row.get("ConfirmationNumber"));

                    response.put("success", true);
                    response.put("message", "Successfully retrieved the checkin ");
                    response.put("data", checkIn);
                }






            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }


    public Resource getFileAsResource(int id)  {
try {
    String dataQuery = "SELECT `FilePath` FROM `tblCheckIn` WHERE Id=?";
    List<Map<String, Object>> dataList = null;
    dataList = jdbcTemplate.queryForList(dataQuery, id);
    String fileName = "";
    for (Map row : dataList) {
        fileName = (String) row.get("FilePath");
    }

    File file = new File("D:\\checkin-files\\" + fileName);


    if (file != null) {
        return new UrlResource(file.toURI());
    }

}catch(Exception e){
    e.printStackTrace();
}
        return null;
    }


    

}
