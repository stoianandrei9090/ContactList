package ro.jademy.contactlist.fxcontactlist;

import java.math.BigDecimal;

public class TestClass {
    public static void main(String[] args) {
        String s = "080";
        if(s.matches("[0-9]([0-9])*")) System.out.println("true");
        else System.out.println("false");
    }
}
