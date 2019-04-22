package me.gaute.redditclonefront.service;

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
public class UserService implements UserDetailsService{

    String BASE_URL = "http://localhost:9003/users";
    private RestTemplate restTemplate = new RestTemplate();
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(restTemplate.getForObject(BASE_URL+"/email/"+email, User.class));
        if(!user.isPresent()) throw new UsernameNotFoundException("Not found user with username: " + email);
        return user.get();
    }

    public List<User> getAllUsers(){
        return  Arrays.stream(
                Objects.requireNonNull(restTemplate.getForObject(BASE_URL, User[].class))
        ).collect(Collectors.toList());
    }

    public Optional<User> getUserById(long id){
        try {
            User user = restTemplate.getForObject(BASE_URL+"/id/"+id, User.class);
            return Optional.ofNullable(user);
        } catch(Exception e){
            return Optional.empty();
        }
    }

    public User getUserByEmail(String email){
        return restTemplate.getForObject(BASE_URL+"/email/"+email, User.class);
    }

    public User saveUser(User newUser){
        return restTemplate.postForObject(BASE_URL, newUser, User.class);
    }

    public void updateUser(long id, User updatedUser){
        restTemplate.put(BASE_URL+"/"+id, updatedUser);
    }

    public long countUsers(){
        List<User> users = getAllUsers();
        return users.size();
    }

    public void deleteUserById(long id){
        restTemplate.delete(BASE_URL+"/"+id);
    }

    public void setPassword(User user, String password){
        user.setPassword(passwordEncoder().encode(password));
    }

    public Optional<User> getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        System.out.println("Name: " + auth.getName());
        return Optional.ofNullable(getUserByEmail(auth.getName()));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}
