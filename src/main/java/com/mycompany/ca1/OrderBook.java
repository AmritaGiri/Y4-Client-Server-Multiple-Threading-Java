/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ca1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {

    private final Map<String, TreeMap<Double, List<Order>>> buyOrders;
    private final Map<String, TreeMap<Double, List<Order>>> sellOrders;

/*  The use of TreeMap ensures that the 
    orders are sorted by price, which is 
    useful for finding matching orders 
    when processing orders 
    received from clients. 
*/
    public OrderBook() {
        buyOrders = new HashMap<>();
        sellOrders = new HashMap<>();
    }

    public void addOrder(Order order) {
        /**
         *  The addOrder method is called when a new order 
         * is received from a client, and adds the order 
         * to the appropriate TreeMap based on its type, 
         * security title, and price.
         */
        Map<String, TreeMap<Double, List<Order>>> orders = order.isBuy() ? buyOrders : sellOrders;
        orders.putIfAbsent(order.getTitle(), new TreeMap<>(order.isBuy() ? Collections.reverseOrder() : Comparator.naturalOrder()));
        TreeMap<Double, List<Order>> title = orders.get(order.getTitle());

        title.putIfAbsent(order.getPrice(), new ArrayList<>());
        List<Order> priceOrders = title.get(order.getPrice());
        priceOrders.add(order);
    }

    public Order findMatchingOrder(Order order) {
        
    /**
     * The findMatchingOrder method is likely called 
     * by the handleOrder method to find a matching 
     * order for a newly received order from a client. 
     * The method searches the appropriate TreeMap of 
     * orders for the given security title and returns 
     * the first order in the list with a price that 
     * is compatible with the price of the given order.
     */
        Map<String, TreeMap<Double, List<Order>>> orders = order.isBuy() ? sellOrders : buyOrders;
        if (!orders.containsKey(order.getTitle())) {
            return null;
        }

        TreeMap<Double, List<Order>> title = orders.get(order.getTitle());
        for (Map.Entry<Double, List<Order>> entry : title.entrySet()) {
            if (order.isBuy() ? order.getPrice() >= entry.getKey() : order.getPrice() <= entry.getKey()) {
                Order matchedOrder = entry.getValue().remove(0);
                if (entry.getValue().isEmpty()) {
                    title.remove(entry.getKey());
                }
                return matchedOrder;
            }
        }
        return null;
    }

    public boolean cancelOrder(Order order) {
    /**
     * The cancelOrder method is likely called by the handleCancel 
     * method to cancel a specific order received from a client. 
     * The method searches the appropriate TreeMap of orders 
     * for the given security title and price, removes the 
     * specified order from the List, and removes the 
     * entire entry from the TreeMap if the List 
     * becomes empty.
     */
        Map<String, TreeMap<Double, List<Order>>> orders = order.isBuy() ? buyOrders : sellOrders;
        TreeMap<Double, List<Order>> titleOrders = orders.get(order.getTitle());

        if (titleOrders != null) {
            List<Order> priceOrders = titleOrders.get(order.getPrice());
            if (priceOrders != null) {
                boolean removed = priceOrders.removeIf(o -> o.getUsername().equals(order.getUsername()));
                if (removed && priceOrders.isEmpty()) {
                    titleOrders.remove(order.getPrice());
                }
                return removed;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Buy Orders:\n");
        for (Map.Entry<String, TreeMap<Double, List<Order>>> entry : buyOrders.entrySet()) {
            for (Map.Entry<Double, List<Order>> orderEntry : entry.getValue().entrySet()) {
                for (Order order : orderEntry.getValue()) {
                    result.append(order).append("\n");
                }
            }
        }
        result.append("Sell Orders:\n");
        for (Map.Entry<String, TreeMap<Double, List<Order>>> entry : sellOrders.entrySet()) {
            for (Map.Entry<Double, List<Order>> orderEntry : entry.getValue().entrySet()) {
                for (Order order : orderEntry.getValue()) {
                    result.append(order).append("\n");
                }
            }
        }
        return result.toString();
    }
}
    