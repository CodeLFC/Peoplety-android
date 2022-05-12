package gaozhi.online.peoplety.util;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * zxing 工具
 */
public class ZxingUtil {
    /**
     * 生成二维码
     */
    public static class QRCodeGenerator {
        /**
         * 生辰不带logo的二维码
         *
         * @param text
         * @param w
         * @param h
         * @return
         */
        public static Bitmap generateImage(String text, int w, int h) {
            return generateImage(text, w, h, null);
        }

        /**
         * 生成带 logo 的二维码
         *
         * @param text 生成二维码的字符串
         * @param w    生成二维码的宽
         * @param h    生成二维码的高
         * @param logo 生成二维码中间的 logo 如果生成不带 logo 的二维码参数传 null 即可
         * @return 生成二维码的 Bitmap
         */
        public static Bitmap generateImage(String text, int w, int h, Bitmap logo) {
            if (TextUtils.isEmpty(text)) {
                return null;
            }
            try {
                Bitmap scaleLogo = getScaleLogo(logo, w, h);
                int width = scaleLogo.getWidth();
                int height = scaleLogo.getHeight();
                scaleLogo = ImageUtil.getRoundBitmapByShader(scaleLogo, width, height, height / 8, 2);
                int offsetX = w / 2;
                int offsetY = h / 2;

                int scaleWidth = 0;
                int scaleHeight = 0;
                if (scaleLogo != null) {
                    scaleWidth = scaleLogo.getWidth();
                    scaleHeight = scaleLogo.getHeight();
                    offsetX = (w - scaleWidth) / 2;
                    offsetY = (h - scaleHeight) / 2;
                }
                Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                //容错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                //设置空白边距的宽度
                hints.put(EncodeHintType.MARGIN, 0);
                BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
                int[] pixels = new int[w * h];
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                            int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                            if (pixel == 0) {
                                if (bitMatrix.get(x, y)) {
                                    pixel = 0xff000000;
                                } else {
                                    pixel = 0xffffffff;
                                }
                            }
                            pixels[y * w + x] = pixel;
                        } else {
                            if (bitMatrix.get(x, y)) {
                                pixels[y * w + x] = 0xff000000;
                            } else {
                                pixels[y * w + x] = 0xffffffff;
                            }
                        }
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(w, h,
                        Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
                return bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
            if (logo == null) return null;
            Matrix matrix = new Matrix();
            float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
            matrix.postScale(scaleFactor, scaleFactor);
            return Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        }
    }

    public static class ImageAnalyser {
        /**
         * 获取资源
         *
         * @param res
         * @param resID
         * @return
         */
        public static Bitmap getDrawable(Resources res, int resID) {
            return BitmapFactory.decodeResource(res, resID);
        }

        public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
            // 获得图片的宽高
            int width = bm.getWidth();
            int height = bm.getHeight();
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newbm;
        }
    }

    /**
     * 解析二维码
     */
    public static class QRCodeAnalyser {

        /**
         * 识别图片
         *
         * @param path file:// 或 http:// 资源
         */
        public static String analyzeImage(final String path) {
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            if (path.startsWith("http://")) {
                return analyzeBitmap(ImageUtil.getImage(path));
            } else {
                /**
                 * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
                 */
                Bitmap mBitmap;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 先获取原大小
                BitmapFactory.decodeFile(path, options);
                int sampleSize = (int) (options.outHeight / (float) 400);
                if (sampleSize <= 0)
                    sampleSize = 1;
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false; // 获取新的大小
                mBitmap = BitmapFactory.decodeFile(path, options);
                mBitmap = ImageAnalyser.zoomImg(mBitmap, mBitmap.getWidth() * 3, mBitmap.getHeight() * 3);
                return analyzeBitmap(mBitmap);
            }
        }

