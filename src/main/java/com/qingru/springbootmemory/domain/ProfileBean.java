package com.qingru.springbootmemory.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProfileBean {
    private String id;
    private String name;
    private String description;
    private String ownerTeam;
    private List<String> aliases;
    private List<String> tags;


    // Deprecated, but just for the sake of the ease to see if GC is executed
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Garbage collector called on ProfileBean");
        System.out.println("ProfileBean garbage collected : " + this);
    }
}
