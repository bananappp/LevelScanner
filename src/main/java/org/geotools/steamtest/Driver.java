/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.steamtest;

/**
 *
 * @author Administrator
 */
public class Driver {
    public static void main( String[] args ) throws Exception
    {
        Win m = new Win();
        m.setLocationRelativeTo(null);
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        m.setVisible(true);
    }
}
