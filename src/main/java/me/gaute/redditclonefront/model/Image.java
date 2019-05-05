package me.gaute.redditclonefront.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
public class Image {
    private long id;
    private String imageName;
    private User owner;
    private byte[] bytes;

}

