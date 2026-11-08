package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class QrCodeUtilsTest {

    private static final String PNG_DATA_URI_PREFIX = "data:image/png;base64,";
    private static final String QR_CONTENT = "SGILT:C:q3Zx9kP2mL7vT1nR8wY4sA";

    // -------------------------------------------------------------------------
    // pngDataUri
    // -------------------------------------------------------------------------

    @Nested
    class PngDataUri {

        @Test
        void givenContent_whenPngDataUri_thenReturnsPngDataUriOf512Px() throws Exception {
            BufferedImage image = decodeImage(QrCodeUtils.pngDataUri(QR_CONTENT));

            assertThat(image.getWidth()).isEqualTo(512);
            assertThat(image.getHeight()).isEqualTo(512);
        }

        @Test
        void givenContent_whenPngDataUri_thenQrReadsBackAsContent() throws Exception {
            BufferedImage image = decodeImage(QrCodeUtils.pngDataUri(QR_CONTENT));

            String read = new QRCodeReader()
                    .decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image))))
                    .getText();

            assertThat(read).isEqualTo(QR_CONTENT);
        }
    }

    // data URI → image ; échoue si le préfixe ou le PNG sont invalides
    private BufferedImage decodeImage(String dataUri) throws Exception {
        assertThat(dataUri).startsWith(PNG_DATA_URI_PREFIX);
        byte[] png = Base64.getDecoder().decode(dataUri.substring(PNG_DATA_URI_PREFIX.length()));
        return ImageIO.read(new ByteArrayInputStream(png));
    }
}
