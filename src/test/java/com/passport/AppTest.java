package com.passport;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        String regEx="[^。！]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher("asdf！");
        System.out.println(m.matches());
    }
}
