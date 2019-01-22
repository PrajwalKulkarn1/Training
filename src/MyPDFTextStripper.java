import org.apache.pdfbox.text.PDFTextStripper;
import java.io.IOException;

public class MyPDFTextStripper extends PDFTextStripper{
    public MyPDFTextStripper() throws IOException {
        setWordSeparator(":");
    }
}
