package com.gitee.qdbp.tools.files;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * 图片工具
 *
 * @author zhaohuihua
 */
public class ImageTools {

    /**
     * 生成缩略图<br>
     * 截取图片的中间部分(不产生变形)<br>
     * 如3000x2000生成100x100的缩略图<br>
     * 先截取中间的2000x2000的部分, 再缩小为100x100
     *
     * @param width
     * @param height
     * @throws IOException
     */
    public static byte[] thumbnail(byte[] bytes, int width, int height) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        return thumbnail(is, width, height);
    }

    /**
     * 生成缩略图<br>
     * 截取图片的中间部分(不产生变形)<br>
     * 如3000x2000生成100x100的缩略图<br>
     * 先截取中间的2000x2000的部分, 再缩小为100x100
     *
     * @param width
     * @param height
     * @throws IOException
     */
    public static byte[] thumbnail(InputStream is, int width, int height) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        thumbnail(is, os, width, height);
        return os.toByteArray();
    }

    /**
     * 生成缩略图<br>
     * 截取图片的中间部分(不产生变形)<br>
     * 如3000x2000生成100x100的缩略图<br>
     * 先截取中间的2000x2000的部分, 再缩小为100x100
     *
     * @param width 缩放后的宽度, 0表示自动计算
     * @param height 缩放后的宽度, 0表示自动计算
     * @throws IOException
     */
    public static void thumbnail(InputStream is, OutputStream os, int width, int height) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(is);
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
        ImageReader reader = iterator.next();
        reader.setInput(iis, true);
        String imageType = reader.getFormatName();
        BufferedImage image = reader.read(0);

        int w = image.getWidth();
        int h = image.getHeight();

        if (width > 0 && height <= 0) {
            height = (int) (1.0 * h / w * width);
        } else if (width <= 0 && height > 0) {
            width = (int) (1.0 * w / h * height);
        } else if (width <= 0 && height <= 0) {
            width = w;
            height = h;
        }

        double wscale = 1.0 * width / w;
        double hscale = 1.0 * height / h;

        double scale = height / wscale > h ? hscale : wscale;

        int nw = (int) (width / scale);
        int nh = (int) (height / scale);

        Builder<?> builder = Thumbnails.of(image);
        builder.scale(scale);
        builder.sourceRegion(Positions.CENTER, nw, nh);

        builder.outputFormat(imageType).toOutputStream(os);
    }

    /**
     * 生成二维码
     * 
     * @param content 内容
     * @param size 尺寸
     * @param os 输出流
     */
    public static void generateQrCode(String content, int size, OutputStream os) {
        generateQrCode(content, size, Math.max(5, size / 10), os);
    }

    /**
     * 生成二维码
     * 
     * @param content 内容
     * @param size 尺寸
     * @param margin 边距
     * @param os 输出流
     */
    public static void generateQrCode(String content, int size, int margin, OutputStream os) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

            QRCodeWriter writer = new QRCodeWriter();

            // 生成二维码
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            // 重设白边大小
            if (margin >= 0) {
                matrix = updateBit(matrix, margin);
            }

            MatrixToImageWriter.writeToStream(matrix, "png", os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重设白边框宽度
     * 
     * @param matrix
     * @param margin
     * @return
     */
    private static BitMatrix updateBit(BitMatrix matrix, int margin) {
        int m = margin * 2;
        int[] rectangle = matrix.getEnclosingRectangle(); // 获取二维码图案的属性
        int w = rectangle[2] + m;
        int h = rectangle[3] + m;
        BitMatrix result = new BitMatrix(w, h); // 按照自定义边框生成新的BitMatrix
        result.clear();
        for (int i = margin; i < w - margin; i++) { // 循环，将二维码图案绘制到新的bitMatrix中
            for (int j = margin; j < h - margin; j++) {
                if (matrix.get(i - margin + rectangle[0], j - margin + rectangle[1])) {
                    result.set(i, j);
                }
            }
        }
        return result;
    }
}
