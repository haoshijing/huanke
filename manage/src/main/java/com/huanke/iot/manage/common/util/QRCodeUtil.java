package com.huanke.iot.manage.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;


/**
 * @模块名：taf
 * @包名：com.tit.taf.io.qrCode
 * @类名称： QRCodeUtil
 * @类描述：【类描述】二维码操作类
 * @版本：1.0
 * @创建人：cc
 * @创建时间：2018年8月27日下午2:38:48
 */
public class QRCodeUtil {
    /**
     * 指定编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 指定二维码类型
     */
    private static final String FORMAT = "PNG";

    /**
     * 指定二维码尺寸
     */
    private static final int QRCODE_SIZE = 300;

    /**
     * 指定LOGO宽度
     */
    private static final int LOGO_WIDTH = 60;

    /**
     * 指定LOGO高度
     */
    private static final int LOGO_HEIGHT = 60;

    /**
     *
     * @方法名：encode
     * @方法描述【方法功能描述】生成二维码(可指定内嵌LOGO)可指定二维码文件名
     * @param content 内容
     * @param logoPath LOGO地址，为空则不指定logo
     * @param destPath 存放目录
     * @param fileName 二维码文件名,为空则二维码文件名随机，文件名可能会有重复
     * @param needCompress 是否压缩LOGO
     * @return 二维码生成路径
     * @throws Exception
     * @修改描述【修改描述】
     * @版本：1.0
     * @创建人：cc
     * @创建时间：2018年8月27日 下午3:09:20
     * @修改人：cc
     * @修改时间：2018年8月27日 下午3:09:20
     */
    public static String encode(String content, String logoPath, String destPath, String fileName, boolean needCompress)
            throws Exception {
        BufferedImage image = QRCodeUtil.createImage(content, logoPath, needCompress);
        mkdirs(destPath);
        if (fileName != null && !"".equals(fileName)) {
            fileName = fileName.substring(0, fileName.indexOf(".") > 0 ? fileName.indexOf(".") : fileName.length())
                    + "." + FORMAT.toLowerCase();
        }
        else {
            fileName = new Random().nextInt(99999999) + "." + FORMAT.toLowerCase();
        }
        ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
        return fileName;
    }

    /**
     *
     * @方法名：createImage
     * @方法描述【方法功能描述】 生成二维码
     * @param content 二维码内容
     * @param logoPath LOGO地址，为空则不指定logo
     * @param needCompress 是否需要压缩
     * @return 图片
     * @throws Exception
     * @修改描述【修改描述】
     * @版本：1.0
     * @创建人：cc
     * @创建时间：2018年8月27日 下午3:12:24
     * @修改人：cc
     * @修改时间：2018年8月27日 下午3:12:24
     */
    private static BufferedImage createImage(String content, String logoPath, boolean needCompress) throws Exception {
        Hashtable < EncodeHintType, Object > hints = new Hashtable <EncodeHintType, Object >();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
                hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (logoPath == null || "".equals(logoPath)) {
            return image;
        }
        // 插入图片
        QRCodeUtil.insertImage(image, logoPath, needCompress);
        return image;
    }

    /**
     *
     * @方法名：insertImage
     * @方法描述【方法功能描述】 插入LOGO
     * @param source 二维码图片
     * @param logoPath LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     * @修改描述【修改描述】
     * @版本：1.0
     * @创建人：cc
     * @创建时间：2018年8月27日 下午3:13:36
     * @修改人：cc
     * @修改时间：2018年8月27日 下午3:13:36
     */
    private static void insertImage(BufferedImage source, String logoPath, boolean needCompress) throws Exception {
        File file = new File(logoPath);
        if (!file.exists()) {
            throw new Exception("logo file not found.");
        }
        Image src = ImageIO.read(new File(logoPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > LOGO_WIDTH) {
                width = LOGO_WIDTH;
            }
            if (height > LOGO_HEIGHT) {
                height = LOGO_HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     *
     * @方法名：mkdirs
     * @方法描述【方法功能描述】 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir． (mkdir如果父目录不存在则会抛出异常)
     * @param destPath 存放目录
     * @修改描述【修改描述】
     * @版本：1.0
     * @创建人：cc
     * @创建时间：2018年8月27日 下午3:14:05
     * @修改人：cc
     * @修改时间：2018年8月27日 下午3:14:05
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }
}
