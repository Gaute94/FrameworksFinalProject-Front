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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    SubredditService subredditService;


    @GetMapping("/")
    public String slash(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model){
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        System.out.println("user: " + user);
        model.addAttribute("subreddits", new ArrayList<Subreddit>());


        return "home";
    }



    @GetMapping("/register")
    public String signup(Model model){
        model.addAttribute("createAdmin", userService.countUsers() == 0);
        System.out.println("Entered register");
        return "register";
    }

    @PostMapping("/processRegistration")
    public String register(@ModelAttribute("user") User user){
        userService.setPassword(user, user.getPassword());
        if(userService.countUsers() == 0){
            user.setRole("ROLE_ADMIN");
        }
        userService.saveUser(user);
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String e, Model model) {
        model.addAttribute("error", e != null);
        return "login";
    }

    @GetMapping("/myAccount")
    public String myAccount(){
        return "myAccount";
    }


    @GetMapping("/searching")
    public String searchBook(@RequestParam("title") String title, Model model){
        if(title.equals("")) return "redirect:/home";
        model.addAttribute("subreddits", subredditService.search(title));
        model.addAttribute("posts", postService.search(title));

        return "search";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String subreddit){
        System.out.println("ENTERED SUBSCRIBE");
        System.out.println("SUBREDDIT: " + subreddit);
        Optional<User> user = userService.getAuthenticatedUser();
        if(!user.isPresent()){
            return "redirect:/home";
        }
        //User user1 = user.get();
        Subreddit subreddit1 = subredditService.getSubredditByTitle(subreddit);
        System.out.println("Subreddit1: " + subreddit1);
        System.out.println("User subreddits: " + user.get().getSubreddits());
        System.out.println("User.get ID: " + user.get().getId());
        System.out.println("USER: " + user);
        user.get().getSubreddits().add(subreddit1);
        System.out.println("User subreddits 2: " + user.get().getSubreddits());
        userService.updateUser(user.get().getId(), user.get());
        return "redirect:/home";
    }
}
