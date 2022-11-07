package com.qingru.springbootmemory.util;

import com.qingru.springbootmemory.domain.Profile;
import com.qingru.springbootmemory.domain.ProfileBean;

import java.util.ArrayList;
import java.util.List;

public class ProfileMockUtil {

    private static long idGenerator = 0;

    public static Profile getMockFullProfile() {
        return Profile.builder().id(idGenerator++).name("Mock Profile " + Math.random())
                .gender("Female").description("Mock description")
                .age((int) (Math.random() * (100 - 20 + 1) + 20)).ownerTeam("FRUITY")
                .aliases(List.of("Profile " + Math.random(), "Mock", "Test"))
                .tags(List.of("Test", "Mock", "Memory", "Java", "Spring")).build();
    }

    public static ProfileBean getProfileBeanByProfile(Profile profile) {
        return ProfileBean.builder().id(String.valueOf(profile.getId())).name(profile.getName())
                .description(profile.getDescription()).ownerTeam(profile.getOwnerTeam())
                .aliases(profile.getAliases()).tags(profile.getTags()).build();
    }

    public static List<ProfileBean> getProfileBeansByProfiles(List<Profile> profiles) {
        List<ProfileBean> profileBeans = new ArrayList<>();
        for (int i = 0; i < profiles.size(); i++) {
            profileBeans.add(getProfileBeanByProfile(profiles.get(i)));
        }
        return profileBeans;
    }
}
