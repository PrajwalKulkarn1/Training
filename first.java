import
public class first{
public static void main(String args[]) throws{
    try{
        PDDocument pdDocument=new PDDocument();
        PDPage pdPage=new PDPage();
        pdDocument.addPage(pdPage);
        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(25, 500);
        String text = "This is the sample document and we are adding content to it.";
        contentStream.showText(text);
        contentStream.close();
        pdDocument.save("Desktop/Training/");
        pdDocument.close();
        Customer c1=new Customer(1,"Prajwal Kulkarni","H.No-15-4-194,SBI Colony, Kumbarawada, Bidar","9686919605");
        Account a1=new Account(201,19856,1);
        a1.deposit(1000);
        a1.withdraw(500);
    }
    catch(Exception e){

    }
}
}
