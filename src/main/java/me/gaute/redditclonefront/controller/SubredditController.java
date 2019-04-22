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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class SubredditController {

    @Autowired
    SubredditService subredditService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @GetMapping("/r")
    public String r(Model model){
        List<Subreddit> subreddits = subredditService.getAllSubreddits();
        model.addAttribute("subreddits", subreddits);
        return "r";
    }

    @GetMapping("/r/{subreddit}")
    public String subreddit(Model model, @PathVariable String subreddit){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        List<Post> posts = postService.getPostBySubreddit(subreddit);
        System.out.println("POSTS: " + posts);
        model.addAttribute("posts", posts);
        return "subreddit";
    }

    @GetMapping("/createSubreddit")
    public String createSubreddit(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "createSubreddit";
    }

    @PostMapping("/createSubreddit")
    public String createSubreddit(@RequestParam int user, @RequestParam String title, @RequestParam String rules, @RequestParam String description){
        Optional<User> user1 = userService.getUserById(user);
        if(!user1.isPresent()){
            return "redirect:/home";
        }
        Subreddit subreddit = new Subreddit();
        subreddit.setDescription(description);
        subreddit.setRules(rules);
        subreddit.setTitle(title);
        subreddit.setOwner(user1.get());
        subredditService.saveSubreddit(subreddit);
        return "redirect:/home";
    }
}
