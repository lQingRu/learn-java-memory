package com.qingru.springbootmemory.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class Profile {
    private long id;
    private String gender;
    private String name;
    private String description;
    private int age;
    private String ownerTeam;
    private List<String> aliases;
    private List<String> tags;

    // Deprecated, but just for the sake of the ease to see if GC is executed
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Garbage collector called on Profile");
        System.out.println("Profile garbage collected : " + this);
    }
}

