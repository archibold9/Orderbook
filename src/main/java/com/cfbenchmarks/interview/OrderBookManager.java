package com.cfbenchmarks.interview;

import java.util.List;
import java.util.Optional;

/** All functions in this class should throw if given null parameters */
public interface OrderBookManager {

  /**
   * Add new order
   *
   * <p>Orders for the same instrument, on the same side, with the same price should be kept in the
   * order as they arrive
   *
   * @param order new order to add <br>
   * @see Order
   */
  void addOrder(Order order);

  /**
   * Modify existing order.
   *
   * <p>If quantity increases, the order should be put at the end of the queue of orders with the
   * same price. If quantity decreases, the order should maintain its position in the queue or orders
   * with the same price. If quantity is 0, behaves the same as deleteOrder(String orderId) Throws if
   * newQuantity is negative or no such order exists
   *
   * @param orderId unique identifier of existing order to modify
   * @param newQuantity new quantity for the order, NOT a delta from previous quantity, always
   *     positive
   * @return True if the order was modified, false otherwise
   */
  boolean modifyOrder(String orderId, long newQuantity);

  /**
   * Delete an existing order Throws if no such order exists
   *
   * @param orderId unique identifier of existing order
   * @return True if the order was successfully deleted, false otherwise
   */
  boolean deleteOrder(String orderId);

  /**
   * Get the best price for the instrument and side.
   *
   * <p>For buy orders - the highest price For sell orders - the lowest price
   *
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @return the best price, or Optional.empty() if there're no orders for the instrument on this
   *     side
   */
  Optional<Long> getBestPrice(String instrument, Side side);

  /**
   * Get total number of orders for the instrument on given side with given price
   *
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @param price requested price level
   * @return total number of orders, or 0 if there're no orders for the instrument on this side with
   *     this price
   */
  long getOrderNumAtLevel(String instrument, Side side, long price);

  /**
   * Get cumulative quantity of all orders for the instrument on given side with given price
   *
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @param price requested price level
   * @return total quantity, or 0 if there're no orders for the instrument on this side with this
   *     price
   */
  long getTotalQuantityAtLevel(String instrument, Side side, long price);

  /**
   * Get cumulative volume ( sum of price * quantity ) of all orders for the instrument on given
   * side with given price
   *
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @param price requested price level
   * @return total volume, or 0 if there're no orders for the instrument on this side with this
   *     price
   */
  long getTotalVolumeAtLevel(String instrument, Side side, long price);

  /**
   * Get all orders for the instrument on given side with given price
   *
   * <p>Result should contain orders in the same order as they arrive, but also see {@link
   * #modifyOrder( String , long )} for exception
   *
   * @param instrument identifier of an instrument
   * @param side either buy or sell
   * @param price requested price level
   * @return all orders, or empty list if there're no orders for the instrument on this side with
   *     this price
   */
  List<Order> getOrdersAtLevel(String instrument, Side side, long price);
}
