package com.kazemieh.rasteh.marketplace.order

import com.kazemieh.rasteh.identity.application.exception.UserNotFoundException
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.marketplace.domain.OrderStatus
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderEntity
import com.kazemieh.rasteh.marketplace.order.entity.MarketplaceOrderItemEntity
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.shared.error.MarketplaceInsufficientStockException
import com.kazemieh.rasteh.shared.error.MarketplaceOrderNotFoundException
import com.kazemieh.rasteh.shared.error.OrderVendorMismatchException
import com.kazemieh.rasteh.shared.error.ProductNotPurchasableException
import com.kazemieh.rasteh.shared.error.ShopNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class MarketplaceOrderService(
    private val orderRepository: MarketplaceOrderRepository,
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
    private val userRepository: UserRepository,
) {

    /** ثبتِ سفارشِ تک‌ونـدوری — اعتبارسنجی، کسرِ موجودی، محاسبهٔ مبلغ. */
    @Transactional
    fun create(userId: Long, req: CreateOrderRequest): OrderResponse {
        val shop = shopRepository.findById(req.shopId).orElseThrow { ShopNotFoundException(req.shopId) }
        if (shop.status != ShopStatus.APPROVED || shop.type != ShopType.BUYABLE) {
            throw ProductNotPurchasableException(req.shopId)
        }
        val customer = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

        val order = MarketplaceOrderEntity(shop = shop, customer = customer, status = OrderStatus.CONFIRMED, note = req.note?.trim())
        var total = BigDecimal.ZERO

        for (line in req.items) {
            val product = productRepository.findById(line.productId).orElseThrow { ProductNotPurchasableException(line.productId) }
            // تک‌ونـدوری: هر کالا باید از همین فروشگاه باشد.
            if (product.shop?.id != shop.id) throw OrderVendorMismatchException()
            if (!product.active || product.price <= BigDecimal.ZERO) throw ProductNotPurchasableException(product.id)
            if (product.stock < line.quantity) throw MarketplaceInsufficientStockException(product.id)

            product.stock -= line.quantity
            val lineTotal = product.price.multiply(BigDecimal(line.quantity))
            total = total.add(lineTotal)
            order.items.add(
                MarketplaceOrderItemEntity(
                    order = order,
                    product = product,
                    productName = product.name,
                    unitPrice = product.price,
                    quantity = line.quantity,
                    lineTotal = lineTotal,
                )
            )
        }
        order.totalAmount = total
        shop.salesCount += req.items.sumOf { it.quantity }
        return OrderMapper.toResponse(orderRepository.save(order))
    }

    @Transactional(readOnly = true)
    fun listMine(userId: Long): List<OrderResponse> =
        orderRepository.findAllByCustomerIdOrderByIdDesc(userId).map(OrderMapper::toResponse)

    @Transactional(readOnly = true)
    fun listForShop(userId: Long, isAdmin: Boolean, shopId: Long): List<OrderResponse> {
        val shop = shopRepository.findById(shopId).orElseThrow { ShopNotFoundException(shopId) }
        if (!isAdmin && shop.owner?.id != userId) throw MarketplaceOrderNotFoundException(shopId)
        return orderRepository.findAllByShopIdOrderByIdDesc(shopId).map(OrderMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun get(userId: Long, isAdmin: Boolean, orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { MarketplaceOrderNotFoundException(orderId) }
        val isCustomer = order.customer?.id == userId
        val isVendor = order.shop?.owner?.id == userId
        if (!isAdmin && !isCustomer && !isVendor) throw MarketplaceOrderNotFoundException(orderId)
        return OrderMapper.toResponse(order)
    }

    /** به‌روزرسانیِ وضعیت — فروشنده/ادمین هر وضعیتی؛ خریدار فقط لغو. */
    @Transactional
    fun updateStatus(userId: Long, isAdmin: Boolean, orderId: Long, statusRaw: String): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { MarketplaceOrderNotFoundException(orderId) }
        val status = runCatching { OrderStatus.valueOf(statusRaw) }.getOrNull()
            ?: throw MarketplaceOrderNotFoundException(orderId)
        val isVendor = order.shop?.owner?.id == userId
        val isCustomer = order.customer?.id == userId
        when {
            isAdmin || isVendor -> order.status = status
            isCustomer && status == OrderStatus.CANCELLED -> order.status = OrderStatus.CANCELLED
            else -> throw MarketplaceOrderNotFoundException(orderId)
        }
        return OrderMapper.toResponse(order)
    }
}
