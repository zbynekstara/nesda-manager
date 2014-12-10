package unused;

import data.BinarySearchTree;
import data.QualificationEvent;

/**
 *
 * @author Zbyněk Stara
 */
public class DebateEvent extends QualificationEvent {
    public DebateEvent(Type eventType, BinarySearchTree judgeTree, BinarySearchTree entityTree, int judgesPerRoom, int entitiesPerRoom) {
        super(eventType, judgeTree, entityTree, judgesPerRoom, entitiesPerRoom);
    }

    @Override
    public void initializeRoundArray() {
        super.initializeRoundArray();

        super.setRoundArrayElement(2);
    }
}
