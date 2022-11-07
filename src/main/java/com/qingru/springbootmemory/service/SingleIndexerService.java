package com.qingru.springbootmemory.service;

import com.google.common.collect.Lists;
import com.qingru.springbootmemory.domain.Profile;
import com.qingru.springbootmemory.domain.ProfileBean;
import com.qingru.springbootmemory.util.ProfileMockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SingleIndexerService {

    public final int BATCH_SIZE = 500000; //25000;
    public final int NUM_OF_BATCH = 5; // 1000;
    public final int BLOCK_SIZE = 100;

    public void runWhole() {

        // 1. Retrieve all profiles from database
        List<Profile> allProfiles = new ArrayList<>();
        for (int i = 0; i < NUM_OF_BATCH * BATCH_SIZE; i++) {
            allProfiles.add(ProfileMockUtil.getMockFullProfile());
        }

        // 2. Split into batches

        List<List<Profile>> profileBatches = Lists.partition(allProfiles, BATCH_SIZE);

        // 3. In each batch,
        // Retrieve other beans based on ids (assume part of the batch process)
        for (List<Profile> profileBatch : profileBatches) {
            log.info("-------------------------\n");
            log.info("Processing new batch...\n");
            // Retrieve beans based on ids
            log.info("Retrieving beans...\n");
            List<ProfileBean> profileBeans =
                    ProfileMockUtil.getProfileBeansByProfiles(profileBatch);
            // Index into
            log.info("Indexing beans...\n");
            for (ProfileBean profileBean : profileBeans) {
                int time = 0;
                while (time < 10000) {
                    profileBean.getAliases();
                    profileBean.getDescription();
                    time++;
                }
            }
            //            System.gc();

        }
    }

    public void runBlocks() {
        // 1. Retrieve all profiles from database
        List<Profile> allProfiles = new ArrayList<>();
        for (int i = 0; i < NUM_OF_BATCH * BATCH_SIZE; i++) {
            allProfiles.add(ProfileMockUtil.getMockFullProfile());
        }

        // 2. Split into batches

        List<List<Profile>> profileBatches = Lists.partition(allProfiles, BATCH_SIZE);

        // 3. In each batch,
        for (List<Profile> profileBatch : profileBatches) {
            log.info("-------------------------\n");
            log.info("Processing new batch...\n");
            // Retrieve beans based on ids
            log.info("Retrieving beans...\n");
            List<ProfileBean> profileBeans =
                    ProfileMockUtil.getProfileBeansByProfiles(profileBatch);

            List<List<ProfileBean>> blocksProfileBeans = Lists.partition(profileBeans, BLOCK_SIZE);
            for (List<ProfileBean> blockProfileBeans : blocksProfileBeans) {
                // Index into
                log.info("Indexing beans...\n");

                // Retrieve other beans based on ids
                for (ProfileBean profileBean : blockProfileBeans) {
                    int time = 0;
                    while (time < 10000) {
                        profileBean.getAliases();
                        profileBean.getDescription();
                        time++;
                    }
                }
                System.gc();
            }
        }
    }
}
