package com.example.farm_to_table;

import java.util.ArrayList;
import java.util.List;

public class CartManager { private static CartManager instance;
    private List<CartItem> cartItems;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated(int newCount);
    }

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setCartUpdateListener(CartUpdateListener listener) {
        this.listener = listener;
    }

    public void addToCart(CartItem item) {
        boolean itemExists = false;

        // Check if product already exists in cart
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductName().equals(item.getProductName()) &&
                    cartItem.getFarmName().equals(item.getFarmName())) {
                // Increment quantity by 1
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        // If not found, add as a new item
        if (!itemExists) {
            item.setQuantity(1); // Ensure new items start with quantity 1
            cartItems.add(item);
        }

        // Notify listeners about cart update
        if (listener != null) {
            listener.onCartUpdated(getItemCount());
        }
    }

    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void updateQuantity(int position, int quantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(quantity);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}