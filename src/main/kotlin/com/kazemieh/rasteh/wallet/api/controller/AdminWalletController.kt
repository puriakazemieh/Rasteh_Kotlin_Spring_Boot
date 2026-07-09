package com.kazemieh.rasteh.wallet.api.controller

import com.kazemieh.rasteh.wallet.api.dto.*
import com.kazemieh.rasteh.wallet.application.AdminWalletService
import com.kazemieh.rasteh.wallet.persistence.entity.WithdrawalStatus
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/wallet")
@Tag(name = "Admin Wallet")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
class AdminWalletController(
    private val adminWalletService: AdminWalletService
) {

    @PostMapping("/adjust")
    fun adjustBalance(@RequestBody req: AdminAdjustBalanceRequest) {
        adminWalletService.adjustBalance(req)
    }

    @GetMapping("/users/search")
    fun searchUsers(@RequestParam query: String): List<UserWalletInfoResponse> =
        adminWalletService.searchUsersWithWallet(query)

    @GetMapping("/withdrawals")
    fun listWithdrawals(@RequestParam(required = false) status: WithdrawalStatus?): List<WithdrawalRequestResponse> =
        adminWalletService.listWithdrawalRequests(status)

    @PostMapping("/withdrawals/{id}/process")
    fun processWithdrawal(@PathVariable id: Long, @RequestBody req: AdminProcessWithdrawalRequest) {
        adminWalletService.processWithdrawal(id, req)
    }
}
