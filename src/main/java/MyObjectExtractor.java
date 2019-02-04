import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import technology.tabula.*;

import java.io.IOException;

public class MyObjectExtractor extends ObjectExtractor{
    private final PDDocument pdDocument;

    public MyObjectExtractor(PDDocument pdfDocument) {
        super(pdfDocument);
        this.pdDocument = pdfDocument;
    }

    @Override
    protected Page extractPage(Integer pageNumber) throws IOException {
        if (pageNumber <= this.pdDocument.getNumberOfPages() && pageNumber >= 1) {
            PDPage p = this.pdDocument.getPage(pageNumber - 1);
            MyObjectExtractorEngine se = new MyObjectExtractorEngine(p);
            se.processPage(p);
            TextStripper pdfTextStripper = new TextStripper(this.pdDocument, pageNumber);
            pdfTextStripper.setWordSeparator(";");
            pdfTextStripper.process();
            Utils.sort(pdfTextStripper.textElements, Rectangle.ILL_DEFINED_ORDER);
            int pageRotation = p.getRotation();
            float w;
            float h;
            if (Math.abs(pageRotation) != 90 && Math.abs(pageRotation) != 270) {
                w = p.getCropBox().getWidth();
                h = p.getCropBox().getHeight();
            } else {
                w = p.getCropBox().getHeight();
                h = p.getCropBox().getWidth();
            }

            return new Page(0.0F, 0.0F, w, h, pageRotation, pageNumber, p, pdfTextStripper.textElements, se.rulings, pdfTextStripper.minCharWidth, pdfTextStripper.minCharHeight, pdfTextStripper.spatialIndex);
        } else {
            throw new IndexOutOfBoundsException("Page number does not exist");
        }
    }

    public PageIterator extract(Iterable<Integer> pages) {
        return new PageIterator(this, pages);
    }

    public PageIterator extract() {
        return this.extract(Utils.range(1, this.pdDocument.getNumberOfPages() + 1));
    }

    public Page extract(int pageNumber) {
        return this.extract(Utils.range(pageNumber, pageNumber + 1)).next();
    }

    public void close() throws IOException {
        this.pdDocument.close();
    }
}
