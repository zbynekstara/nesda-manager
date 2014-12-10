/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

/**
 *
 * @author Zbyněk Stara
 */
public class NESDAFileFilter extends javax.swing.filechooser.FileFilter {
    public NESDAFileFilter () {

    }

    private String getExtension(java.io.File file) {
            String extension = "";
            String fileName = file.getName();
            int i = fileName.lastIndexOf('.');

            if (i > 0 && i < fileName.length() - 1) {
                extension = fileName.substring(i + 1).toLowerCase();
            }
            return extension;
        }

    public String getDescription() {
        return "NESDA Tournament Manager Files";
    }

    public boolean accept(java.io.File file) {
        if (file.isDirectory()) return true;

        String extension = this.getExtension(file);
        if (extension != null) {
            if (extension.equals("nesda")) return true;
            else return false;
        }

        return false;
    }
}
