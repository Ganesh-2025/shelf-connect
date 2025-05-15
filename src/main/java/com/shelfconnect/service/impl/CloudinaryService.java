package com.shelfconnect.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<?, ?> upload(
            @NotNull MultipartFile file,
            String fileName,
            @NotNull String folder
    ) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "override", true
        );
        if (fileName != null)
            options.put("public_id", fileName);
        return cloudinary
                .uploader()
                .upload(toFile(file), options);
    }

    public Map<?, ?> delete(String public_id) throws IOException {
        return cloudinary
                .uploader()
                .destroy(public_id, ObjectUtils.asMap("invalidate", true));
    }

    public File toFile(MultipartFile file) throws IOException {

        File temp = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(temp, false);
        fos.write(file.getBytes());
        fos.close();
        return temp;
    }
}
