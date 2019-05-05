package me.gaute.redditclonefront.controller;

import me.gaute.redditclonefront.model.Image;
import me.gaute.redditclonefront.model.Post;
import me.gaute.redditclonefront.model.Subreddit;
import me.gaute.redditclonefront.model.User;
import me.gaute.redditclonefront.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @Autowired
    SubredditService subredditService;

    @Autowired
    DeleteService deleteService;

    @Autowired
    ImageService imageService;

    @GetMapping("/")
    public String slash(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model){
        /*List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);*/
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));

        if(user.isPresent()){
            List<Subreddit> subreddits = user.get().getSubreddits();
            model.addAttribute("subreddits", subreddits);
            List<Post> posts = new ArrayList<>();
            for(Subreddit subreddit : subreddits){
                List<Post> subPosts = postService.getPostBySubreddit(subreddit);
                Collections.reverse(subPosts);
                posts.addAll(subPosts);
            }
            model.addAttribute("posts", posts);
        }else{
            List<Post> posts = postService.getAllPosts();
            Collections.reverse(posts);
            model.addAttribute("posts", posts);
            List<Subreddit> subreddits = subredditService.getAllSubreddits();
            model.addAttribute("subreddits", subreddits);
        }

        System.out.println("user: " + user);
        //model.addAttribute("subreddits", new ArrayList<Subreddit>());


        return "home";
    }



    @GetMapping("/register")
    public String signup(Model model){
        model.addAttribute("createAdmin", userService.countUsers() == 0);
        System.out.println("Entered register");
        return "register";
    }

    @GetMapping("/register/exists")
    public String register(Model model){
        model.addAttribute("createAdmin", userService.countUsers() == 0);
        System.out.println("Entered register");
        String exists = "Already a user with that username or email address";
        model.addAttribute("exists", exists);
        System.out.println("exists: " + exists);
        return "register";
    }

    @PostMapping("/processRegistration")
    public String register(@ModelAttribute("user") User user){
        userService.setPassword(user, user.getPassword());
        if(userService.countUsers() == 0){
            user.setRole("ROLE_ADMIN");
        }
        if(userService.saveUser(user).isOk()){
            return "redirect:/home";
        }else{

            return "redirect:/register/exists";
        }

    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String e, Model model) {
        model.addAttribute("error", e != null);
        return "login";
    }

    @GetMapping("/myAccount")
    public String myAccount(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        if(!user.isPresent()){
            return "redirect:/home";
        }
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        model.addAttribute("username", user.get().getUName());
        System.out.println("USERNAME: " + user.get().getUsername());
        List<Post> posts = postService.getPostByOwner(user.get().getUsername());
        Collections.reverse(posts);
        model.addAttribute("posts", posts);
        List<User> following = user.get().getFollowing();
        model.addAttribute("following", following);
        return "myAccount";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(Model model, @RequestParam String username){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        model.addAttribute("username", username);
        return "changePassword";
    }

    @PostMapping("/passwordChange")
    public String passwordChange(@RequestParam String password, @RequestParam String username){
        User user = userService.getUserByUsername(username);
        userService.setPassword(user, password);
        userService.updateUser(user.getId(), user);

        return "redirect:/myAccount";
    }


    @GetMapping("/deleteAccount")
    public String deleteAccount(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        if(user.isPresent()){
            System.out.println("ROLE: " + user.get().getRole());
        }
        return "deleteAccount";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(Model model, @RequestParam String username){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        model.addAttribute("username", username);
        return "deleteAccount";
    }

    @PostMapping("/accountDelete")
    public String accountDelete(@RequestParam String confirmed, @RequestParam String username){
        User user = userService.getUserByUsername(username);
        System.out.println("DELETE user: " + userService.getUserByUsername(username));
        System.out.println("DELETE user ID: " + user.getId());
        if(!confirmed.equals("true")){
            return "redirect:/myAccount";
        }else {
            deleteService.deleteUserByUsername(username);
            return "redirect:/home";
        }
    }


    @GetMapping("/searching")
    public String searchBook(@RequestParam("title") String title, Model model){
        if(title.equals("")) return "redirect:/home";
        model.addAttribute("subreddits", subredditService.search(title));
        model.addAttribute("posts", postService.search(title));

        return "search";
    }

    @PostMapping("/follow")
    public String follow(@RequestParam String username){
        Optional<User> user = userService.getAuthenticatedUser();
        if(!user.isPresent()){
            return "redirect:/home";
        }

        User user1 = userService.getUserByUsername(username);
        if(user.get().getFollowing().contains(user1)){
            return "redirect:/home";
        }

        user.get().getFollowing().add(user1);
        userService.updateUser(user.get().getId(), user.get());
        return "redirect:/u/" + username;
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
        if(user.get().getSubreddits().contains(subreddit1)){
            return "redirect:/home";
        }
        System.out.println("Subreddit1: " + subreddit1);
        System.out.println("User subreddits: " + user.get().getSubreddits());
        System.out.println("User.get ID: " + user.get().getId());
        System.out.println("USER: " + user);
        user.get().getSubreddits().add(subreddit1);
        System.out.println("User subreddits 2: " + user.get().getSubreddits());
        userService.updateUser(user.get().getId(), user.get());
        return "redirect:/r/" + subreddit;
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestParam String subreddit){
        Optional<User> user = userService.getAuthenticatedUser();
        if(user.isPresent()){
            user.get().getSubreddits().remove(subredditService.getSubredditByTitle(subreddit));
            userService.updateUser(user.get().getId(), user.get());
        }
        return "redirect:/r/" + subreddit;
    }

    @GetMapping("/users")
    public String users(Model model){
        Optional<User> authUser = userService.getAuthenticatedUser();
        authUser.ifPresent(user1 -> model.addAttribute("authUser", user1));

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/testImages")
    public String images(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        return "testImages";
    }

    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("imageFile") MultipartFile imageFile, @RequestParam String imageName, @RequestParam int user) {

        Optional<User> user1 = userService.getUserById(user);
        if (!user1.isPresent()) {
            return "redirect:/home";
        } else {
            Image image = new Image();
            image.setImageName(imageName);
            image.setOwner(user1.get());
            try {
                image.setBytes(imageFile.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("No image?");
            }

            imageService.saveImage(image);

        }
        return "redirect:/home";
    }

    @GetMapping("testImageDisplay")
    public String imageDisplay(Model model){
        Optional<User> user = userService.getAuthenticatedUser();
        user.ifPresent(user1 -> model.addAttribute("user", user1));
        if(user.isPresent()) {
            List<Image> images1 = imageService.getImageByOwner(user.get().getUsername());
            List<String> images = new ArrayList<>();

            for (Image image : images1) {
                byte[] encode = Base64.getEncoder().encode(image.getBytes());
                images.add(new String(encode, StandardCharsets.UTF_8));
            }
            model.addAttribute("images", images);
            System.out.println("ImageBase64: " +images);
        }
        return "testImageDisplay";
    }
}
