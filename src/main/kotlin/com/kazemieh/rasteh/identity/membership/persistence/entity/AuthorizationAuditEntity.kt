package com.kazemieh.rasteh.identity.membership.persistence.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity @Table(name = "identity_audit_events")
class AuthorizationAuditEntity(@Id var id: UUID = UUID.randomUUID(), @Column(name="actor_user_id") var actorUserId: Long? = null,
    @Column(name="subject_user_id") var subjectUserId: Long? = null, @Column(name="shop_id") var shopId: Long? = null,
    var action: String = "", @Column(name="detail_json", columnDefinition="jsonb") var detailJson: String = "{}", @Column(name="created_at") var createdAt: OffsetDateTime? = null)
