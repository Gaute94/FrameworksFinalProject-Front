package me.gaute.redditclonefront.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubredditAndUser {
    private List<Subreddit> subreddits;
    private List<User> users;
}
