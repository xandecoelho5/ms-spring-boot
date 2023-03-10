package com.xandecoelho5.OrderService.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.xandecoelho5.OrderService.OrderServiceConfig;
import com.xandecoelho5.OrderService.entity.Order;
import com.xandecoelho5.OrderService.model.OrderRequest;
import com.xandecoelho5.OrderService.model.OrderResponse;
import com.xandecoelho5.OrderService.model.PaymentMode;
import com.xandecoelho5.OrderService.repository.OrderRepository;
import com.xandecoelho5.OrderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
class OrderControllerTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MockMvc mockMvc;
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8080))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    @Test
    void test_WhenPlaceOrder_DoPayment_Success() throws Exception {
        //First Place Order
        //Get Order by Order Id from Db and Cheack
        //Check Output

        OrderRequest orderRequest = getMockOrderRequest();
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String orderId = mvcResult.getResponse().getContentAsString();

        var optionalOrder = orderRepository.findById(Long.valueOf(orderId));
        assertTrue(optionalOrder.isPresent());

        var order = optionalOrder.get();
        assertEquals(Long.parseLong(orderId), order.getId());
        assertEquals("PLACED", order.getOrderStatus());
        assertEquals(orderRequest.totalAmount(), order.getAmount());
        assertEquals(orderRequest.quantity(), order.getQuantity());
    }

    @Test
    void test_WhenPlaceOrderWithWrongAccess_thenThrow403() throws Exception {
        OrderRequest orderRequest = getMockOrderRequest();
        mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

//    @Test
//    void test_WhenGetOrder_Success() throws Exception {
//        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/order/1")
//                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//        String actualResponse = mvcResult.getResponse().getContentAsString();
//        Order order = orderRepository.findById(1L).get();
//        String expectedResponse = getOrderResponse(order);
//
//        assertEquals(expectedResponse, actualResponse);
//    }

    @Test
    void testWhen_GetOrder_Order_Not_Found() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/order/2")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    private String getOrderResponse(Order order) throws IOException {
        OrderResponse.PaymentDetails paymentDetails = objectMapper.readValue(copyToString(getClass().getResourceAsStream("mock/GetPayment.json"),
                Charset.defaultCharset()), OrderResponse.PaymentDetails.class);
        OrderResponse.ProducDetails producDetails = objectMapper.readValue(copyToString(getClass().getResourceAsStream("mock/GetProduct.json"),
                Charset.defaultCharset()), OrderResponse.ProducDetails.class);
        OrderResponse orderResponse = OrderResponse.builder()
                .paymentDetails(paymentDetails)
                .producDetails(producDetails)
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .orderId(order.getId())
                .build();
        return objectMapper.writeValueAsString(orderResponse);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1L)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .totalAmount(200)
                .build();
    }

    private void reduceQuantity() {
        wireMockServer.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getPaymentDetails() throws IOException {
        wireMockServer.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(getClass().getResourceAsStream("mock/GetPayment.json"), Charset.defaultCharset()))));
    }

    private void doPayment() {
        wireMockServer.stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        wireMockServer.stubFor(get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(getClass().getResourceAsStream("mock/GetProduct.json"), Charset.defaultCharset()))));
    }
}
