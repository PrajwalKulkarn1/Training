import org.apache.pdfbox.pdmodel.*;
import java.io.*;
import org.apache.pdfbox.text.*;
import java.io.IOException;
public class pdfparsing {
    public static void main(String args[]) throws IOException,Exception{
        try{
            File file = new File("135582695135_bankStatement_5.pdf");
            openPDFDoc(file,null);
        }
        catch(Exception e){

        }
    }
    private static void openPDFDoc(final File pdfFile,String password) throws Exception {
        PDDocument document;
        if(password!=null){
            document=PDDocument.load(pdfFile, password);
            document.setAllSecurityToBeRemoved(true);
        }
        else{
            document=PDDocument.load(pdfFile);
        }
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        pdfTextStripper.setWordSeparator(";");
        String pageText = pdfTextStripper.getText(document);
        System.out.println(pageText);
    }
}
