package com.cfbenchmarks.interview;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class SampleTest {

    /**
     * These tests are all conducted on different instruments since we use a singleton manager. We could use a @Before
     * annotation to nullify the singleton through reflection, but I don't like this. Further, we should be mocking here,
     * since unit tests shouldn't be intra-dependent on other functionality working.
     */

    OrderBookManager orderBookManager = OrderBookManagerImpl.getInstance();

    @Test
    public void testBestBidPrice() {
        Order o = new Order.OrderBuilder("order1", "TEST.0", Side.BUY, 200l, 10l).build();
        orderBookManager.addOrder(o);

        // check that best price is 200
        Optional<Long> expectedPrice = Optional.of(200L);
        Optional<Long> actualPrice = orderBookManager.getBestPrice("TEST.0", Side.BUY);
        assertEquals("Best bid price is 200", expectedPrice, actualPrice);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateId() {
        Order o = new Order.OrderBuilder("order2", "TEST.1", Side.BUY, 30, 10l).build();
        Order o1 = new Order.OrderBuilder("order2", "TEST.1", Side.BUY, 30, 10l).build();

        orderBookManager.addOrder(o);
        orderBookManager.addOrder(o1);
    }

    @Test
    public void testModifyOrder() {

        long price = 200L;
        //create order
        Order o = new Order.OrderBuilder("order4", "TEST.2", Side.BUY, price, 10l).build();
        Order o1 = new Order.OrderBuilder("order5", "TEST.2", Side.BUY, price, 12l).build();

        orderBookManager.addOrder(o);
        orderBookManager.addOrder(o1);
        orderBookManager.modifyOrder(o.getOrderId(), 1000l);
        assertEquals("Quantity was modified", o.getQuantity(), 1000l);

        // Check the order with increased quantity was moved to the end of the order list
        List<Order> ordersAtLevel = orderBookManager.getOrdersAtLevel("TEST.2", Side.BUY, price);
        assertEquals("Increased quantity item at the end", o, ordersAtLevel.get(ordersAtLevel.size() - 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeletionOfNonExistentOrder() {

        //create order
        Order o = new Order.OrderBuilder("600", "TEST.3", Side.BUY, 200l, 10l).build();


        orderBookManager.addOrder(o);
        // Attempt to delete twice, we expect the exception here
        orderBookManager.deleteOrder(o.getOrderId());
        orderBookManager.deleteOrder(o.getOrderId());
    }

    @Test
    public void testGetOrderNumAtLevel() {
        long price = 200l;
        Order o = new Order.OrderBuilder("650", "TEST.4", Side.SELL, price, 10l).build();
        Order o2 = new Order.OrderBuilder("1000", "TEST.4", Side.SELL, price, 102131l).build();

        orderBookManager.addOrder(o);
        orderBookManager.addOrder(o2);

        long orderNum = orderBookManager.getOrderNumAtLevel("TEST.4", Side.SELL, price);

        assertEquals("Correct amount of orders for the price level", 2, orderNum);

    }

    @Test
    public void testGetTotalQuantityAtLevel() {
        Order o = new Order.OrderBuilder("12512", "TEST.5", Side.SELL, 300, 10l).build();
        Order o2 = new Order.OrderBuilder("235212", "TEST.5", Side.SELL, 300, 10l).build();

        orderBookManager.addOrder(o);
        orderBookManager.addOrder(o2);

        long orderNum = orderBookManager.getTotalQuantityAtLevel("TEST.5", Side.SELL, 300);

        assertEquals("Correct amount of orders for the price level", orderNum, 20);
    }

    @Test
    public void testGetTotalVolumeAtLevel() {
        Order o = new Order.OrderBuilder("123352", "TEST.6", Side.SELL, 300, 10l).build();
        Order o1 = new Order.OrderBuilder("12412321", "TEST.6", Side.SELL, 300, 10l).build();
        orderBookManager.addOrder(o);
        orderBookManager.addOrder(o1);
        long totalVol = 6000;

        assertEquals(
                "Total volume correct",
                totalVol,
                orderBookManager.getTotalVolumeAtLevel("TEST.6", Side.SELL, 300)
        );
    }

}
