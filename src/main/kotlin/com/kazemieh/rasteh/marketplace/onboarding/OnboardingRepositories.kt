package com.kazemieh.rasteh.marketplace.onboarding
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
interface ShopApplicationRepository:JpaRepository<ShopApplicationEntity,UUID>{fun findByApplicantUserIdAndIdempotencyKey(userId:Long,key:String):ShopApplicationEntity?;fun findAllByStatusOrderByIdDesc(status:ApplicationStatus):List<ShopApplicationEntity>;fun findAllByNormalizedNameAndNormalizedPhoneAndUnitId(name:String,phone:String?,unitId:UUID?):List<ShopApplicationEntity>}
interface ApplicationEvidenceRepository:JpaRepository<ApplicationEvidenceEntity,UUID>{fun findAllByApplicationId(applicationId:UUID):List<ApplicationEvidenceEntity>}
interface ShopClaimRepository:JpaRepository<ShopClaimEntity,UUID>{fun findByApplicationId(applicationId:UUID):ShopClaimEntity?}
interface OnboardingAuditRepository:JpaRepository<OnboardingAuditEntity,UUID>