        /**
         * 解析二维码位图
         *
         * @param bitmap
         * @return 结果字符串
         */
        public static String analyzeBitmap(Bitmap bitmap) {
            MultiFormatReader multiFormatReader = new MultiFormatReader();

            // 解码的参数
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
            // 可以解析的编码类型
            Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats = new Vector<BarcodeFormat>();
            // 这里设置可扫描的类型，这里选择了都支持
            decodeFormats.addAll(DecodeFormat.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormat.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormat.DATA_MATRIX_FORMATS);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
            // 设置继续的字符编码格式为UTF8
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
//         设置解析配置参数
            multiFormatReader.setHints(hints);

            // 开始对图像资源解码
            Result rawResult = null;
            String result = null;
            try {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width, height, pixels))));
                result = rawResult.getText();
            } catch (Exception e) {
                Log.e("zxingUtil", "not QRCode");
            }

            return result;
        }
    }

    static final class DecodeFormat {

        private static final Pattern COMMA_PATTERN = Pattern.compile(",");

        static final Set<BarcodeFormat> PRODUCT_FORMATS;
        static final Set<BarcodeFormat> INDUSTRIAL_FORMATS;
        public static final Set<BarcodeFormat> ONE_D_FORMATS;
        public static final Set<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
        public static final Set<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);
        static final Set<BarcodeFormat> AZTEC_FORMATS = EnumSet.of(BarcodeFormat.AZTEC);
        static final Set<BarcodeFormat> PDF417_FORMATS = EnumSet.of(BarcodeFormat.PDF_417);

        static {
            PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                    BarcodeFormat.UPC_E,
                    BarcodeFormat.EAN_13,
                    BarcodeFormat.EAN_8,
                    BarcodeFormat.RSS_14,
                    BarcodeFormat.RSS_EXPANDED);
            INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                    BarcodeFormat.CODE_93,
                    BarcodeFormat.CODE_128,
                    BarcodeFormat.ITF,
                    BarcodeFormat.CODABAR);
            ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
            ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);
        }

        private static final Map<String, Set<BarcodeFormat>> FORMATS_FOR_MODE;

        static {
            FORMATS_FOR_MODE = new HashMap<>();
            FORMATS_FOR_MODE.put(Intents.Scan.ONE_D_MODE, ONE_D_FORMATS);
            FORMATS_FOR_MODE.put(Intents.Scan.PRODUCT_MODE, PRODUCT_FORMATS);
            FORMATS_FOR_MODE.put(Intents.Scan.QR_CODE_MODE, QR_CODE_FORMATS);
            FORMATS_FOR_MODE.put(Intents.Scan.DATA_MATRIX_MODE, DATA_MATRIX_FORMATS);
            FORMATS_FOR_MODE.put(Intents.Scan.AZTEC_MODE, AZTEC_FORMATS);
            FORMATS_FOR_MODE.put(Intents.Scan.PDF417_MODE, PDF417_FORMATS);
        }

        private DecodeFormat() {
        }

        public static Set<BarcodeFormat> parseDecodeFormats(Intent intent) {
            Iterable<String> scanFormats = null;
            CharSequence scanFormatsString = intent.getStringExtra(Intents.Scan.FORMATS);
            if (scanFormatsString != null) {
                scanFormats = Arrays.asList(COMMA_PATTERN.split(scanFormatsString));
            }
            return parseDecodeFormats(scanFormats, intent.getStringExtra(Intents.Scan.MODE));
        }

        private static Set<BarcodeFormat> parseDecodeFormats(Iterable<String> scanFormats, String decodeMode) {
            if (scanFormats != null) {
                Set<BarcodeFormat> formats = EnumSet.noneOf(BarcodeFormat.class);
                try {
                    for (String format : scanFormats) {
                        formats.add(BarcodeFormat.valueOf(format));
                    }
                    return formats;
                } catch (IllegalArgumentException iae) {
                    // ignore it then
                }
            }
            if (decodeMode != null) {
                return FORMATS_FOR_MODE.get(decodeMode);
            }
            return null;
        }
    }

    static final class Intents {
        private Intents() {
        }

        public static final class Scan {
            /**
             * Send this intent to open the Barcodes app in scanning mode, find a barcode, and return
             * the results.
             */
            public static final String ACTION = "com.google.zxing.client.android.SCAN";

            /**
             * By default, sending this will decode all barcodes that we understand. However it
             * may be useful to limit scanning to certain formats. Use
             * {@link Intent#putExtra(String, String)} with one of the values below.
             * <p>
             * Setting this is effectively shorthand for setting explicit formats with {@link #FORMATS}.
             * It is overridden by that setting.
             */
            public static final String MODE = "SCAN_MODE";

            /**
             * Decode only UPC and EAN barcodes. This is the right choice for shopping apps which get
             * prices, reviews, etc. for products.
             */
            public static final String PRODUCT_MODE = "PRODUCT_MODE";

            /**
             * Decode only 1D barcodes.
             */
            public static final String ONE_D_MODE = "ONE_D_MODE";

            /**
             * Decode only QR codes.
             */
            public static final String QR_CODE_MODE = "QR_CODE_MODE";

            /**
             * Decode only Data Matrix codes.
             */
            public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

            /**
             * Decode only Aztec.
             */
            public static final String AZTEC_MODE = "AZTEC_MODE";

            /**
             * Decode only PDF417.
             */
            public static final String PDF417_MODE = "PDF417_MODE";

            /**
             * Comma-separated list of formats to scan for. The values must match the names of
             * {@link BarcodeFormat}s, e.g. {@link BarcodeFormat#EAN_13}.
             * Example: "EAN_13,EAN_8,QR_CODE". This overrides {@link #MODE}.
             */
            public static final String FORMATS = "SCAN_FORMATS";

            /**
             * Optional parameter to specify the id of the camera from which to recognize barcodes.
             * Overrides the default camera that would otherwise would have been selected.
             * If provided, should be an int.
             */
            public static final String CAMERA_ID = "SCAN_CAMERA_ID";

            /**
             * @see DecodeHintType#CHARACTER_SET
             */
            public static final String CHARACTER_SET = "CHARACTER_SET";

            /**
             * Set to false to disable beep. Defaults to true.
             */
            public static final String BEEP_ENABLED = "BEEP_ENABLED";

            /**
             * Set to true to return a path to the barcode's image as it was captured. Defaults to false.
             */
            public static final String BARCODE_IMAGE_ENABLED = "BARCODE_IMAGE_ENABLED";

            /**
             * Set the time to finish the scan screen.
             */
            public static final String TIMEOUT = "TIMEOUT";

            /**
             * Whether or not the orientation should be locked when the activity is first started.
             * Defaults to true.
             */
            public static final String ORIENTATION_LOCKED = "SCAN_ORIENTATION_LOCKED";

            /**
             * Prompt to show on-screen when scanning by intent. Specified as a {@link String}.
             */
            public static final String PROMPT_MESSAGE = "PROMPT_MESSAGE";

            /**
             * If a barcode is found, Barcodes returns {@link android.app.Activity#RESULT_OK} to
             * {@link android.app.Activity#(int, int, Intent)}
             * of the app which requested the scan via
             * {@link android.app.Activity#startActivityForResult(Intent, int)}
             * The barcodes contents can be retrieved with
             * {@link Intent#getStringExtra(String)}.
             * If the user presses Back, the result code will be {@link android.app.Activity#RESULT_CANCELED}.
             */
            public static final String RESULT = "SCAN_RESULT";

            /**
             * Call {@link Intent#getStringExtra(String)} with {@link #RESULT_FORMAT}
             * to determine which barcode format was found.
             * See {@link BarcodeFormat} for possible values.
             */
            public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";

            /**
             * Call {@link Intent#getStringExtra(String)} with {@link #RESULT_UPC_EAN_EXTENSION}
             * to return the content of any UPC extension barcode that was also found. Only applicable
             * to {@link BarcodeFormat#UPC_A} and {@link BarcodeFormat#EAN_13}
             * formats.
             */
            public static final String RESULT_UPC_EAN_EXTENSION = "SCAN_RESULT_UPC_EAN_EXTENSION";

            /**
             * Call {@link Intent#getByteArrayExtra(String)} with {@link #RESULT_BYTES}
             * to get a {@code byte[]} of raw bytes in the barcode, if available.
             */
            public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";

            /**
             * Key for the value of {@link com.google.zxing.ResultMetadataType#ORIENTATION}, if available.
             * Call {@link Intent#getIntArrayExtra(String)} with {@link #RESULT_ORIENTATION}.
             */
            public static final String RESULT_ORIENTATION = "SCAN_RESULT_ORIENTATION";

            /**
             * Key for the value of {@link com.google.zxing.ResultMetadataType#ERROR_CORRECTION_LEVEL}, if available.
             * Call {@link Intent#getStringExtra(String)} with {@link #RESULT_ERROR_CORRECTION_LEVEL}.
             */
            public static final String RESULT_ERROR_CORRECTION_LEVEL = "SCAN_RESULT_ERROR_CORRECTION_LEVEL";

            /**
             * Prefix for keys that map to the values of {@link com.google.zxing.ResultMetadataType#BYTE_SEGMENTS},
             * if available. The actual values will be set under a series of keys formed by adding 0, 1, 2, ...
             * to this prefix. So the first byte segment is under key "SCAN_RESULT_BYTE_SEGMENTS_0" for example.
             * Call {@link Intent#getByteArrayExtra(String)} with these keys.
             */
            public static final String RESULT_BYTE_SEGMENTS_PREFIX = "SCAN_RESULT_BYTE_SEGMENTS_";

            /**
             * Call {@link Intent#getStringExtra(String)} with {@link #RESULT_BARCODE_IMAGE_PATH}
             * to get a {@code String} path to a cropped and compressed png file of the barcode's image
             * as it was displayed. Only available if
             * is called with true.
             */
            public static final String RESULT_BARCODE_IMAGE_PATH = "SCAN_RESULT_IMAGE_PATH";

            /***
             * The scan should be inverted. White becomes black, black becomes white.
             */
            public static final String INVERTED_SCAN = "INVERTED_SCAN";

            private Scan() {
            }
        }
    }
}
