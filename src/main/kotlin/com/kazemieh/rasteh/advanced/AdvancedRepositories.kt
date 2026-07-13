package com.kazemieh.rasteh.advanced

import com.kazemieh.rasteh.advanced.entity.CommunityCommentEntity
import com.kazemieh.rasteh.advanced.entity.CommunityPostEntity
import com.kazemieh.rasteh.advanced.entity.NotificationEntity
import com.kazemieh.rasteh.advanced.entity.SubscriptionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostRepository : JpaRepository<CommunityPostEntity, Long> {
    fun findAllByOrderByIdDesc(): List<CommunityPostEntity>
}
interface CommunityCommentRepository : JpaRepository<CommunityCommentEntity, Long> {
    fun findAllByPostIdOrderByIdAsc(postId: Long): List<CommunityCommentEntity>
}
interface SubscriptionRepository : JpaRepository<SubscriptionEntity, Long> {
    fun findByUserId(userId: Long): SubscriptionEntity?
}
interface NotificationRepository : JpaRepository<NotificationEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<NotificationEntity>
}
