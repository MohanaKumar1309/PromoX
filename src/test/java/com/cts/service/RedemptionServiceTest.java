package com.cts.service;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cts.dto.CheckoutRequest;
import com.cts.entity.Category;
import com.cts.entity.Customer;
import com.cts.entity.Order;
import com.cts.entity.Product;
import com.cts.repository.CampaignCategoryRepository;
import com.cts.repository.CampaignProductRepository;
import com.cts.repository.OrderRepository;
import com.cts.repository.ProductRepository;
import com.cts.repository.PromotionCategoryRepository;
import com.cts.repository.PromotionProductRepository;
import com.cts.repository.RedemptionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromotionService promotionService;
    @Mock
    private PromotionProductRepository promotionProductRepository;
    @Mock
    private PromotionCategoryRepository promotionCategoryRepository;
    @Mock
    private CampaignService campaignService;
    @Mock
    private CampaignProductRepository campaignProductRepository;
    @Mock
    private CampaignCategoryRepository campaignCategoryRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RedemptionRepository redemptionRepository;
    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private RedemptionService redemptionService;

    @Test
    void checkout_ShouldCalculateWithoutOffers() {
        Category category = new Category();
        category.setCategoryId(1L);

        Product product = new Product();
        product.setProductId(1L);
        product.setPrice(BigDecimal.valueOf(100));
        product.setCategory(category);

        CheckoutRequest.CartLine line = new CheckoutRequest.CartLine();
        line.setProductId(1L);
        line.setQuantity(2);

        CheckoutRequest request = new CheckoutRequest();
        request.setItems(List.of(line));

        Customer customer = new Customer();
        customer.setAge(20);

        Order order = new Order();
        order.setOrderId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(promotionService.getActivePromotionEntities()).thenReturn(List.of());
        when(campaignService.getActiveCampaignEntities(20)).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(redemptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = redemptionService.checkout(customer, request);

        assertEquals(BigDecimal.valueOf(200.00).setScale(2), response.getFinalAmount());
        assertEquals(1L, response.getOrderId());
    }
}
