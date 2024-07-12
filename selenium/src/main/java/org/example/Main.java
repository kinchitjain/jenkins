package org.example;

import java.util.HashMap;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.


       Map<String,Map<String,Integer>> m=new HashMap<>();
       Map<String,Integer> i=new HashMap<>();
       i.put("C",1);
       //m.put("A",1);
      // m.put("B",i.put("C",2));
      m.put("B",i);


       for(String s: m.keySet())
       {
           System.out.println( m.get("B"));
       }





    }
}