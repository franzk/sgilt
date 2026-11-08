package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Map;

/**
 * Dessine les QR des e-billets, seul endroit où sont fixés leurs paramètres : la page e-billet
 * (via le DTO) et le PDF affichent la même image.
 */
public final class QrCodeUtils {

    // Correction M (~15 % de modules récupérables) : bon compromis densité / tolérance aux écrans abîmés.
    private static final ErrorCorrectionLevel QR_ERROR_CORRECTION = ErrorCorrectionLevel.M;
    // Marge blanche en modules, autour du QR : 4 = la marge recommandée par la norme pour la lecture.
    private static final int QR_MARGIN_MODULES = 4;
    // Côté de l'image en pixels : net jusqu'à ~12rem sur écran haute densité comme à l'impression.
    private static final int QR_SIZE_PX = 512;
    private static final int BLACK = 0x000000;
    private static final int WHITE = 0xFFFFFF;
    private static final String PNG_DATA_URI_PREFIX = "data:image/png;base64,";

    private QrCodeUtils() {
    }

    /**
     * Dessine le QR d'un contenu, en PNG encodé en data URI (affichable tel quel dans un {@code <img>}).
     *
     * @param content le contenu du QR
     * @return {@code data:image/png;base64,…}
     */
    public static String pngDataUri(String content) {
        return PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(png(content));
    }

    // QR → PNG ; une erreur ici est un bug (contenu trop long, encodeur PNG absent), pas un cas métier.
    private static byte[] png(String content) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, QR_SIZE_PX, QR_SIZE_PX, Map.of(
                    EncodeHintType.ERROR_CORRECTION, QR_ERROR_CORRECTION,
                    EncodeHintType.MARGIN, QR_MARGIN_MODULES));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(toImage(matrix), "png", output);
            return output.toByteArray();
        } catch (WriterException e) {
            throw new IllegalStateException("Impossible d'encoder le QR", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire le PNG du QR", e);
        }
    }

    // Une image en niveaux de gris suffit (noir et blanc) et donne un PNG plus léger qu'en RGB.
    private static BufferedImage toImage(BitMatrix matrix) {
        BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < matrix.getWidth(); x++) {
            for (int y = 0; y < matrix.getHeight(); y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
}
