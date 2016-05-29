package utils;
import com.itextpdf.text.DocListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import monitor.model.PCInfo;
import monitor.model.PCInfoViewWrapper;
import monitor.model.Program;
import monitor.model.ProgramViewWrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileNotFoundException;
public class TestPDF {
	private static final Font H1 = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
	private static final Font H2 = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
	private static final Font SUBTITLE = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC);
	private static final Font STD_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	public static void main(String[] args) {

        Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                new FileOutputStream("HelloWorld.pdf"));

            document.open();
            document.add(new Paragraph("A Hello World PDF document."));
            document.close(); // no need to close PDFwriter?

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void generatePdfMachine(PCInfoViewWrapper pc, String captureTime, PieChart pieChart){
        //Cr�ation du nom du document
    	String docNameCapture = captureTime.replace(' ', '_').replace(':', '_');
        String docName = pc.getHostname()+"_"+docNameCapture+".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                new FileOutputStream(docName));

            document.open();
            Paragraph title = new Paragraph("System Info Visualizer", H1);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Capture Time: "+ captureTime, SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(new Paragraph("PC Information", H2));

            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(10f);

            PdfPCell cell = new PdfPCell(new Paragraph("Hostname"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getHostname()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Ip Address"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getIpAddress()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("MAC Address"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getMacAddress()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Operating System"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getOs()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("CPU Constructor"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getCpu().getConstructor()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("CPU Model"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getCpu().getModel()));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("CPU Frequency"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getCpu().getFrequency()+" GHz"));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("CPU Number of Cores"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getCpu().getNbCore()+" Cores"));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Storage Capacity"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getHdd().getTotalSize()+" GB"));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Free Storage Space"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getHdd().getFreeSize()+" GB"));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Memory Size"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(pc.getRamSize()+" GB"));
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Installed Programs"));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(String.valueOf(pc.getPrograms().size())));
            table.addCell(cell);

            document.add(table);

            //Ajout du graphique camembert

    		WritableImage image = pieChart.snapshot(new SnapshotParameters(), null);

    		File file = new File("chart.png");

    		try {
    	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    		} catch (IOException e) {
    	        // TODO: handle exception here
    	    }

            Image chart = Image.getInstance("chart.png");
            chart.scaleAbsolute(250f,250f);
            chart.setAlignment(Element.ALIGN_CENTER);
            document.add(chart);
            file.delete();
            //Ajout de l'histogramme

            /*
             *
             document.add(new Paragraph("Storage Evolution", H2));
            WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);

    		File file = new File("chart.png");

    		try {
    	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    		} catch (IOException e) {
    	        // TODO: handle exception here
    	    }

            Image chart = Image.getInstance("chart.png");
            chart.setAlignment(Element.ALIGN_CENTER);
            //Ajuster la taille
            document.add(chart);



            */
            document.add(new Paragraph("Installed programs", H2));
            table = new PdfPTable(2);
            table.setSpacingBefore(10f);

            cell = new PdfPCell(new Paragraph("Program", STD_BOLD));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph("Version", STD_BOLD));
            table.addCell(cell);

            for(ProgramViewWrapper p : pc.getPrograms()){
                cell = new PdfPCell(new Paragraph(p.getName()));
                table.addCell(cell);
                cell = new PdfPCell(new Paragraph(p.getVersion()));
                table.addCell(cell);
            }
            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}