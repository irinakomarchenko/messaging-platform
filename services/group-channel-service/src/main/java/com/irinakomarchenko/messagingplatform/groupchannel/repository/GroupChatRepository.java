package com.irinakomarchenko.messagingplatform.groupchannel.repository;

import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
}
