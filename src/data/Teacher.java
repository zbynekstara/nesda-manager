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
 * This class takes care of the teacher entries in schools in the database.
 * Teachers are important because they are the judges for the tournaments.
 * Teachers have two attributes: name and school. This class contains methods to
 * access them and change their values.
 *
 * @author Zbyněk Stara
 */
public class Teacher {
    // Attributes:
    private String name = "<No name>";
    private School school = new School();

    // Constructors:
    public Teacher() {

    }
    public Teacher(School school) {
        this.school = school;
    }
    public Teacher(String name, School school) {
        this.name = name;
        this.school = school;
    }

    // Get methods:
    /**
     * This method makes accessible the name attribute.
     *
     * @return the name of the teacher
     *
     * @author Zbyněk Stara
     */
    public String getName() {
        return name;
    }
    /**
     * This method makes accessible the school attribute.
     *
     * @return the school this teacher belongs to
     *
     * @author Zbyněk Stara
     */
    public School getSchool() {
        return school;
    }

    // Set methods:
    /**
     * This method allows for setting up the name of the teacher.
     *
     * @param name name of the teacher as a string
     *
     * @author Zbyněk Stara
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * This method allows for setting up the school of the teacher.
     *
     * @param school school this teacher belongs to
     *
     * @author Zbyněk Stara
     */
    public void setSchool(School school) {
        this.school = school;
    }

    /**
     * This method converts the teacher to string. It returns the teacher's
     * name.
     *
     * @return name of the teacher as a string
     *
     * @author Zbyněk Stara
     */
    @Override
    public String toString() {
        return "Name: " + name;
    }
}
