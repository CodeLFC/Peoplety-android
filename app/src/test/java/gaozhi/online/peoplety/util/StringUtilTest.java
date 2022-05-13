package gaozhi.online.peoplety.util;

import junit.framework.TestCase;

public class StringUtilTest extends TestCase {
      public void testNumFormat(){
          System.out.println(StringUtil.num2Str(1000));
          System.out.println(StringUtil.num2Str(10000));
      }
}