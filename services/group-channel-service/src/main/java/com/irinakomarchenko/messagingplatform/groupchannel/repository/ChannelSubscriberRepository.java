package com.irinakomarchenko.messagingplatform.groupchannel.repository;

import com.irinakomarchenko.messagingplatform.groupchannel.entity.ChannelSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelSubscriberRepository extends JpaRepository<ChannelSubscriber, Long> {

    List<ChannelSubscriber> findByChannel_Id(Long channelId);

    Optional<ChannelSubscriber> findByChannel_IdAndUserId(Long channelId, Long userId);

    boolean existsByChannel_IdAndUserId(Long channelId, Long userId);

    long countByChannel_Id(Long channelId);
}
