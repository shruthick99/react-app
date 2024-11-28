package com.mss.checkin.controller;

import com.mss.checkin.entity.CheckIn;
import com.mss.checkin.entity.Status;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.mss.checkin.service.CheckInService;



import java.util.Map;
@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
//@SecurityRequirement(name = "bearerAuth")
@Tag(name = "CheckIn", description = "Endpoints for managing checkin's")
public class CheckInController {

    @Autowired
    CheckInService checkInService;




 //   @Autowired
   // private UserInfoService service;



    @Operation(
            summary = "To submit check in form",
          //  security = @SecurityRequirement(name = "bearerAuth"),
            description = "It will stores the checkin form data",
            tags = { "CheckIn" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }

    )


    @PostMapping(value = "/check-in-form", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    //public Map<String,Object> addCheckInForm(@RequestBody CheckIn checkIn) {
    public Map<String,Object> addCheckInForm(@ModelAttribute CheckIn checkIn) {
        log.info("In check in api ");
        return checkInService.addCheckIn(checkIn);
    }



    @Operation(
            summary = "To get list of check in records",
            security = @SecurityRequirement(name = "bearerAuth"),
            description = "Here you will see all the check in records",
            tags = { "CheckIn" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }

    )

    @GetMapping("/check-in")
    public Map<String,Object> getCheckIns() {
        return checkInService.getCheckInList();
    }
    @CrossOrigin
    @Operation(
            summary = "To update check in status",
           // security = @SecurityRequirement(name = "bearerAuth"),
            description = "To update check in status by id",
            tags = { "CheckIn" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }

    )

    @PutMapping("/check-in/{id}")
    public Map<String,Object> updateStatus(@RequestBody Status checkInStatus, @PathVariable("id") int id ) {
        return checkInService.updateStatus(checkInStatus,id);
    }



    @Operation(
            summary = "To get check in details",
            security = @SecurityRequirement(name = "bearerAuth"),
            description = "Here you will get all details of individual check in record by confirmation number",
            tags = { "CheckIn" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }

    )

    @GetMapping("/check-in/{conf-number}")
    public Map<String,Object> getCheckInByConfNumber( @PathVariable("conf-number") String confNumber ) {
        return checkInService.getCheckInByConfNumber(confNumber);
    }

    @Operation(
            summary = "To download check in attachment",
            security = @SecurityRequirement(name = "bearerAuth"),
            description = "Here you can download the attachment by passing check in id",
            tags = { "CheckIn" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }

    )

    @GetMapping("/download-file/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable("id") int fileCode) {





        Resource resource = null;

            resource = checkInService.getFileAsResource(fileCode);


        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }



}
