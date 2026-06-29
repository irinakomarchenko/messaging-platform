package com.irinakomarchenko.messagingplatform.groupchannel.entity;


import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "channel_subscribers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_channel_subscriber_channel_user",
                        columnNames = {"channel_id", "user_id"}
                )
        }
)
public class ChannelSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;

    @PrePersist
    void onCreate() {
        subscribedAt = LocalDateTime.now();
    }
}
