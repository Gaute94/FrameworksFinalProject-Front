package me.gaute.redditclonefront.service;


import me.gaute.redditclonefront.model.Image;
import me.gaute.redditclonefront.model.Post;
import me.gaute.redditclonefront.model.Subreddit;
import me.gaute.redditclonefront.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageService{

    private String BASE_URL = "http://localhost:9003/images";
    private RestTemplate restTemplate = new RestTemplate();

    public List<Image> getAllImages(){
        return  Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL, Image[].class))
        ).collect(Collectors.toList());
    }

    public Optional<Image> getImageById(long id){
        try {
            Image image = restTemplate.getForObject(BASE_URL+"/id/"+id, Image.class);
            return Optional.ofNullable(image);
        } catch(Exception e){
            return Optional.empty();
        }
    }


    public List<Image> getImageByOwner(String owner){
        String image = owner.toLowerCase();
        return getAllImages().stream().filter(b -> b.getOwner().getUName().toLowerCase()
                .contains(image))
                .collect(Collectors.toList());
    }

    public Image saveImage(Image newImage){
        return restTemplate.postForObject(BASE_URL, newImage, Image.class);
    }

    public void updateImage(long id, Image updatedImage){
        restTemplate.put(BASE_URL+"/"+id, updatedImage);
    }


}
