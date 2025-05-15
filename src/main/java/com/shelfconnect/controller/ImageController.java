package com.shelfconnect.controller;

import com.shelfconnect.Exception.APIException;
import com.shelfconnect.dto.api.APIResponse;
import com.shelfconnect.dto.api.Status;
import com.shelfconnect.model.Image;
import com.shelfconnect.service.impl.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController()
@RequestMapping("/api/image")
public class ImageController {
    private enum FOLDERS {book,user};
    private final ImageService imageService;
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/upload",consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<APIResponse> uploadImage(
            @RequestParam("folder") FOLDERS folder,
            MultipartFile imageFile
    ){
        try {
            Image image = imageService.create(imageFile, folder.toString());
            return ResponseEntity.ok(
                    APIResponse.builder()
                            .message("image uploaded")
                            .statusCode(HttpStatus.OK)
                            .status(Status.SUCCESS)
                            .data(Map.of("imageId",image.getId()))
                            .build()
            );
        }catch (IOException e){
            e.printStackTrace();
            throw new APIException("error occured while uploading image", HttpStatus.INTERNAL_SERVER_ERROR,null);
        }
    }

}
