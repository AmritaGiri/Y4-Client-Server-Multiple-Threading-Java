/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ca1;

import java.util.Objects;

/**
 *
 * @author Amrita Giri
 */
public class Order {

    private String clientId;
    private boolean isBuy;
    private String title;
    private double price;

    public Order(String clientId, boolean isBuy, String title, double price) {
        this.clientId = clientId;
        this.isBuy = isBuy;
        this.title = title;
        this.price = price;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    // Add the getUsername() method to return the clientId
    public String getUsername() {
        return clientId;
    }

    public boolean isIsBuy() {
        return isBuy;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Optionally, you can also override the `toString` method to provide a custom string representation of the Order object.
    @Override
    public String toString() {
        return (isBuy ? "B" : "S") + "," + title + "," + price + "," + clientId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.clientId);
        hash = 59 * hash + (this.isBuy ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.title);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.price) ^ (Double.doubleToLongBits(this.price) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Order other = (Order) obj;
        if (this.isBuy != other.isBuy) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (!Objects.equals(this.clientId, other.clientId)) {
            return false;
        }
        return Objects.equals(this.title, other.title);
    }
}
