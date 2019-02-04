import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.ObjectExtractor;
import technology.tabula.Rectangle;
import technology.tabula.PageIterator;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.writers.CSVWriter;

import java.io.*;
import java.util.*;

public class BankStatementPdfParser {
    static final String DB_URL = "jdbc:mysql://localhost/moneyview";
    static String HDFCDateFormat = "[\\d]{2}/[\\d]{2}/[\\d]{2}";
    static String SBILine2Format = "[\\d]{4}";
    static String SBIDateFormat1 = "[\\d]+[\\s][\\w]{3}[\\s][\\d]{4}";
    static String SBIDateFormat2 = "[\\d]+[\\s][\\w]{3}";

    public static void main(String args[]) throws Exception {

        String string = getTableContent("106298684136_bankStatement_0.pdf");
        String[] tableDataArray = string.split("\\r?\\n");
        int lineLength = tableDataArray[0].split(",").length;
        HashMap<Integer, List<String>> transactions = new HashMap<Integer, List<String>>();
        List<String> columnHeaders = new ArrayList<String>();

        for(int i=0; i<tableDataArray.length; i++) {
            if(tableDataArray[i].matches(".*[\"].*")){
                String[] strings=tableDataArray[i].split("\"");
                String finalString= "";
                for(int in=0;in<strings.length;in++){
                    if(in%2!=0){
                        strings[in] = strings[in].replaceAll(",","");
                    }
                    finalString+=strings[in];
                }
                tableDataArray[i]=finalString;
            }
        }


        int i=0,transactionNumber=0;
        while(i<tableDataArray.length && !(tableDataArray[i].split(",")[0].trim().matches(HDFCDateFormat))){
            i++;
        }
        for(int j=i;j<tableDataArray.length;j++){
            String[] currentLine = tableDataArray[j].split(",");

            while(j+1<tableDataArray.length && !(tableDataArray[j+1].split(",")[0].trim().matches(HDFCDateFormat))&& (tableDataArray[j+1].split(",")[0].trim().equals(""))){
                String[] nextLine = tableDataArray[j+1].split(",");
                if(count(tableDataArray[j+1], ',') == lineLength-1){
                    for(int in=0;in< nextLine.length;in++){
                        currentLine[in] = currentLine[in] + nextLine[in];
                    }
                }
                j++;
            }
            if(j+1<tableDataArray.length && !(tableDataArray[j+1].split(",")[0].trim().matches(HDFCDateFormat))){
                j++;
            }
            List<String> transactionDetails = Arrays.asList(currentLine);
            transactions.put(transactionNumber++, transactionDetails);
        }

        JDBCConnectivity jdbcConnectivity = new JDBCConnectivity("root","root");
        jdbcConnectivity.insertHdfc(transactions, lineLength);
        String text = getMetaContent("138044195860_bankStatement_10.pdf");
        System.out.println(transactions);
    }

    public static String getTableContent(String filePath) throws IOException {
        PDDocument pdfDocument = PDDocument.load(new File(filePath));
        ObjectExtractor extractor = new ObjectExtractor(pdfDocument);

        Map<Integer, List<Rectangle>> detectedTables = new HashMap<Integer, List<Rectangle>>();

        NurminenDetectionAlgorithm detectionAlgorithm = new NurminenDetectionAlgorithm();

        PageIterator pages = extractor.extract();
        while (pages.hasNext()) {
            Page page = pages.next();
            List<Rectangle> tablesOnPage = detectionAlgorithm.detect(page);
            if (tablesOnPage.size() > 0) {
                detectedTables.put(new Integer(page.getPageNumber()), tablesOnPage);
            }
        }

        StringBuilder sb = new StringBuilder();
        List<Table> tablesList = new ArrayList<Table>();

        for (Integer pageNo : detectedTables.keySet()) {
            List<Rectangle> detectedPageTables = detectedTables.get(pageNo);
            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
            Page page;

            if (detectedPageTables != null) {
                for(Rectangle r : detectedPageTables) {
                    page = getAreaFromPage(filePath, pageNo, r.getTop(), r.getLeft(), r.getBottom()-1, r.getRight());
                    List<Table> tmpTablesList = bea.extract(page);
                    if (tmpTablesList != null && tmpTablesList.size() > 0) {
                        for (Table table : tmpTablesList) {
                            tablesList.add(table);
                        }
                    }
                }
            }
        }

        if(tablesList != null && tablesList.size() > 0)
            (new CSVWriter()).write(sb, tablesList);

        String s = sb.toString();
        return s;
    }

    public static boolean isEmpty(String str) {
        if (str == null) return true;
        return "".equals(str.trim());
    }

    public static Page getAreaFromPage(String path, int page, float top, float left, float bottom, float right) throws IOException {
        return getPage(path, page).getArea(top, left, bottom, right);
    }

    public static Page getPage(String path, int pageNumber) throws IOException {
        ObjectExtractor oe = null;
        try {
            PDDocument document = PDDocument
                    .load(new File(path));
            oe = new ObjectExtractor(document);
            Page page = oe.extract(pageNumber);
            return page;
        } finally {
            if (oe != null)
                oe.close();
        }
    }

    public static int count(String string,char ch){
        int count=0;
        for(int i=0;i<string.length();i++){
            if(string.charAt(i)==ch)
                count++;
        }
        return count;
    }

    public static String getMetaContent(String fileName)throws Exception{
        PDDocument pdfDocument = PDDocument.load(new File(fileName));
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        pdfTextStripper.setWordSeparator(";");
        String text = pdfTextStripper.getText(pdfDocument);
        return text;
    }

    public static String getName(String text){
        String[] textlist = text.split("\\r?\\n");
        String name = "";
        String AccountNumber = "";
        String IfscCode = "";
        String Address = "";
        String From = "";
        String To = "";

        if(text.matches(".*Account Name.*")||text.matches(".*Account Number.*"));{
            int i;
            for(i=0;i<textlist.length;i++){
                if(textlist[i].matches(".*Account Name.*")){
                    textlist[i] = textlist[i].replaceAll("[\\s]+"," ");
                    name = textlist[i].substring("Account Name : ".length());
                }
                else if(textlist[i].matches(".*Account Number.*")){
                    textlist[i]=textlist[i].replaceAll("[\\s]+"," ");
                    AccountNumber = textlist[i].substring("Account Number : ".length());
                }
                else if(textlist[i].matches(".*IFS[\\w]*.*")){
                    textlist[i]=textlist[i].replaceAll("[\\s]+"," ");
                    IfscCode = textlist[i].substring("IFS Code : ".length());
                }
                else if(textlist[i].matches(".*Address.*")){
                    textlist[i]=textlist[i].replaceAll("[\\s]+"," ");
                    Address = textlist[i].substring("Address : ".length());
                    while(!textlist[i+1].matches(".*:.*")){
                        Address += " " + textlist[i+1].replaceAll("[\\s]+","");
                        i++;
                    }
                }
                else if(textlist[i].matches(".*from.*")){
                    textlist[i]=textlist[i].replaceAll("[\\s]+"," ");
                    From = textlist[i].substring(textlist[i].indexOf("from ")+"from ".length(),textlist[i].indexOf("from ")+"from ".length()+11);
                    To = textlist[i].substring(textlist[i].indexOf("to ")+"to ".length());
                }
            }
        }
        System.out.println(name+" "+Address+" "+AccountNumber+" "+IfscCode+" "+From+" "+To);
        return name;
    }

}

