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

import java.util.Collection;
import java.util.Collections;
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
    public String r(Model model) {
        List<Subreddit> subreddits = subredditService.getAllSubreddits();
        model.addAttribute("subreddits", subreddits);
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "r";
    }

    @GetMapping("/r/{subreddit}")
    public String subreddit(Model model, @PathVariable String subreddit){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        Subreddit subreddit1 = subredditService.getSubredditByTitle(subreddit);
        model.addAttribute("subredditTitle", subreddit);
        System.out.println("SubredditTitle: " + subreddit);
        model.addAttribute("subreddit1", subreddit1);
        if(subreddit.equals("All")){
            List<Post> allPosts = postService.getAllPosts();
            Collections.reverse(allPosts);
            model.addAttribute("posts", allPosts);
        }else{
            List<Post> posts = postService.getPostBySubreddit(subreddit1);
            Collections.reverse(posts);
            model.addAttribute("posts", posts);

        }
        System.out.println("Subreddit: " + subreddit1);
        model.addAttribute("description", subreddit1.getDescription());
        model.addAttribute("subreddit", subreddit);
        return "subreddit";
    }

    @GetMapping("/u")
    public String u(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "u";
    }

    @GetMapping("/u/{username}")
    public String userProfile(Model model, @PathVariable String username){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        List<Post> posts = postService.getPostByOwner(username);
        model.addAttribute("posts", posts);
        model.addAttribute("username", username);
        System.out.println("username: " + username);
        for(Post post : posts){
            System.out.println("Title" + post.getTitle());
            System.out.println("Owner" + post.getOwner());
        }
        return "userProfile";
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
