package unused;

import data.BinarySearchTree;
import data.Event;
import data.Qualification;

/**
 *
 * @author Zbyněk Stara
 */
public class SemifinalEvent extends Event {
    public SemifinalEvent(Qualification qualification, Type eventType, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) {
        super(eventType, true, false, judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
    }

    public void initializeRoundArray() {
        super.setRoundArrayElement(0);
    }
}
