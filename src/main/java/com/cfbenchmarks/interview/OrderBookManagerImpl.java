package com.cfbenchmarks.interview;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class OrderBookManagerImpl implements OrderBookManager {

    /**
     * The interface suggests this is a class that manages many orderbooks for many instruments,
     * if this is the case, then I don't believe I agree with this interface, Since if this is an
     * orderbook manager as opposed to an orderbook itself, why would we have
     * operations such as addOrder() at this level? We should be using an OrderBook class for this
     * instead (Which I have decided to implement barebones to achieve a
     * certain level of data compartmentalisation. Having said that, everything has been programmed to
     * the interface.
     */

    /**
     * HashMap of order books instrument as key and the orderbook object as value.
     */
    private static HashMap<String, OrderBook> orderBooks = new HashMap<>();

    /**
     * Store a list of all active orders in a map of IDs for easy object reference retrieval. This is not a real world
     * solution as we would use some kind of datastore.
     */
    private HashMap<String, Order> activeOrders = new HashMap<>();

    /**
     * Static singleton instance.
     */
    private static OrderBookManagerImpl INSTANCE = new OrderBookManagerImpl();

    /**
     * Static factory here allows us to get the singleton object
     */
    public static OrderBookManagerImpl getInstance() { return INSTANCE; }

    /**
     * Prevent instantiation
     */
    private OrderBookManagerImpl() {}

    /**
     * {@inheritDoc}
     *
     * @param order the order to be added
     */
    public void addOrder(Order order) {
        requireNonNull(order);

        // We should first check for a duplicate, go to the pseudo data store for this orderId
        if(activeOrders.containsKey(order.getOrderId()))
            // We could probably have a custom exception here but for now illegal arg will suffice
            throw new IllegalArgumentException(
                    "An order with the ID " + order.getOrderId() + " already exists"
            );

        // If we haven't seen an order for this instrument before, we need to instantiate
        // an order book for it to be traded on and store it in a hashmap for easy retrieval
        // based on instrument key
        if (!orderBooks.containsKey(order.getInstrument()))
            orderBooks.put(order.getInstrument(), new OrderBook(order.getInstrument()));

        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(order);

        // If the tree doesn't contain the price level then create a new key-value pair for it
        if (!tree.containsKey(order.getPrice())) {
            ArrayList<Order> newPriceLevel = new ArrayList<>();
            tree.put(order.getPrice(), newPriceLevel);
        }

        // Persist the object to the generic non-discriminate object store for easy retrieval
        tree.get(order.getPrice()).add(order);
        activeOrders.put(order.getOrderId(), order);
    }

    /**
     * {@inheritDoc}
     *
     * @param orderId     the String id of the order
     * @param newQuantity the new quantity for the order
     */
    public boolean modifyOrder(String orderId, long newQuantity) {
        // before we do any processing we should simply delete for 0 quantities
        if (newQuantity == 0)
            deleteOrder(orderId);

        Order order = getOrderFromId(orderId);
        long initQuantity = order.getQuantity(); // save memento before hand
        order.setQuantity(newQuantity);

        if (newQuantity > initQuantity) {
            // just treat this as a new order, thus shifting it to the end of the list
            deleteOrder(orderId);
            addOrder(order);
        }
        return order.getQuantity() == newQuantity;
    }

    /**
     * {@inheritDoc}
     *
     * @param orderId the String id of the order
     */
    public boolean deleteOrder(String orderId) {
        Order order = getOrderFromId(orderId); // will throw if non-existent
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(order);
        ArrayList<Order> ordersForThatPrice = tree.get(order.getPrice());
        // We should delete from the list (theoretical datastore) of active orders
        activeOrders.remove(order.getOrderId());

        // we can use the equals implementation in Order class for easy removal and ride on
        // the back of list booleans method returns
        return ordersForThatPrice.remove(order);
    }

    /**
     * {@inheritDoc}
     *
     * @param instrument the instrument to get the best price for
     * @param side       specifies buy/sell side
     */
    public Optional<Long> getBestPrice(String instrument, Side side) {
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(instrument, side);
        // best price of buy side is the highest and vice a versa
        // use of TreeMap makes this trivial. Note this return could be null
        if (side.equals(Side.BUY)) {
            return Optional.ofNullable(tree.lastKey());
        } else {
            return Optional.ofNullable(tree.firstKey());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param instrument the instrument to get the best price for
     * @param side       specifies buy/sell side
     * @param price      the price level to inspect
     */
    public long getOrderNumAtLevel(String instrument, Side side, long price) {
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(instrument, side);
        if (tree.containsKey(price)) {
            return tree.get(price).size();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @param instrument the instrument to get the best price for
     * @param side       specifies buy/sell side
     * @param price      the price level to inspect
     */
    public long getTotalQuantityAtLevel(String instrument, Side side, long price) {
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(instrument, side);
        long quantity = 0;

        // Side Tree -> prices -> arraylist of orders
        if (tree.containsKey(price)) {
            for (Order o : tree.get(price)) {
                quantity += o.getQuantity();
            }
        }
        return quantity;
    }

    /**
     * {@inheritDoc}
     *
     * @param instrument the instrument to get the best price for
     * @param side       specifies buy/sell side
     * @param price      the price level to inspect
     */
    public long getTotalVolumeAtLevel(String instrument, Side side, long price) {
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(instrument, side);
        // Because multiplication is commutative and associative we could simply
        // get the cumulative quantity through getOrderNumAtLevel. but why do N + N when you can
        // simply do it in N
        long volume = 0;

        // Side Tree -> prices -> arraylist of orders
        if (tree.containsKey(price)) {
            for (Order o : tree.get(price)) {
                volume += o.getQuantity() * o.getPrice();
            }
        }
        return volume;
    }

    /**
     * {@inheritDoc}
     *
     * @param instrument the instrument to get the best price for
     * @param side       specifies buy/sell side
     * @param price      the price level to inspect
     */
    public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
        TreeMap<Long, ArrayList<Order>> tree = OrderBookManagerImpl.getSideTree(instrument, side);
        return tree.get(price);
    }

    /************************************
     *                                  *
     *          AUX METHODS             *
     *                                  *
     ************************************/

    private Order getOrderFromId(String orderId) {
        if (!activeOrders.containsKey(orderId))
            throw new IllegalArgumentException("Unknown order ID " + orderId);
        // Fetch the object reference from the indiscriminate data store
        return activeOrders.get(orderId);
    }

    private static TreeMap<Long, ArrayList<Order>> getSideTree(Order o) {
        OrderBook orderbook = orderBooks.get(o.getInstrument());
        if (o.getSide().equals(Side.BUY))
            return orderbook.getBuyTree();
        return orderbook.getSellTree();
    }

    private static TreeMap<Long, ArrayList<Order>> getSideTree(String instrument, Side s) {
        OrderBook orderbook = orderBooks.get(instrument);
        if (s.equals(Side.BUY))
            return orderbook.getBuyTree();
        return orderbook.getSellTree();
    }
}
