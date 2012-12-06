/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.steamtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Administrator
 */
public class Command {
    static void execute(String cmd){
        try{
            String s;
//            System.out.println("Executing command: "+cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            System.out.println("Output of command:\n");
            while ((s = stdOutput.readLine()) != null) {
                System.out.println(s);
            }
            
            System.out.println("Error of command:\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            
        }
        catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }
}
