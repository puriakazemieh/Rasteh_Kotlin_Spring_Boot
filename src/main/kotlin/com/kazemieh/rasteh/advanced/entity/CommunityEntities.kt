package com.kazemieh.rasteh.advanced.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.OffsetDateTime

/** پستِ انجمن (community). */
@Entity
@Table(name = "community_posts")
class CommunityPostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    @Column(name = "user_id", nullable = false) var userId: Long = 0,
    @Column(name = "author_name", length = 120) var authorName: String? = null,
    @Column(nullable = false, columnDefinition = "text") var body: String = "",
    @Column(name = "comment_count", nullable = false) var commentCount: Int = 0,
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)

@Entity
@Table(name = "community_comments")
class CommunityCommentEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
    @Column(name = "post_id", nullable = false) var postId: Long = 0,
    @Column(name = "user_id", nullable = false) var userId: Long = 0,
    @Column(name = "author_name", length = 120) var authorName: String? = null,
    @Column(nullable = false, columnDefinition = "text") var body: String = "",
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) var createdAt: OffsetDateTime? = null,
)
