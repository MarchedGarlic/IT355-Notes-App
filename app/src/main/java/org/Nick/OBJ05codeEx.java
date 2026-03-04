package org.Nick;

public class OBJ05codeEx {
    public static void main(String[] args) {
        //OBJ05-J: Do not return a reference to private mutable class members    
            class obj implements Cloneable {
            int data = 10;
           
            //allows for the ablity to clone the object and return a copy of the object instead of the reference to the original object
            @Override
            protected Object clone() {
                try {
                    return super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError();
                }
            }
        }

        class MutableClass  {
            //private mutable class member
            private obj object= new obj();
            public obj getData() {
                //returning a copy of the object instead of the reference to the original object
                return (obj)object.clone();
            }
        } 
        MutableClass mutable = new MutableClass();
        //accessing a copy of the object instead of the reference to the original object
        System.out.println(mutable.getData().data);
    }
}
