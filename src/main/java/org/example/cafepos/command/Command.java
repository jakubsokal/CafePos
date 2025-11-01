package org.example.cafepos.command;

public interface Command {
    void execute();
    default void undo() { /* optional */ }
}