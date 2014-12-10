package unused;

import data.Event;

/**
 *
 * @author Zbyněk Stara
 */
public class EventPair {
    private Event firstEvent;
    private Event secondEvent;

    public EventPair() {

    }

    public EventPair(Event firstEvent, Event secondEvent) {
        this.firstEvent = firstEvent;
        this.secondEvent = secondEvent;

        firstEvent.setEventPair(this);
        secondEvent.setEventPair(this);
    }
}
