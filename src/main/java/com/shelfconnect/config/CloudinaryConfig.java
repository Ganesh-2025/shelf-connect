package com.shelfconnect.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.api-key}")
    private String API_KEY;
    @Value("${cloudinary.api-secret}")
    private String API_SECRET;
    @Value("${cloudinary.cloud-name}")
    private String CLOUD_NAME;

    @Bean
    public Cloudinary cloudinary() throws Exception {
        Cloudinary cloudinary = new Cloudinary(Map.of(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET)
        );
//        Map<?,?> result = cloudinary
//                .uploader()
//                .upload(
//                        Path.of("C:/Users/91951/Pictures/Screenshots/cnn.png").toFile(),ObjectUtils.asMap(
//                "asset_folder","avatar",
//                        "public_id","avatar1",
//                        "overwrite","true"
//        ));
//        result.forEach((k,v)->{
//            System.out.println(k+" "+v);
//        });
        return cloudinary;
    }
}
