package com.test;

import com.test.api.TestService;
import com.test.api.impl.TestServiceImpl;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        TestService testService = new TestServiceImpl();
       String str = testService.hello("zhangming");
       System.out.println(str);
    }
}
