package com.irinakomarchenko.messagingplatform.groupchannel.repository;

import com.irinakomarchenko.messagingplatform.groupchannel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    boolean existsByNameIgnoreCase(String name);
}
