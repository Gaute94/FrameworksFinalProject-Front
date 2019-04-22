package me.gaute.redditclonefront.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Post {
    private long id;
    private String title;
    private String comment;
    private Subreddit subreddit;
    private User owner;
    private int likes;

}
