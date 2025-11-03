package example.cafepos;

import org.example.cafepos.command.AddItemCommand;
import org.example.cafepos.command.MacroCommand;
import org.example.cafepos.command.PosRemote;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.order.OrderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandTests {

    @Test
    void addItemCommandExecuteAndUndo() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        AddItemCommand cmd = new AddItemCommand(service, "ESP+OAT+SHOT+SYP+L", 1);

        cmd.execute();
        assertEquals(1, order.items().size());

        cmd.undo();
        assertEquals(0, order.items().size());
    }

    @Test
    void macroCommandExecuteAndUndo() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        AddItemCommand cmd1 = new AddItemCommand(service, "ESP+OAT+SHOT+SYP+L", 1);
        AddItemCommand cmd2 = new AddItemCommand(service, "ESP+OAT+SHOT+SYP+L", 2);

        MacroCommand macro = new MacroCommand(cmd1, cmd2);
        macro.execute();

        assertEquals(3, order.items().stream().mapToInt(i -> i.quantity()).sum());

        macro.undo();

        assertEquals(0, order.items().size());
    }

    @Test
    void posRemotePressAndUndo() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        AddItemCommand cmd = new AddItemCommand(service, "ESP+OAT+SHOT+SYP+L", 1);

        PosRemote remote = new PosRemote(1);
        remote.setSlot(0, cmd);
        remote.press(0);

        assertEquals(1, order.items().size());

        remote.undo();

        assertEquals(0, order.items().size());
    }
}

