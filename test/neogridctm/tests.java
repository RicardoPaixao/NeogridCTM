package neogridctm;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Ricardo Paixão
 */
public class tests {

    @Test(expected = IOException.class)
    public void fileNotFound() throws IOException {
        NeogridCTM.main(new String[]{"inexitantInput.txt"});
    }

    @Test(expected = IOException.class)
    public void malformedInput() throws IOException {
        NeogridCTM.main(new String[]{"malformedInput.txt"});
    }

    @Test
    public void basicTest() throws IOException {
        NeogridCTM.main(new String[0]);
        File output = new File("output");
        File expectedOutput = new File("expectedOutput");
        assertTrue(FileUtils.contentEquals(output, expectedOutput));
    }
    
    @Test
    public void variousTalkLengths() throws IOException {
        NeogridCTM.main(new String[]{"inputVariousTalkLengths.txt"});
    }
        
    @Test
    public void bulkData() throws IOException {
        NeogridCTM.main(new String[]{"inputBulkData.txt"});
    }
}
