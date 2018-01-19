package ozosync.test;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class StringTest {

    @Test
    public void indexOf() {
        String sku = "123489AU-DG";
        sku = sku.substring(0, sku.indexOf("AU-") + 2);

        System.out.println(sku);

        Assert.assertEquals(sku, "123489AU");
    }

}
