/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.steamtest;
import java.lang.reflect.*;
import java.util.Arrays;
/**
 *
 * @author Administrator
 */

class ModifierList {
    public final int PUBLIC           = 0x00000001;
    public final int PRIVATE          = 0x00000002;
    public final int PROTECTED        = 0x00000004;
    public final int STATIC           = 0x00000008;
    public final int FINAL            = 0x00000010;
    public final int SYNCHRONIZED     = 0x00000020;
    public final int VOLATILE         = 0x00000040;
    public final int TRANSIENT        = 0x00000080;
    public final int NATIVE           = 0x00000100;
    public final int INTERFACE        = 0x00000200;
    public final int ABSTRACT         = 0x00000400;
    public final int STRICT           = 0x00000800;
}

public class Accessor {
    private static boolean rFilterOn = false;
    private static boolean mFilterOn = false;
    private static int[] modFilter;
    private static Class retFilter;
    
    public static final ModifierList MODIFIERS = new ModifierList();
    
    private static final java.util.Map<Class, Class> convert = new java.util.HashMap();
    static {
        convert.put(Boolean.class, boolean.class);
        convert.put(Byte.class, byte.class);
        convert.put(Short.class, short.class);
        convert.put(Character.class, char.class);
        convert.put(Integer.class, int.class);
        convert.put(Long.class, long.class);
        convert.put(Float.class, float.class);
        convert.put(Double.class, double.class);
    }
    
    public static void disableFilters(){
        rFilterOn = false;
        mFilterOn = false;
    }
    
    public static void filterByReturnType(Class clazz) {
        if(clazz == null){
            rFilterOn = false;
        }
        else {
            rFilterOn = true;
            retFilter = clazz;
        }
    }
    
    public static void filterByModifier(int...allowedMods) {
        mFilterOn = true;
        modFilter = allowedMods;
    }
    
    public static void displayMethods(Class s){
      Method[] m = s.getDeclaredMethods();
      for(int i=0; i<m.length; i++){
          m[i].setAccessible(true);
          boolean match = true;
          if(rFilterOn){
              if(retFilter != m[i].getReturnType()){
                  match = false;
              }
          }
          if(match && mFilterOn){
              int mod = m[i].getModifiers();
              for(int c:modFilter) {
                  if((mod & c) != 0){
                      break;
                  }
                  match = false;
              }
          }
          if(match) {
              System.out.print(Modifier.toString(m[i].getModifiers())+ " " + m[i].getReturnType().getName() + " " + m[i].getName() + "(");
              if(m[i].getParameterTypes().length > 0){
                  System.out.print(m[i].getParameterTypes()[0].getName());
                  for(int d=1; d< m[i].getParameterTypes().length; d++){
                      System.out.print(", "+m[i].getParameterTypes()[d].getName());
                  }
              }
              System.out.println(")");
          }
      }
  }
  
    public static void displayFields(Object s) throws Exception {
        /*
         * displayFields(Object s): used to display all fields of an instance at their current values.
         */
        Field[] f = s.getClass().getDeclaredFields();
      for(int i=0; i<f.length; i++){
          f[i].setAccessible(true);
          boolean match = true;
          if(rFilterOn){
              if(retFilter != f[i].getType()){
                  match = false;
              }
          }
          if(match && mFilterOn){
              int mod = f[i].getModifiers();
              for(int c:modFilter) {
                  if((mod & c) != 0){
                      break;
                  }
                  match = false;
              }
          }
          if(match){
              System.out.print(Modifier.toString(f[i].getModifiers()) + " " + f[i].getType().getName() + " " + f[i].getName());
              if(f[i].get(s) != null){
                  System.out.print(" = " + f[i].get(s).toString());
              }
              System.out.println(";");
          }
      }
    }
    
