package de.thm.apimanagement.commands;

public interface Command {
    void execute();
    void undo();
}
