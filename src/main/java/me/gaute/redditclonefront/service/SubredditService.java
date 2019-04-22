package me.gaute.redditclonefront.service;

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
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubredditService{

    String BASE_URL = "http://localhost:9003/subreddits";
    private RestTemplate restTemplate = new RestTemplate();

    public List<Subreddit> getAllSubreddits(){
        return  Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL, Subreddit[].class))
        ).collect(Collectors.toList());
    }
    /*
    public Optional<Subreddit> getSubredditByTitle (String title){
        try {
            Subreddit subreddit = restTemplate.getForObject(BASE_URL+"/title/"+title, Subreddit.class);
            return Optional.ofNullable(subreddit);
        } catch(Exception e){
            return Optional.empty();
        }
    }*/

    public Subreddit getSubredditByTitle(String title){
        Subreddit subreddit = restTemplate.getForObject(BASE_URL+"/"+title, Subreddit.class);
        return subreddit;
    }

    public Subreddit saveSubreddit(Subreddit newSubreddit){
        return restTemplate.postForObject(BASE_URL, newSubreddit, Subreddit.class);
    }

    public void updateSubreddit(String title, Subreddit updatedSubreddit){
        restTemplate.put(BASE_URL+"/"+title, updatedSubreddit);
    }

    public long countSubreddits(){
        List<Subreddit> subreddits = getAllSubreddits();
        return subreddits.size();
    }

    public void deleteSubredditByTitle(String title){
        restTemplate.delete(BASE_URL+"/"+title);
    }

    public List<Subreddit> search(String searchString){
        String search = searchString.toLowerCase();
        return getAllSubreddits().stream().filter(b -> b.getTitle().toLowerCase()
                .contains(search))
                .collect(Collectors.toList());
    }
}
