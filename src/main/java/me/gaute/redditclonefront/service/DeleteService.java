package me.gaute.redditclonefront.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DeleteService {
    private String BASE_URL = "http://localhost:9003/delete";
    private RestTemplate restTemplate = new RestTemplate();

    public void deleteUserByUsername(String username){
        System.out.println("URL: " + BASE_URL+"/user/"+username);
        restTemplate.delete(BASE_URL+"/user/"+username);

    }

    public void deleteSubredditByTitle(String title){
        restTemplate.delete(BASE_URL+"/subreddit/"+title);
    }

    public void deletePostById(long id){
        restTemplate.delete(BASE_URL+"/post/"+id);
    }

}
