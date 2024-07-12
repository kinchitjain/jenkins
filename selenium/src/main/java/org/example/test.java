package org.example;

import java.util.*;

public class test {
  /*  String return10element(List list)
    {
        String element="";
        int n=list.size();
        if(n<10)
            System.out.println("Size is small then 10");
        else
        {
            Iterator s=list.iterator();
            int index=n-10;
            int i=0;
            while(s.hasNext())
            {
                i++;
                if(i==index)
                {   element=s.next().toString();
                    System.out.println(element);
                }

            }

            list.get(n-10);



        }



        return element;
    }


    int countList(List l)
    {
        for(int i=0;l.get(i)!=null;)
        {
            i++;
            System.out.println(i);
        }



    }*/




    public static void main(String s[])
    {
        List<Integer> list=new ArrayList<>();

        list.add(null);
        System.out.println(list.size());
        for(int i=0;list.get(i)!=null;)
        {
            i++;

        }
       // return10element(List list);

    }
}
