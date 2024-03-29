package me.gaute.redditclonefront.controller;

import me.gaute.redditclonefront.model.Image;
import me.gaute.redditclonefront.model.Post;
import me.gaute.redditclonefront.model.Subreddit;
import me.gaute.redditclonefront.model.User;
import me.gaute.redditclonefront.service.ImageService;
import me.gaute.redditclonefront.service.PostService;
import me.gaute.redditclonefront.service.SubredditService;
import me.gaute.redditclonefront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @Autowired
    ImageService imageService;

    @GetMapping("/submit")
    public String submit(Model model) {
        List<Subreddit> subreddits = subredditService.getAllSubreddits();
        model.addAttribute("subreddits", subreddits);
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "submit";
    }

    @PostMapping("/createPost")
    public String addPost(@RequestParam(value = "imageFile", required = false) MultipartFile imageFile, @RequestParam("subreddit.title") String subreddit, @RequestParam String title, @RequestParam String comment, @RequestParam int user) {
        System.out.println("SUBREDDIT: " + subreddit);
        Optional<User> user1 = userService.getUserById(user);
        Subreddit subreddit1 = subredditService.getSubredditByTitle(subreddit);
        if (!user1.isPresent()) {
            return "redirect:/home";
        }
        Image image = new Image();
        image.setImageName("");
        image.setOwner(user1.get());
        try {
            image.setBytes(imageFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No image?");
        }
        Post post = new Post();
        post.setSubreddit(subreddit1);
        post.setTitle(title);
        post.setComment(comment);
        post.setOwner(user1.get());
        post.setDate(LocalDateTime.now());
        if (!imageFile.isEmpty(

        )) {
            post.setImage(imageService.saveImage(image));
        }
        postService.savePost(post);
        return "redirect:/home";
    }

    @PostMapping("/vote/{id}")
    public String downvote(Model model, @PathVariable String id, @RequestParam String subreddit, @RequestParam String vote, @RequestParam String home) {
        long id1 = Long.parseLong(id);
        Optional<Post> post = postService.getPostById(id1);
        model.addAttribute("subreddit", subreddit);
        System.out.println("PostDownvote Subreddit: " + subreddit);
        Optional<User> user = userService.getAuthenticatedUser();

        if (post.isPresent() && user.isPresent() && !post.get().getUserLikes().contains(user.get())) {
            if (vote.equals("downvote")) {
                post.get().getUserLikes().add(user.get());
                post.get().setLikes(post.get().getLikes() - 1);
            } else {
                post.get().getUserLikes().add(user.get());
                post.get().setLikes(post.get().getLikes() + 1);
            }
            postService.updatePost(id1, post.get());
        }
        if (home.equals("home")) {
            return "redirect:/home";
        } else {
            return "redirect:/r/" + subreddit;
        }
    }
}
