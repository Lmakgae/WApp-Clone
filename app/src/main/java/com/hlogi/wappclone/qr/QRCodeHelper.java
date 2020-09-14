package com.hlogi.wappclone.qr;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeHelper {

    private static int BLACK = 0xFF000000;
    private static int WHITE = 0xFFFFFFFF;
    private static QRCodeHelper qrCodeHelper = null;
    private ErrorCorrectionLevel mErrorCorrectionLevel;
    private int mMargin;
    private String mContent;
    private int mWidth, mHeight;

    /**
     * private constructor of this class only access by stying in this class.
     */
    private QRCodeHelper(@NonNull Context context) {
        mHeight = (int) (context.getResources().getDisplayMetrics().heightPixels / 2.4);
        mWidth = (int) (context.getResources().getDisplayMetrics().widthPixels / 1.3);
    }

    /**
     * Singleton instance of QRCodeHelper.
     *
     * @return the QRCodeHelper instance.
     */
    public static QRCodeHelper newInstance(Context context) {
        if (qrCodeHelper == null) {
            qrCodeHelper = new QRCodeHelper(context);
        }
        return qrCodeHelper;
    }

    /**
     * Method for getting the generated QR Code.
     *
     * @return QR code bitmap.
     */
    public Bitmap getQRCOde() {
        return generate();
    }

    /**
     * Setting the correctionLevel to QR code.
     *
     * @param level ErrorCorrectionLevel for QR code.
     * @return instance of QrCode helper class.
     */
    public QRCodeHelper setErrorCorrectionLevel(ErrorCorrectionLevel level) {
        mErrorCorrectionLevel = level;
        return this;
    }

    /**
     * Setting the content for the QR code.
     *
     * @param content string to be stored in the QR code.
     * @return instance of QrCode helper class.
     */
    public QRCodeHelper setContent(String content) {
        mContent = content;
        return this;
    }

    /**
     * Simply setting the width and height for qrcode.
     *
     * @param width for QR code which needs to be greater than 1.
     * @param height for QR code which needs to be greater than 1.
     * @return instance of QrCode helper class.
     */
    public QRCodeHelper setWidthAndHeight(@IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    /**
     * Setting the margin for QR code.
     *
     * @param margin for QR code spaces.
     * @return instance of QrCode helper class.
     */
    public QRCodeHelper setMargin(@IntRange(from = 0) int margin) {
        mMargin = margin;
        return this;
    }

    /**
     * Generate the QR code bitmap based on the given properties.
     *
     * @return the generated QR code bitmap, or null.
     */
    private Bitmap generate() {
        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintsMap.put(EncodeHintType.ERROR_CORRECTION, mErrorCorrectionLevel);
        hintsMap.put(EncodeHintType.MARGIN, mMargin);
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(mContent, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);

            int[] pixels = new int[mWidth * mHeight];
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    if (bitMatrix.get(j, i)) {
                        pixels[i * mWidth + j] = BLACK;
                    } else {
                        pixels[i * mWidth + j] = WHITE;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
