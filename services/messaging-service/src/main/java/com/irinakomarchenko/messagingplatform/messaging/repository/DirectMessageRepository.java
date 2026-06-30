package com.irinakomarchenko.messagingplatform.messaging.repository;

import com.irinakomarchenko.messagingplatform.messaging.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("""
            select message
            from DirectMessage message
            where
                (
                    message.senderUserId = :userId
                    and message.recipientUserId = :otherUserId
                )
                or
                (
                    message.senderUserId = :otherUserId
                    and message.recipientUserId = :userId
                )
            order by message.createdAt asc, message.id asc
            """)
    List<DirectMessage> findConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
}
