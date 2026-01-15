package command;

public interface Order {
    void execute();
    void undo();
    String description();
}
