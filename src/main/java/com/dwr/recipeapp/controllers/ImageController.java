package com.dwr.recipeapp.controllers;


import com.dwr.recipeapp.commands.RecipeCommand;
import com.dwr.recipeapp.services.ImageService;
import com.dwr.recipeapp.services.RecipeService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class ImageController {
    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));

        return "recipe/imageuploadform";
    }

    @PostMapping("recipe/{id}/image")
    public String handleImagePost(@PathVariable String id, @RequestParam("imagefile") MultipartFile file) { //ReqestParam will post up by post
        imageService.saveImageFile(Long.valueOf(id), file);

        return "redirect:/recipe/" + "show/" + id;
    }

    @GetMapping("recipe/{id}/recipeimage")
    public void renderImageFromDB(@PathVariable String id, HttpServletResponse response) throws IOException{
        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(id));

        if(recipeCommand.getImage() != null){
            //in db is Byte[] and we need to convert it into byte[]
            byte[] byteArray = new byte[recipeCommand.getImage().length];
            int i = 0;
            //converting boxed byte image to primitive type
            for (Byte wrappedByte : recipeCommand.getImage()){
                byteArray[i++] = wrappedByte; //autoboxing
            }
            response.setContentType("image/jpeg");
            InputStream inputStream = new ByteArrayInputStream(byteArray);
            //coping from the byte array input stream to response output stream
            IOUtils.copy(inputStream,response.getOutputStream());
        }
    }

}