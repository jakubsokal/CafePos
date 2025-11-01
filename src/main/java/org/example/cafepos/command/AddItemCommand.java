package org.example.cafepos.command;

import org.example.cafepos.order.OrderService;

public final class AddItemCommand implements Command {
    private final OrderService service;
    private final String recipe;
    private final int qty;
    public AddItemCommand(OrderService service, String recipe, int qty) {
        this.service = service; this.recipe = recipe; this.qty = qty;
    }
    @Override public void execute() { service.addItem(recipe, qty); }
    @Override public void undo() { service.removeLastItem(); }
}
