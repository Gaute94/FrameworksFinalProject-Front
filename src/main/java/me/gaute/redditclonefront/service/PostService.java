package me.gaute.redditclonefront.service;


import me.gaute.redditclonefront.model.Post;
import me.gaute.redditclonefront.model.Subreddit;
import me.gaute.redditclonefront.model.SubredditAndUser;
import me.gaute.redditclonefront.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private String BASE_URL = "http://reddit-back:9003/posts";
    private RestTemplate restTemplate = new RestTemplate();

    public List<Post> getAllPosts() {
        return Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL, Post[].class))
        ).collect(Collectors.toList());
    }

    public List<Post> getAllPostsByDate() {
        return Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL + "/ordered", Post[].class))
        ).collect(Collectors.toList());
    }

    public Optional<Post> getPostById(long id) {
        try {
            Post post = restTemplate.getForObject(BASE_URL + "/id/" + id, Post.class);
            return Optional.ofNullable(post);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Post> getPostBySubreddit(Subreddit subreddit) {
        System.out.println("Subreddit in getPostBySubreddit: " + subreddit);
        return Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL + "/" + subreddit.getTitle(), Post[].class))
        ).collect(Collectors.toList());
    }

    /*
    public List<Post> getPostBySubreddit(String title) {
        System.out.println("getPostBySubreddit Title: " + title);
        String subreddit = title.toLowerCase();
        return getAllPosts().stream().filter(b -> b.getSubreddit().getTitle().toLowerCase()
                .contains(subreddit))
                .collect(Collectors.toList());
    }*/

    public List<Post> getPostByOwner(String owner) {
        String post = owner.toLowerCase();
        return getAllPosts().stream().filter(b -> b.getOwner().getUName().toLowerCase()
                .contains(post))
                .collect(Collectors.toList());
    }

    public List<Post> getAllSubscribedAndFollowed(List<Subreddit> subreddits, List<User> users) {
        System.out.println("entered getAllSubscribedAndFollowed");
        SubredditAndUser subredditAndUser = new SubredditAndUser(subreddits, users);
        return Arrays.stream(
                Objects.requireNonNull(restTemplate.postForObject(BASE_URL + "/postsFeed", subredditAndUser, Post[].class))
        ).collect(Collectors.toList());
    }

    /*
        public String searchBook(@RequestParam("title") String title, Model model){
            if(title.equals("")) return "redirect:/home";
            model.addAttribute("subreddits", subredditService.search(title));

            return "search";
        }
        */
    public Post savePost(Post newPost) {
        return restTemplate.postForObject(BASE_URL, newPost, Post.class);
    }

    public void updatePost(long id, Post updatedPost) {
        restTemplate.put(BASE_URL + "/" + id, updatedPost);
    }

    public long countPosts() {
        List<Post> posts = getAllPosts();
        return posts.size();
    }

    /*
    public void deletePostById(long id){
        restTemplate.delete(BASE_URL+"/"+id);
    }*/

    public List<Post> search(String searchString) {
        String search = searchString.toLowerCase();
        return getAllPosts().stream().filter(b -> b.getTitle().toLowerCase()
                .contains(search))
                .collect(Collectors.toList());
    }

}
