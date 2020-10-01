package com.cfbenchmarks.interview;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.TreeMap;

public class OrderBook {

    /** instrument pertaining to this orderbook */
    private final String INSTRUMENT;

    /** buy/sell TreeMap of orders. There is actually no motivation to use a linked list here. If
     * we didn't need to preserve order, a HashSet would be amazing here for the O(1) access */
    private TreeMap<Long, ArrayList<Order>> buyTree = new TreeMap<>();
    private TreeMap<Long, ArrayList<Order>> sellTree = new TreeMap<>();

    /**
     * All-values ctor.
     *
     * @param instrument the instrument traded on the order book
     */

    public OrderBook(String instrument) {
        this.INSTRUMENT = instrument;
    }

    public String getInstrument() {
        return INSTRUMENT;
    }

    public TreeMap<Long, ArrayList<Order>> getBuyTree() {
        return buyTree;
    }

    public TreeMap<Long, ArrayList<Order>> getSellTree() {
        return sellTree;
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "INSTRUMENT='" + INSTRUMENT + '\'' +
                ", buyTree=" + buyTree +
                ", sellTree=" + sellTree +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBook orderBook = (OrderBook) o;
        return Objects.equal(INSTRUMENT, orderBook.INSTRUMENT) &&
                Objects.equal(buyTree, orderBook.buyTree) &&
                Objects.equal(sellTree, orderBook.sellTree);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(INSTRUMENT, buyTree, sellTree);
    }
}
