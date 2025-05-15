package com.shelfconnect.service.impl;

import com.shelfconnect.model.Image;
import com.shelfconnect.repo.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class ImageService {
    private ImageRepository imageRepository;
    private CloudinaryService cloudinaryService;

    @Autowired
    public ImageService(ImageRepository imageRepository, CloudinaryService cloudinaryService) {
        this.imageRepository = imageRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Image create(MultipartFile file, String folder) throws IOException {

//        upload/create image to cloudinary with public_id = filename and asset_folder = folder
        Map<?, ?> data = cloudinaryService.upload(file, null, folder);
//        save public_id,asset_id,url to Image and db
        Image image = Image.builder()
                .assetId(data.get("asset_id").toString())
                .publicId(data.get("public_id").toString())
                .url(data.get("secure_url").toString())
                .build();
        return imageRepository.save(image);
    }

    public Image update(Long id, MultipartFile file, String folder) throws IOException {
        Image image = imageRepository.findById(id).orElseThrow();
        Map<?, ?> data = cloudinaryService.upload(file, image.getPublicId(), folder);
        image.setAssetId(data.get("asset_id").toString());
        image.setPublicId(data.get("public_id").toString());
        image.setUrl(data.get("secure_url").toString());
        return imageRepository.save(image);
    }

    public void delete(Long id) throws IOException {
        Image image = imageRepository.findById(id).orElseThrow();
        cloudinaryService.delete(image.getPublicId());
        imageRepository.delete(image);
    }

    public Optional<Image> findById(Long imageId) {
        return imageRepository.findById(imageId);
    }
}
