package com.cfbenchmarks.interview;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class Order {

  /** unique identifier for the order */
  private String orderId;

  /** identifier of an instrument */
  private String instrument;

  /** either buy or sell */
  private Side side;

  /** limit price for the order, always positive */
  private long price;

  /** required quantity, always positive */
  private long quantity;

  public static class OrderBuilder {
    // Probably should be using UUID here
    private String orderId;
    private String instrument;
    private Side side;
    private long price;
    private long quantity;

    /**
     * All-values ctor for builder. The use of a builder here allows for future optional params
     * without ridiculous telescoping of constructors.
     *
     * @param orderId unique identifier for the order
     * @param instrument identifier of an instrument
     * @param side either buy or sell
     * @param price limit price for the order, always positive
     * @param quantity required quantity, always positive
     */

    public OrderBuilder(String orderId, String instrument, Side side, long price, long quantity){
      requireNonNull(orderId);
      requireNonNull(instrument);
      checkArgument(price > 0, "Price must be positive");
      checkArgument(quantity > 0, "Quantity must be positive");
      this.orderId = orderId;
      this.instrument = instrument;
      this.side = side;
      this.price = price;
      this.quantity = quantity;
    }

    public OrderBuilder orderId(String val) { orderId = val; return this; }
    public OrderBuilder instrument(String val) { instrument = val; return this; }
    public OrderBuilder side(Side val) { side = val; return this; }
    public OrderBuilder price(long val) { price = val; return this; }
    public OrderBuilder quantity(long val) { quantity = val; return this; }

    public Order build() {
      return new Order(this);
    }
  }

  private Order(OrderBuilder builder) {
    this.orderId = builder.orderId;
    this.instrument = builder.instrument;
    this.side = builder.side;
    this.price = builder.price;
    this.quantity = builder.quantity;
  }

  /**
   * Copying ctor
   *
   * @param order an order to make copy from
   */
  public Order(Order order) {
    new OrderBuilder(order.orderId, order.instrument, order.side, order.price, order.quantity).build();
  }

  public String getOrderId() {
    return orderId;
  }
  public String getInstrument() {
    return instrument;
  }
  public Side getSide() {
    return side;
  }
  public long getPrice() {
    return price;
  }
  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    checkArgument(quantity > 0, "quantity must be positive");
    this.quantity = quantity;
  }

 //POJO value methods

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    if (price != order.price) return false;
    if (quantity != order.quantity) return false;
    if (orderId != null ? !orderId.equals(order.orderId) : order.orderId != null) return false;
    if (instrument != null ? !instrument.equals(order.instrument) : order.instrument != null)
      return false;
    return side == order.side;
  }

  @Override
  public int hashCode() {
    int result = orderId != null ? orderId.hashCode() : 0;
    result = 31 * result + (instrument != null ? instrument.hashCode() : 0);
    result = 31 * result + (side != null ? side.hashCode() : 0);
    result = 31 * result + (int) (price ^ (price >>> 32));
    result = 31 * result + (int) (quantity ^ (quantity >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Order{"
        + "orderId='"
        + orderId
        + '\''
        + ", instrument='"
        + instrument
        + '\''
        + ", side="
        + side
        + ", price="
        + price
        + ", quantity="
        + quantity
        + '}';
  }
}
