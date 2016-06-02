package utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import monitor.model.PCInfoViewWrapper;
import monitor.model.ProgramViewWrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Cette classe fournit des méthodes permettant l'exportation des données au format PDF.
 */
public class PdfGenerator {

	//Définition de styles
	private static final Font H1 = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
	private static final Font H2 = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
	private static final Font SUBTITLE = new Font(Font.FontFamily.HELVETICA, 16, Font.ITALIC);
	private static final Font STD_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

	/**
	 * Permet la génération d'un PDF résumant toutes les informations d'une capture.
	 *
	 * @param file, le fichier dans lequel stocker le PDF
	 * @param pcList, l'état des PCs du parc lors de la capture
	 * @param captureTime, la date de la capture
	 * @param nbCoreChart, le graphique des PCs par nombre de coeurs du processeur
	 * @param modelChart, le graphique des PCs par modèles de processeur
	 * @param hddSizeChart, le graphique des PCs par taille de disque dur
	 * @param ramSizeChart, le graphique des PCs par taille de mémoire vive
	 * @param storageChart, le graphique de la charge moyenne des disque durs en fonction du temps
	 * @param programsChart, le graphique des programmes les plus installés
	 */
	public static void generatePdfCapture(File file, ObservableList<PCInfoViewWrapper> pcList, String captureTime, PieChart nbCoreChart, PieChart modelChart, PieChart hddSizeChart, PieChart ramSizeChart, LineChart<String, Double> storageChart, BarChart<String, Integer> programsChart){
        Document document = new Document();
        try {
            PdfWriter.getInstance(document,
                new FileOutputStream(file));

            document.open();
            Paragraph title = new Paragraph("System Info Visualizer - Park", H1);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Capture Date: "+ captureTime, SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(new Paragraph("Computer Park", H2));
            document.add(new Paragraph("Number of computers in the park: "+ pcList.size()));
            addParkInformation(pcList, document);
            document.newPage();

    		document.add(new Paragraph("Park Statistics", H2));

    		PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidthPercentage(90f);

            addPieChart(nbCoreChart, table);
            addPieChart(modelChart, table);
            addPieChart(hddSizeChart, table);
            addPieChart(ramSizeChart, table);

            document.add(table);

            document.newPage();

            document.add(new Paragraph("Storage Statistics", H2));
            addLineChart(storageChart, document);

            document.add(new Paragraph("Program Statistics", H2));
            addBarChart(programsChart, document);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet la génération d'un PDF résumant toutes les informations d'un PC lors d'une certaine capture.
	 *
	 * @param file, le fichier dans lequel le PDF sera enregistré
	 * @param pc, le PC qui sera décrit dans le PDF
	 * @param captureTime, la date de la capture correspondant à l'état du PC
	 * @param pieChart, le graphique de l'espace de stockage libre par rapport à l'espace total
	 * @param lineChart, le graphique montrant l'évolution dans le temps du taux d'occupation de l'espace de stockage
	 */
	public static void generatePdfMachine(File file, PCInfoViewWrapper pc, String captureTime, PieChart pieChart, LineChart<String, Double> lineChart){
        Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                new FileOutputStream(file));

            document.open();
            Paragraph title = new Paragraph("System Info Visualizer - PC", H1);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Capture Date: "+ captureTime, SUBTITLE);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

    		document.add(new Paragraph("PC Information", H2));
            addPCInformation(pc, document);

            document.add(new Paragraph("Installed programs", H2));
            addProgramInfos(pc.getPrograms(), document);

            document.newPage();
            document.add(new Paragraph("Storage Load", H2));
            addPieChart(pieChart, document);

            document.add(new Paragraph("Storage Load Evolution", H2));
            addLineChart(lineChart, document);


            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	/**
	 * Permet la génération de la section "Park Information" du PDF résumant une capture
	 *
	 * @param pcList, la liste des PCs du parc lors de la capture
	 * @param document, le PDF en cours de génération
	 * @throws DocumentException
	 */
	private static void addParkInformation(ObservableList<PCInfoViewWrapper> pcList, Document document) throws DocumentException {

		PdfPTable table = new PdfPTable(5);

		table.setSpacingBefore(10f);
		table.setWidthPercentage(90f);

		table.addCell(new PdfPCell(new Paragraph("Hostname", STD_BOLD)));
		table.addCell(new PdfPCell(new Paragraph("IP Address", STD_BOLD)));
		table.addCell(new PdfPCell(new Paragraph("OS", STD_BOLD)));
		table.addCell(new PdfPCell(new Paragraph("Free Space", STD_BOLD)));
		table.addCell(new PdfPCell(new Paragraph("Installed Programs", STD_BOLD)));

		for(PCInfoViewWrapper pc : pcList){
			table.addCell(new PdfPCell(new Paragraph(pc.getHostname())));
			table.addCell(new PdfPCell(new Paragraph(pc.getIpAddress())));
			table.addCell(new PdfPCell(new Paragraph(pc.getOs())));
			table.addCell(new PdfPCell(new Paragraph(pc.getHdd().getFreeSize()+" GB")));
			table.addCell(new PdfPCell(new Paragraph(Integer.toString(pc.getPrograms().size()))));
		}
		document.add(table);

	}


	/**
	 * Permet la génération de la section "Installed programs" du PDF résumant un PC
	 *
	 * @param programs, la liste des programmes du PC
	 * @param document, le PDF en cours de génération
	 * @throws DocumentException
	 */
	private static void addProgramInfos(ObservableList<ProgramViewWrapper> programs, Document document) throws DocumentException {
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell = null;

         table.setSpacingBefore(10f);

		cell = new PdfPCell(new Paragraph("Program", STD_BOLD));
         table.addCell(cell);
         cell = new PdfPCell(new Paragraph("Version", STD_BOLD));
         table.addCell(cell);

         for(ProgramViewWrapper p : programs){
             cell = new PdfPCell(new Paragraph(p.getName()));
             table.addCell(cell);
             cell = new PdfPCell(new Paragraph(p.getVersion()));
             table.addCell(cell);
         }
         document.add(table);

	}

	/**
	 * Permet d'ajouter un diagramme en barre à un PDF.
	 * Crée une image .png à partir d'un graphique, l'ajoute au PDF, puis l'efface.
	 *
	 * @param barChart, le diagramme à ajouter
	 * @param document, le PDF en cours de génération
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static void addBarChart(BarChart<String, Integer> barChart, Document document) throws IOException, DocumentException {
    	WritableImage image = barChart.snapshot(new SnapshotParameters(), null);

		File file = new File("chart.png");

	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);


        Image chart = Image.getInstance("chart.png");
        chart.setAlignment(Element.ALIGN_CENTER);
        chart.scalePercent(80f);
        document.add(chart);
        file.delete();

	}

	/**
	 * Permet d'ajouter un diagramme en courbes à un PDF.
	 * Crée une image .png à partir d'un graphique, l'ajoute au PDF, puis l'efface.
	 *
	 * @param lineChart, le diagramme à ajouter
	 * @param document, le PDF en cours de génération
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static void addLineChart(LineChart<String, Double> lineChart, Document document) throws IOException, DocumentException {
		WritableImage image = lineChart.snapshot(new SnapshotParameters(), null);

		File file = new File("chart.png");

	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);


        Image chart = Image.getInstance("chart.png");
        chart.setAlignment(Element.ALIGN_CENTER);
        chart.scalePercent(80f);
        document.add(chart);
        file.delete();
	}

	/**
	 * Permet d'ajouter un diagramme en camembert à un PDF.
	 * Crée une image .png à partir d'un graphique, l'ajoute au PDF, puis l'efface.
	 *
	 * @param pieChart, le diagramme à ajouter
	 * @param document, le PDF en cours de génération
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static void addPieChart(PieChart pieChart, Document document) throws MalformedURLException, IOException, DocumentException {
		WritableImage image = pieChart.snapshot(new SnapshotParameters(), null);

		File file = new File("chart.png");

	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);


        Image chart = Image.getInstance("chart.png");
        chart.setAlignment(Element.ALIGN_CENTER);
        chart.scalePercent(80f);
        document.add(chart);
        file.delete();

	}

	/**
	 * Permet de placer un diagramme en camembert dans un tableau.
	 * Crée une image .png à partir d'un graphique, l'ajoute au PDF, puis l'efface.
	 *
	 * @param pieChart, le diagramme à ajouter
	 * @param table, le tableau auquel ajouter le diagramme
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static void addPieChart(PieChart pieChart, PdfPTable table) throws MalformedURLException, IOException, DocumentException {
		WritableImage image = pieChart.snapshot(new SnapshotParameters(), null);
		File file = new File("chart.png");
	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	     Image chart = Image.getInstance("chart.png");
	     chart.setAlignment(Element.ALIGN_CENTER);
	     chart.scalePercent(50f);
	     PdfPCell cell = new PdfPCell(chart);
	     cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	     cell.setPaddingTop(10f);
	     cell.setPaddingBottom(10f);
	     table.addCell(cell);
	     file.delete();

	}

	/**
	 * Permet d'ajouter la section "PC information" au PDF résumant un PC
	 *
	 * @param pc, le PC résumé dans le PDF
	 * @param document, le PDF en cours de génération
	 * @throws DocumentException
	 */
	private static void addPCInformation(PCInfoViewWrapper pc, Document document) throws DocumentException {
         PdfPTable table = new PdfPTable(2);
         table.setSpacingBefore(10f);
         table.setSpacingAfter(10f);

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

	}

}