  public static void displayFields(Class s) throws Exception{
      /*
       * displayFields(Class s): used to display all fields of a class 's' at their default values and all static fields at their current values.
       */
      Field[] f = s.getDeclaredFields();
      for(int i=0; i<f.length; i++){
          f[i].setAccessible(true);
          System.out.print(Modifier.toString(f[i].getModifiers()) + " " + f[i].getType().getName() + " " + f[i].getName());
          try{
              if(f[i].get(s) != null){
                  System.out.print(" = " + f[i].get(s).toString());
              }
          }
          catch(Exception e){
              
          }
          System.out.println(";");
      }
  }
  public static String[] getFields(Class s) throws Exception {
      Field[] f = s.getDeclaredFields();
      String[] ret = new String[f.length];
      for(int i=0; i<f.length; i++){
          f[i].setAccessible(true);
          boolean match = true;
          if(rFilterOn){
              if(retFilter != f[i].getType()){
                  match = false;
              }
          }
          if(match && mFilterOn){
              int mod = f[i].getModifiers();
              for(int c:modFilter) {
                  if((mod & c) != 0){
                      break;
                  }
                  match = false;
              }
          }
          if(match){
              ret[i] = (Modifier.toString(f[i].getModifiers()) + " " + f[i].getType().getName() + " " + f[i].getName());
              try{
                  if(f[i].get(s) != null){
                      ret[i] += " = " + f[i].get(s).toString();
                  }
              }
              catch(Exception e){

              }
              ret[i] += ";";
          }
      }
      return ret;
  }
  public static void setStaticFinal(Class s, String name, Object value) throws Exception {
      Field f = s.getDeclaredField(name);
      f.setAccessible(true);
      
      Field mods = Field.class.getDeclaredField("modifiers");
      mods.setAccessible(true);
      
      mods.setInt(f, f.getModifiers() & ~Modifier.FINAL);
      f.set(null, value);
  }
  public static void setField(Object s, String name, Object value) throws Exception {
      try{
          Field f = s.getClass().getDeclaredField(name);
          f.setAccessible(true);
          f.set(s, value);
      }
      catch(IllegalAccessException e) {
          throw new IllegalAccessException("Unable to modify field - it is probably a static final field. Try using setStaticFinal() instead.");
      }
  }
  public static Object getFieldValue(Object instance, String fName) throws Exception{
      Field f;
      if(instance instanceof Class){
          f = ((Class)instance).getDeclaredField(fName);
      }
      else{
          f = instance.getClass().getDeclaredField(fName);
      }
      f.setAccessible(true);
      return f.get(instance);
  }
  public static <T> T callMethod(Object instance, String mName, Object...params) throws Exception {
      Class[] paramTypes = new Class[params.length];
      Class[] tmp = null;
      int pos = 0;
      for(Object i:params) {
          paramTypes[pos++] = i.getClass();
      }
      System.out.println(Arrays.toString(paramTypes));
      Method[] possible = instance.getClass().getDeclaredMethods();
      Method m;
      boolean found = false;
      int i = 0;
      while(!found) {
          if(i == possible.length) {
              throw new Exception("No method with matching parameters found.");
          }
          if(possible[i].getName().equals(mName) && (possible[i].getParameterTypes().length == paramTypes.length)) {
              tmp = paramTypes;
              System.out.println("Checking if paramters match "+Arrays.toString(possible[i].getParameterTypes()));
              if((!parametersMatch(possible[i].getParameterTypes(), paramTypes))){
                  for(int d=0; d<paramTypes.length; d++){
                      if(convert.containsKey(paramTypes[d]) && convert.get(paramTypes[d]).equals(possible[i].getParameterTypes()[d])) {
                          System.out.println("Replacing "+paramTypes[d] + " with "+convert.get(paramTypes[d]));
                          tmp[d] = convert.get(paramTypes[d]);
                      }
                  }
              }
              if(Arrays.equals(tmp, (possible[i]).getParameterTypes())){
                  System.out.println("They match!");
                  paramTypes = tmp;
                  found = true;
              }
          }
          i++;
      }
      m = instance.getClass().getDeclaredMethod(mName, paramTypes);
      m.setAccessible(true);
      return (T)m.invoke(instance, params);
  }
  private static boolean parametersMatch(Class[] a, Class[] b){
      if(Arrays.equals(a, b)) { return true; }
      else{
          return false;
      }
  }
}
