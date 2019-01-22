import org.apache.pdfbox.pdmodel.*;
import java.io.*;
import org.apache.pdfbox.text.*;
import java.io.IOException;
public class pdfparsing {
    public static void main(String args[]) throws IOException,Exception{
        try{
            File file = new File("Sample Bank Statement Template.pdf");
            openPDFDoc(file,"Password123");
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
        MyPDFTextStripper pdfTextStripper = new MyPDFTextStripper();
        String pageText = pdfTextStripper.getText(document);
        System.out.println(pageText);
    }
}