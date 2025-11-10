package example.cafepos;

import org.example.cafepos.state.OrderFSM;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderStateTests {

    @Test
    void happy_path_to_delivered() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        fsm.pay();
        assertEquals("PREPARING", fsm.status());
        fsm.markReady();
        assertEquals("READY", fsm.status());
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void cannot_prepare_before_pay() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());
        fsm.prepare(); // stays NEW
        assertEquals("NEW", fsm.status());
    }

    @Test
    void can_cancel_from_new_or_preparing() {
        OrderFSM fsm = new OrderFSM();
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());

        fsm = new OrderFSM();
        fsm.pay();         // PREPARING
        fsm.cancel();      // -> CANCELLED
        assertEquals("CANCELLED", fsm.status());
    }
}
