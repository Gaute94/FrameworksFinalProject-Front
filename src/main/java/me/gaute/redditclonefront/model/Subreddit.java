package me.gaute.redditclonefront.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Subreddit {
    private String title;
    private String rules;
    private String description;
    private User owner;
}
