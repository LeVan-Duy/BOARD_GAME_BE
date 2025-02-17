package org.example.board_game.utils;//package com.example.scent.utils;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//@Log4j2
//@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
//public class CloudinaryUpload {
//
//
//    Cloudinary cloudinary;
//
//    public String upload(String imageBase64){
//        String imageCloudinary = "";
//        if (imageBase64 == null || imageBase64.isEmpty() || !isBase64Image(imageBase64)) {
//            return imageBase64;
//        }
//        try {
//            Map result = cloudinary.uploader().upload(imageBase64, ObjectUtils.asMap("phash", true));
//            imageCloudinary = (String) result.get("secure_url");
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        }
//        return imageCloudinary;
//    }
//
//    boolean isBase64Image(String urlImage) {
//        return urlImage != null && urlImage.startsWith("data:image/");
//    }
//
//}
