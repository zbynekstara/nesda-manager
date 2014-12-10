/**
 * NESDA Tournament Manager 2013
 *
 * By Zbyněk Stara, 000889-045
 * International School of Prague
 * Czech Republic
 *
 * Computer used:
 * Macbook unibody aluminum late 2008
 * Mac OS X 10.6.8
 * 8 GiB of RAM
 *
 * IDE used:
 * NetBeans 6.9.1
 */

package data;

/**
 * This is a specific extension of the StudentPair class, intended for use for
 * debate student pairs – for that end, it contains a specific constructor.
 *
 * @author Zbyněk Stara
 */
public class DebateStudentPair extends StudentPair {
    public DebateStudentPair() {

    }

    public DebateStudentPair(Student student1, Student student2) throws IllegalArgumentException {
        super(student1, student2);

        if (super.student1.getDebateStudentPair() == null && super.student2.getDebateStudentPair() == null) {
            if (super.student1 == student1) {
                super.student1.setDebateStudentPair(this);
                super.student2.setDebateStudentPair(this);
            } else {
                super.student1.setDebateStudentPair(this);
                super.student2.setDebateStudentPair(this);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
