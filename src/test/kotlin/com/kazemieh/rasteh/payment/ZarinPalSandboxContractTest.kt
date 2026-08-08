package com.kazemieh.rasteh.payment

import com.kazemieh.rasteh.payment.application.ZarinPalService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import java.util.UUID

class ZarinPalSandboxContractTest {
    private val restTemplate = RestTemplate()
    private val server = MockRestServiceServer.createServer(restTemplate)
    private val service = ZarinPalService(
        merchantId = UUID.randomUUID().toString(),
        isSandbox = true,
        accessToken = "",
        callbackUrl = "https://sandbox-callback.example.invalid/api/payment/callback",
        restTemplate = restTemplate,
    )

    @Test
    fun `sandbox request sends the IRR minor-unit amount without a toman conversion`() {
        server.expect(requestTo("https://sandbox.zarinpal.com/pg/v4/payment/request.json"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.amount").value(125000))
            .andExpect(jsonPath("$.callback_url").value("https://sandbox-callback.example.invalid/api/payment/callback"))
            .andRespond(withSuccess("""{"data":{"authority":"sandbox-authority"}}""", MediaType.APPLICATION_JSON))

        assertThat(service.createPaymentRequest(125000L, "order-42"))
            .isEqualTo("https://sandbox.zarinpal.com/pg/StartPay/sandbox-authority")

        server.verify()
    }

    @Test
    fun `sandbox verification uses the same IRR minor-unit amount as the request`() {
        server.expect(requestTo("https://sandbox.zarinpal.com/pg/v4/payment/verify.json"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(jsonPath("$.amount").value(125000))
            .andExpect(jsonPath("$.authority").value("sandbox-authority"))
            .andRespond(withSuccess("""{"data":{"code":100,"ref_id":42}}""", MediaType.APPLICATION_JSON))

        assertThat(service.verifyPayment("sandbox-authority", 125000L).isSuccess).isTrue()

        server.verify()
    }
}
