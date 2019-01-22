import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.encryption.*;
import org.apache.pdfbox.pdfparser.*;
import java.io.*;
import org.apache.pdfbox.text.*;
import java.io.IOException;
public class bank {
    public static void main(String args[]) throws IOException,Exception{
        try{
            /*File file = new File("Sample Bank Statement Template.pdf");
            PDFParser parser = new PDFParser(new BufferedInputStream(new FileInputStream(file)));
            parser.parse();

            PDDocument originialPdfDoc = parser.getPDDocument();

            boolean isOriginalDocEncrypted = originialPdfDoc.isEncrypted();
            if (isOriginalDocEncrypted) {
                originialPdfDoc.openProtection(new StandardDecryptionMaterial("Password12"));
            }*/
            Customer c1=new Customer("Prajwal Kulkarni","H.No-15-4-194,SBI Colony, Kumbarawada, Bidar","9686919605");
            Customer c2=new Customer("Akarsh mahadi","Banashankari,Bangalore","9741776654");
            Account a1=new Account(201,19856, c1);
            a1.deposit(1000);
            a1.withdraw(500);
        }
        catch(Exception e){

        }
    }
}
