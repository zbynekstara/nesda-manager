package unused;

import data.BinarySearchTree;
import data.Event;
import data.Qualification;

/**
 *
 * @author Zbyněk Stara
 */
public class FinalEvent extends Event {
    public FinalEvent(Qualification qualification, Type eventType, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) {
        super(eventType, false, true, judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
    }

    public void initializeRoundArray() {
        super.setRoundArrayElement(0);
    }
}
