import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import technology.tabula.Ruling;
import technology.tabula.Utils;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyObjectExtractorEngine extends PDFGraphicsStreamEngine {
    private static final String NBSP = " ";
    protected List<Ruling> rulings = new ArrayList();
    private AffineTransform pageTransform = null;
    private boolean debugClippingPaths;
    private boolean extractRulingLines = true;
    private Logger log = LoggerFactory.getLogger(MyObjectExtractorEngine.class);
    private int clipWindingRule = -1;
    private GeneralPath currentPath = new GeneralPath();
    public List<Shape> clippingPaths;

    protected MyObjectExtractorEngine(PDPage page) {
        super(page);
        PDRectangle cb = this.getPage().getCropBox();
        int rotation = this.getPage().getRotation();
        this.pageTransform = new AffineTransform();
        if (Math.abs(rotation) != 90 && Math.abs(rotation) != 270) {
            this.pageTransform.concatenate(AffineTransform.getTranslateInstance(0.0D, (double)cb.getHeight()));
            this.pageTransform.concatenate(AffineTransform.getScaleInstance(1.0D, -1.0D));
        } else {
            this.pageTransform = AffineTransform.getRotateInstance((double)rotation * 0.017453292519943295D, 0.0D, 0.0D);
            this.pageTransform.concatenate(AffineTransform.getScaleInstance(1.0D, -1.0D));
        }

        this.pageTransform.translate((double)(-cb.getLowerLeftX()), (double)(-cb.getLowerLeftY()));
    }

    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        this.currentPath.moveTo((float)p0.getX(), (float)p0.getY());
        this.currentPath.lineTo((float)p1.getX(), (float)p1.getY());
        this.currentPath.lineTo((float)p2.getX(), (float)p2.getY());
        this.currentPath.lineTo((float)p3.getX(), (float)p3.getY());
        this.currentPath.closePath();
    }

    public void clip(int windingRule) {
        this.clipWindingRule = windingRule;
    }

    public void closePath() {
        this.currentPath.closePath();
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.currentPath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    public void drawImage(PDImage arg0) {
    }

    public void endPath() {
        if (this.clipWindingRule != -1) {
            this.currentPath.setWindingRule(this.clipWindingRule);
            this.getGraphicsState().intersectClippingPath(this.currentPath);
            this.clipWindingRule = -1;
        }

        this.currentPath.reset();
    }

    public void fillAndStrokePath(int arg0) {
        this.strokeOrFillPath(true);
    }

    public void fillPath(int arg0) {
        this.strokeOrFillPath(true);
    }

    public Point2D getCurrentPoint() {
        return this.currentPath.getCurrentPoint();
    }

    public void lineTo(float x, float y) {
        this.currentPath.lineTo(x, y);
    }

    public void moveTo(float x, float y) {
        this.currentPath.moveTo(x, y);
    }

    public void shadingFill(COSName arg0) {
    }

    public void strokePath() {
        this.strokeOrFillPath(false);
    }

    private void strokeOrFillPath(boolean isFill) {
        GeneralPath path = this.currentPath;
        if (!this.extractRulingLines) {
            this.currentPath.reset();
        } else {
            PathIterator pi = path.getPathIterator(this.getPageTransform());
            float[] c = new float[6];
            if (pi.currentSegment(c) != 0) {
                path.reset();
            } else {
                pi.next();

                int currentSegment;
                while(!pi.isDone()) {
                    currentSegment = pi.currentSegment(c);
                    if (currentSegment != 1 && currentSegment != 4 && currentSegment != 0) {
                        path.reset();
                        return;
                    }

                    pi.next();
                }

                float[] first = new float[6];
                pi = path.getPathIterator(this.getPageTransform());
                pi.currentSegment(first);
                Point2D.Float start_pos = new Point2D.Float(Utils.round((double)first[0], 2), Utils.round((double)first[1], 2));
                Point2D.Float last_move = start_pos;
                Point2D.Float end_pos = null;
                MyObjectExtractorEngine.PointComparator pc = new MyObjectExtractorEngine.PointComparator();

                while(!pi.isDone()) {
                    pi.next();

                    try {
                        currentSegment = pi.currentSegment(c);
                    } catch (IndexOutOfBoundsException var13) {
                        continue;
                    }

                    java.awt.geom.Line2D.Float line;
                    Ruling r;
                    switch(currentSegment) {
                        case 0:
                            last_move = new Point2D.Float(c[0], c[1]);
                            end_pos = last_move;
                            break;
                        case 1:
                            end_pos = new Point2D.Float(c[0], c[1]);
                            if (start_pos != null && end_pos != null) {
                                line = pc.compare((Point2D)start_pos, (Point2D)end_pos) == -1 ? new java.awt.geom.Line2D.Float(start_pos, end_pos) : new java.awt.geom.Line2D.Float(end_pos, start_pos);
                                if (line.intersects(this.currentClippingPath())) {
                                    r = (new Ruling(line.getP1(), line.getP2())).intersect(this.currentClippingPath());
                                    if (r.length() > 0.01D) {
                                        this.rulings.add(r);
                                    }
                                }
                            }
                        case 2:
                        case 3:
                        default:
                            break;
                        case 4:
                            if (start_pos != null && end_pos != null) {
                                line = pc.compare((Point2D)end_pos, (Point2D)last_move) == -1 ? new java.awt.geom.Line2D.Float(end_pos, last_move) : new java.awt.geom.Line2D.Float(last_move, end_pos);
                                if (line.intersects(this.currentClippingPath())) {
                                    r = (new Ruling(line.getP1(), line.getP2())).intersect(this.currentClippingPath());
                                    if (r.length() > 0.01D) {
                                        this.rulings.add(r);
                                    }
                                }
                            }
                    }

                    start_pos = end_pos;
                }

                path.reset();
            }
        }
    }

    public AffineTransform getPageTransform() {
        return this.pageTransform;
    }

    public Rectangle2D currentClippingPath() {
        Shape clippingPath = this.getGraphicsState().getCurrentClippingPath();
        Shape transformedClippingPath = this.getPageTransform().createTransformedShape(clippingPath);
        return transformedClippingPath.getBounds2D();
    }

    public boolean isDebugClippingPaths() {
        return this.debugClippingPaths;
    }

    public void setDebugClippingPaths(boolean debugClippingPaths) {
        this.debugClippingPaths = debugClippingPaths;
    }

    class PointComparator implements Comparator<Point2D> {
        PointComparator() {
        }

        public int compare(Point2D o1, Point2D o2) {
            float o1X = Utils.round(o1.getX(), 2);
            float o1Y = Utils.round(o1.getY(), 2);
            float o2X = Utils.round(o2.getX(), 2);
            float o2Y = Utils.round(o2.getY(), 2);
            if (o1Y > o2Y) {
                return 1;
            } else if (o1Y < o2Y) {
                return -1;
            } else if (o1X > o2X) {
                return 1;
            } else {
                return o1X < o2X ? -1 : 0;
            }
        }
    }
}
