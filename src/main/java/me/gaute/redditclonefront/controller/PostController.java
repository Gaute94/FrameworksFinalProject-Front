package me.gaute.redditclonefront.controller;

import me.gaute.redditclonefront.model.Post;
import me.gaute.redditclonefront.model.Subreddit;
import me.gaute.redditclonefront.model.User;
import me.gaute.redditclonefront.service.PostService;
import me.gaute.redditclonefront.service.SubredditService;
import me.gaute.redditclonefront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    SubredditService subredditService;

    @Autowired
    UserService userService;

    @GetMapping("/submit")
    public String submit(Model model){
        List<Subreddit> subreddits = subredditService.getAllSubreddits();
        model.addAttribute("subreddits", subreddits);
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "submit";
    }

    @PostMapping("/createPost")
    public String addPost(@RequestParam("subreddit.title") String subreddit, @RequestParam String title, @RequestParam String comment, @RequestParam int user) {
        System.out.println("SUBREDDIT: " + subreddit);
        Optional<User> user1 = userService.getUserById(user);
        Subreddit subreddit1 = subredditService.getSubredditByTitle(subreddit);
        if(!user1.isPresent()){
            return "redirect:/home";
        }
        Post post = new Post();
        post.setSubreddit(subreddit1);
        post.setTitle(title);
        post.setComment(comment);
        post.setOwner(user1.get());
        postService.savePost(post);
        return "redirect:/home";
    }
}
