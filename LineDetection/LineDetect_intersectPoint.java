package LineDetection;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Point;
public class LineDetect_intersectPoint {

    public static void main(String[] args) throws IOException {

        BufferedImage input = ImageIO.read(new File("D:\\sobeledge1.png"));
        int width = input.getWidth();
        int height = input.getHeight();
        
        int[][] sobel = new int[width][height];

// find lines using Hough transform
        ArrayList<Line> lines = new ArrayList<Line>();
        
        int r = (int) Math.sqrt(height*height + width*width);
        
        System.out.println("H = " + height);
        System.out.println("W = " + width);
        
        float houghArr[][] = new float [r+1][180];
        double conv = Math.PI/180;
        
        for(int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (sobel[x][y] ==1) {
                    for (int t=0; t<180; t++) {
                        int rho = (int) (x*Math.cos(t*conv) + y*Math.sin(t*conv));
                        if (Math.abs(rho - (x*Math.cos(t*conv) + y*Math.sin(t*conv))) > 0.25) continue;
                        if (rho < 0) continue;
                        houghArr[rho][t] = houghArr[rho][t] + 1;
                    }
                } 
            }
        }

        
        for (int i=0; i<=r; i++) {
            for (int j=0; j<180; j++) {
                System.out.println("rho = " + i + " \t theta = " + j + " \t " + houghArr[i][j]);
            }
        }

// find peaks in Hough space (local maxima)
        for (int d = 0; d < width; d++) {
            for ( int a = 0; r < height; r++) {
                int dx = 0, dy = 0;
                if (d > 0) {
                    dx = -1;
                }
                if (d < width - 1) {
                    dx = 1;
                }
                if (r > 0) {
                    dy = -1;
                }
                if (r < height - 1) {
                    dy = 1;
                }
                if (houghArr[d][a] > houghArr[d + dx][a] && houghArr[d][a] > houghArr[d][a + dy]) {
                    double theta = Math.asin(a / Math.sqrt(a * a + d * d));
                    lines.add(new Line(d, theta));
                }
            }
        }

// group lines by angle

        Collections.sort(lines, new Comparator<Line>() {
            public int compare(Line l1, Line l2) {
                if (l1.theta < l2.theta) {
                    return -1;
                } else if (l1.theta > l2.theta) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        ArrayList<ArrayList<Line>> lineSegments = new ArrayList<ArrayList<Line>>();
        lineSegments.add(new ArrayList<Line>());
//        lineSegments.get(0).add(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            Line currentLine = lines.get(i);
            if (Math.abs(currentLine.theta - lines.get(i - 1).theta) < Math.PI / 180) {
                lineSegments.get(lineSegments.size() - 1).add(currentLine);
            } else {
                lineSegments.add(new ArrayList<Line>());
                lineSegments.get(lineSegments.size() - 1).add(currentLine);
            }
        }

// filter line segments by length
        ArrayList<ArrayList<Line>> longLineSegments = new ArrayList<ArrayList<Line>>();
        for (ArrayList<Line> lineSegment : lineSegments) {
            if (lineSegment.size() > 20) {
                longLineSegments.add(lineSegment);
            }
        }

// find intersection points of line segments
        ArrayList<Point> intersections = new ArrayList<Point>();
        for (int i = 0; i < longLineSegments.size(); i++) {
            for (int j = i + 1; j < longLineSegments.size(); j++) {
                ArrayList<Line> lineSegment1 = longLineSegments.get(i);
                ArrayList<Line> lineSegment2 = longLineSegments.get(j);
                Line l1 = lineSegment1.get(0);
                Line l2 = lineSegment2.get(0);
                double theta1 = l1.theta;
                double theta2 = l2.theta;
                if (Math.abs(theta1 - theta2) < Math.PI / 180) {
                    continue;
                }

                double d1 = l1.d;
                double d2 = l2.d;
                double x = (d1 * Math.sin(theta2) - d2 * Math.sin(theta1))/ Math.sin(theta2 - theta1);
                double y = (-d1 * Math.cos(theta2) + d2 * Math.cos(theta1))/ Math.sin(theta2 - theta1);
                Point p = new Point((int) x, (int) y);

// check if the intersection point is within both line segments
                boolean withinLineSegment1 = false, withinLineSegment2 = false;
                for (Line l : lineSegment1) {
                    double d = l.distanceToPoint(p);
                    if (d < 10) {
                        withinLineSegment1 = true;
                        break;
                    }
                }
                for (Line l : lineSegment2) {
                    double d = l.distanceToPoint(p);
                    if (d < 10) {
                        withinLineSegment2 = true;
                        break;
                    }
                }

                if (withinLineSegment1 && withinLineSegment2) {
                    intersections.add(p);
                }
            }
        }

// find corners of the paper sheet
        Point topLeft = new Point(width, height);
        Point topRight = new Point(0, height);
        Point bottomRight = new Point(0, 0);
        Point bottomLeft = new Point(width, 0);
        for (Point p : intersections) {
            if (p.x < topLeft.x && p.y < topLeft.y) {
                topLeft = p;
            }
            if (p.x > topRight.x && p.y < topRight.y) {
                topRight = p;
            }
            if (p.x > bottomRight.x && p.y > bottomRight.y) {
                bottomRight = p;
            }
            if (p.x < bottomLeft.x && p.y > bottomLeft.y) {
                bottomLeft = p;
            }
        }

// sort corners
        Point[] corners = {topLeft, topRight, bottomRight, bottomLeft};
        Arrays.sort(corners, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                if (p1.x < p2.x) {
                    return -1;
                } else if (p1.x > p2.x) {
                    return 1;
                } else {
                    if (p1.y < p2.y) {
                        return -1;
                    } else if (p1.y > p2.y) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

// calculate edge equations
        Line edge1 = new Line(0, 1);
        Line edge2 = new Line(1, 2);
        Line edge3 = new Line(2, 3);
        Line edge4 = new Line(3, 4);

// crop the image
        BufferedImage output = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (edge1.distanceToPoint(new Point(x, y)) < 10
                        || edge2.distanceToPoint(new Point(x, y)) < 10
                        || edge3.distanceToPoint(new Point(x, y)) < 10
                        || edge4.distanceToPoint(new Point(x, y)) < 10) {
                    output.setRGB(x, y, input.getRGB(x, y));
                } else {
                    output.setRGB(x, y, 0);
                }
            }
        }

// save the output image
        ImageIO.write(output, "jpg", new File("D:\\intersected.png"));
    }
}

class Line {

    public double d, theta;

    public Line(double d, double theta) {
        this.d = d;
        this.theta = theta;
    }
    public Line(Point d, Point theta) {
        
    }

    public double distanceToPoint(Point p) {
        double x = p.x - d * Math.sin(theta);
        double y = p.y + d * Math.cos(theta);
        return Math.sqrt(x * x + y * y);
    }
}
