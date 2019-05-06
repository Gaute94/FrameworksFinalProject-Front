package me.gaute.redditclonefront.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
@NoArgsConstructor
public class Image {
    private long id;
    private String imageName;
    private User owner;
    private byte[] bytes;

    public String getBase64(){
        byte[] encode = Base64.getEncoder().encode(getBytes());
        return new String(encode, StandardCharsets.UTF_8);
    }
}

