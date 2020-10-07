package ml.generator.hotspot.helper;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class CodeQrHelper {

	public static final long MAX_PRODUITS = 10_000_000L;

	public static final long CODEBARRE_LENGTH = 14;

	private static final int CARD_W = (int) (60 * AppConst.POINTS_PER_MM); // = 170 pixels
	private static final int CARD_H = (int) (30 * AppConst.POINTS_PER_MM); // = 85 pixels
	public static final PDRectangle CARD_60x30 = new PDRectangle(-3, 0, CARD_W, CARD_H);


//	/**
//	 * @param type= 2 pour les produits 3 pour les ventes (non utilisée ici) 4 pour
//	 *              les paiements 5 pour les remboursements
//	 * @return code barre
//	 */
//	private static Long generateCodeBarreWith(int type, int codeMagazin, long nextId) {
//		if (!(type != 2 || type != 3 || type != 4 || type != 5)) {
//			throw new IllegalArgumentException("invalide value for type=" + type);
//		}
//		LocalDate now = LocalDate.now();
//		int prefix = type * 10000; // pour un décalage de YYMM
//		prefix = prefix + ((now.getYear() % 100) * 100) + now.getMonthValue();
//		prefix = (prefix * 100) + codeMagazin;
//		long codeBarre = MAX_PRODUITS * prefix;
//		codeBarre += nextId;
//		// Validation
//		if (codeBarre < 10000000000000L || codeBarre > 100000000000000L) {
//			throw new IllegalArgumentException("codeBarre calculé invalide, valeur=" + codeBarre);
//		}
//		return codeBarre;
//	}

	public static byte[] generateBatchQrCode(String code,String munite) throws Exception {
		BitMatrix bitMatrix;


		PDDocument document = new PDDocument();
		PDPage page = new PDPage(CARD_60x30);
		PDRectangle rect = page.getMediaBox();
		
		System.out.println("page=" + rect.getWidth() +  ", " +  (rect.getHeight()) );
		
		PDFont font = PDType1Font.HELVETICA;
		PDType1Font fontB = PDType1Font.HELVETICA_BOLD;
		document.addPage(page);
		PDPageContentStream content = new PDPageContentStream(document, page);
		
		int line = 10;
		int fontSize = 10;
		
		 line += fontSize;
		 
		try {
			content.beginText();
			content.setFont(font, 10);
			content.setNonStrokingColor(Color.BLACK);
			content.newLineAtOffset(10, rect.getHeight() - line);
			content.showText("QrCode");
			content.endText();

			fontSize = 7;
		//	line += fontSize + 5;
			// Text center
			String batchTitle = code;
			float titleSize = font.getStringWidth(batchTitle) / 1000 * fontSize;
			content.beginText();
			content.setFont(fontB, fontSize);
			content.newLineAtOffset( rect.getWidth() - titleSize - 23, rect.getHeight() - line);
			content.showText(batchTitle);
			content.endText();
			
			line = (int) (CARD_H * 0.70);
			
			int imageW = (int) (CARD_W * 0.85);
			int imageH = (int) (CARD_H * 0.3);
			
			int xStartImage = (int) ((rect.getWidth() / 2) - (imageW / 2));
			int yStartImage = (int) (rect.getHeight() - line);
					
			content.setNonStrokingColor(Color.LIGHT_GRAY);
			content.setStrokingColor(Color.WHITE);
			
			content.addRect(xStartImage, yStartImage, imageW, imageH);
			content.closeAndStroke();
			
//			Code128Writer code128Writer = new Code128Writer();
//			bitMatrix = code128Writer.encode(codebarre.toString(), BarcodeFormat.CODE_128, imageW, imageH);
			
			QRCodeWriter code128Writer = new QRCodeWriter();
			bitMatrix = code128Writer.encode(code, BarcodeFormat.QR_CODE, imageW, imageH);

			
			BufferedImage buffImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
			PDImageXObject xImage = LosslessFactory.createFromImage(document, buffImg);
			content.drawImage(xImage, xStartImage, yStartImage, imageW, imageH);
			
			fontSize = 9;
			line += (fontSize + 15);
			String value = "Minutes";
			float idSize = font.getStringWidth(value) / 1000 * fontSize;
			content.beginText();
			content.setFont(fontB, fontSize);
			content.setNonStrokingColor(Color.BLACK);
			content.newLineAtOffset(rect.getWidth() - idSize - 10, rect.getHeight() - line);// 170
			content.showText(munite +" mins");
			content.endText();

			fontSize = 8;
			//value = "Taille: " + produit.getTaille();
			content.beginText();
			content.setFont(font, fontSize);
			content.setNonStrokingColor(Color.BLACK);
			content.newLineAtOffset(10, rect.getHeight() - line);// 170
			content.showText(value);
			content.endText();

			fontSize = 8;
			content.beginText();
			content.setFont(font, fontSize);
			content.setNonStrokingColor(Color.BLACK);
			content.newLineAtOffset(10, rect.getHeight() - line + 8);// 170
			content.endText();

			content.close();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			document.save(output);
			document.close();

			byte[] data = output.toByteArray();
			return data;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

//	private static List<String> parseLines(PDFont FONT, float FONT_SIZE, float width, String text) throws Exception {
//		List<String> lines = new ArrayList<String>();
//		width=width-15;
//		int lastSpace = -1;
//		while (text.length() > 0) {
//			int spaceIndex = text.indexOf(' ', lastSpace + 1);
//			if (spaceIndex < 0)
//				spaceIndex = text.length();
//			String subString = text.substring(0, spaceIndex);
//			String remove = "\t";
//			if (subString.contains(remove)) {
//				subString = subString.replaceAll(remove, " ");
//			}
//
//			float size = FONT_SIZE * FONT.getStringWidth(subString.trim()) / 1000;
//			if (size > width) {
//				if (lastSpace < 0) {
//					lastSpace = spaceIndex;
//				}
//				subString = text.substring(0, lastSpace);
//				lines.add(subString);
//				text = text.substring(lastSpace).trim();
//				lastSpace = -1;
//			} else if (spaceIndex == text.length()) {
//				lines.add(text);
//				text = "";
//			} else {
//				lastSpace = spaceIndex;
//			}
//		}
//		return lines;
//	}

//	private static float addParagraph(PDPageContentStream content, PDFont font, float fontSize, float width, float sx,
//			float sy, String text, boolean justify) throws Exception {
//		content.beginText();
//		List<String> lines = parseLines(font, fontSize, width, text);
//		content.setFont(font, fontSize);
//
////		float stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() * fontSize / 1000;
//		// sy = sy - stringHeight;
//
//		content.newLineAtOffset(sx, sy);
//		float LEADING = -1f * fontSize;
//		for (String line : lines) {
//			float charSpacing = 0;
//			if (justify) {
//				if (line.length() > 1) {
//					float size = fontSize * font.getStringWidth(line) / 1000;
//					float free = width - size;
//					
//					if (free > 0 && !lines.get(lines.size() - 1).equals(line)) {
//						charSpacing = free / (line.length() - 1);
//					}
//				}
//			}
//			content.setCharacterSpacing(charSpacing);
//			content.setNonStrokingColor(0, 0, 0);
//			content.newLineAtOffset(0, LEADING);
//			content.showText(line);
//		}
//		sy = sy + LEADING * lines.size();
//		content.endText();
//		return sy;
//	}

}
