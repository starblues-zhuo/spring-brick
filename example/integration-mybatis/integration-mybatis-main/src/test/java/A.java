import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
public class A {

    @Test
    public void aa(){
        String path = "//instances/8b08af850fd9/details";
        int startOffset = (path.startsWith("/") ? 1 : 0);
        int endOffset = path.indexOf('/', 1);
        if (endOffset != -1) {
            String webjar = path.substring(startOffset, endOffset);
            String partialPath = path.substring(endOffset + 1);
            System.out.println(webjar);
            System.out.println(partialPath);
        }

        System.out.println(path.startsWith("/"));

    }


}
