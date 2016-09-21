package app.popularmovies;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class MoviesJsonParserTest {

    static String sampleJson;

    @BeforeClass
    public static void loadSampleData() throws IOException {

        InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("sample.json");
        assertNotNull(inputStream);

        /*
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        sampleJson = s.hasNext() ? s.next() : null;
        */

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        sampleJson = result.toString("UTF-8");

        //System.out.println(sampleJson);

        assertNotNull(sampleJson);
    }

    @Test
    public void testParsing() throws Exception {


    }
}