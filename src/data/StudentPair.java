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
 * This is a superclass for the specific studentPair classes. It defines the
 * attributes of a studentPair and provides methods to work on these.
 *
 * @author Zbyněk Stara
 */
public class StudentPair {
    protected Student student1;
    protected Student student2;

    protected String originalCode; // the name under which this is saved in the trees (the keystring)

    protected StudentPair() {

    }

    protected StudentPair(Student student1, Student student2) throws IllegalArgumentException {
        if (student1.getCode().compareTo(student2.getCode()) <= 0) {
            this.student1 = student1; // keep the alphabetical order
            this.student2 = student2;
        } else {
            this.student1 = student2; // flip them to be alphabetically ordered
            this.student2 = student1;
        }

        if (!this.student1.getCode().equals("<No code>") && !this.student2.getCode().equals("<No code>")) {
            originalCode = this.student1.getCode() + " - " + this.student2.getCode();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * This method allows access to the studentPair's current code.
     *
     * @return the code of this pair
     *
     * @author Zbyněk Stara
     */
    public String getCode() {
        return this.student1.getCode() + " - " + this.student2.getCode();
    }

    /**
     * This method allows access to the studentPair's original code. This is the
     * code under which the student pair is currently in its respective
     * pairTree.
     *
     * @return the value of the originalCode attribute of this pair
     *
     * @author Zbyněk Stara
     */
    public String getOriginalCode() {
        return originalCode;
    }

    /**
     * This returns the student in the student pair that is other than the one
     * provided as a parameter to the method.
     *
     * @return the other student in the pair
     *
     * @author Zbyněk Stara
     */
    public Student getOtherStudent(Student student) {
        if (student.getName().equals(student1.getName())) {
            return student1;
        } else if (student.getName().equals(student2.getName())) {
            return student2;
        } else {
            return null;
        }
    }

    /**
     * This method returns an array of the members of this student pair.
     *
     * @return a two-element array of the members of this student pair
     *
     * @author Zbyněk Stara
     */
    public Student[] getStudentArray() {
        Student[] studentArray = {student1, student2};
        return studentArray;
    }

    /**
     * This method represents the student pair as a string
     *
     * @return a string with the code of the student pair
     *
     * @author Zbyněk Stara
     */
    @Override
    public String toString() {
        return "Code: " + this.getCode();
    }
}
