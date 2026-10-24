package com.kazemieh.rasteh.marketplace.onboarding

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

enum class ApplicationStatus { DRAFT, CONSENTED, SUBMITTED, UNDER_REVIEW, APPROVED, CLAIM_PENDING, ACTIVE, NEEDS_CHANGES, REJECTED, SUSPENDED, CLOSED }
enum class ClaimStatus { PENDING_OTP, VERIFIED, APPROVED, REJECTED }

@Entity @Table(name="shop_applications") class ShopApplicationEntity(
 @Id var id:UUID=UUID.randomUUID(), @Column(name="applicant_user_id") var applicantUserId:Long=0, @Column(name="agent_user_id") var agentUserId:Long?=null,
 @Column(name="cluster_id") var clusterId:UUID?=null,@Column(name="building_id") var buildingId:UUID?=null,@Column(name="unit_id") var unitId:UUID?=null,
 var name:String="",@Column(name="normalized_name") var normalizedName:String="",var phone:String?=null,@Column(name="normalized_phone") var normalizedPhone:String?=null,var address:String?=null,
 var lat:Double?=null,var lng:Double?=null,@Column(name="indoor_x") var indoorX:Double?=null,@Column(name="indoor_y") var indoorY:Double?=null,@Column(name="precise_location_consented_at") var preciseLocationConsentedAt:OffsetDateTime?=null,
 @Enumerated(EnumType.STRING) var status:ApplicationStatus=ApplicationStatus.DRAFT,@Column(name="duplicate_of_id") var duplicateOfId:UUID?=null,@Column(name="idempotency_key") var idempotencyKey:String="",
 @Column(name="review_reason") var reviewReason:String?=null,@Column(name="appeal_text") var appealText:String?=null,@Column(name="reviewed_by_user_id") var reviewedByUserId:Long?=null,@Column(name="reviewed_at") var reviewedAt:OffsetDateTime?=null)
@Entity @Table(name="application_evidence") class ApplicationEvidenceEntity(@Id var id:UUID=UUID.randomUUID(),@Column(name="application_id") var applicationId:UUID=UUID.randomUUID(),@Column(name="object_key") var objectKey:String="",@Column(name="media_type") var mediaType:String="",var sha256:String="",var classification:String="PRIVATE",@Column(name="retention_until") var retentionUntil:OffsetDateTime?=null,@Column(name="uploaded_by_user_id") var uploadedByUserId:Long=0)
@Entity @Table(name="shop_claims") class ShopClaimEntity(@Id var id:UUID=UUID.randomUUID(),@Column(name="application_id") var applicationId:UUID=UUID.randomUUID(),@Column(name="claimant_user_id") var claimantUserId:Long=0,@Enumerated(EnumType.STRING) var status:ClaimStatus=ClaimStatus.PENDING_OTP,@Column(name="otp_verified_at") var otpVerifiedAt:OffsetDateTime?=null,@Column(name="approved_by_user_id") var approvedByUserId:Long?=null,@Column(name="approved_at") var approvedAt:OffsetDateTime?=null)
@Entity @Table(name="onboarding_audit_events") class OnboardingAuditEntity(@Id var id:UUID=UUID.randomUUID(),@Column(name="application_id") var applicationId:UUID?=null,@Column(name="actor_user_id") var actorUserId:Long?=null,var action:String="",@Column(name="detail_json",columnDefinition="jsonb") var detailJson:String="{}",@Column(name="created_at") var createdAt:OffsetDateTime?=null)
