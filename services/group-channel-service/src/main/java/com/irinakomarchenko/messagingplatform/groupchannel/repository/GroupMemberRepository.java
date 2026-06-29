package com.irinakomarchenko.messagingplatform.groupchannel.repository;

import com.irinakomarchenko.messagingplatform.groupchannel.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroup_Id(Long groupId);

    Optional<GroupMember> findByGroup_IdAndUserId(Long groupId, Long userId);

    boolean existsByGroup_IdAndUserId(Long groupId, Long userId);

    long countByGroup_Id(Long groupId);
}
