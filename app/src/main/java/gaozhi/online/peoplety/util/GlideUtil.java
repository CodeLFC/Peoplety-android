package gaozhi.online.peoplety.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.InputStream;
import java.security.MessageDigest;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.util.pictureselector.ImageLoaderUtils;

/**
 * Glide工具
 */
public class GlideUtil {

    public static void loadImage(Context context, String url, @DrawableRes int resId, ImageView imageView) {
        if (!checkContext(context,url,resId,imageView)) return;
        Glide.with(context)
                .load(url)
                .placeholder(resId)
                .into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (!checkContext(context,url,imageView)) return;
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.app_logo)
                .into(imageView);
    }

    public static void loadGrayImage(Context context, String url, ImageView imageView) {
        if (!checkContext(context,url,imageView)) return;
        Glide.with(context)
                .load(url)
                .transform(new GreyPicTransform())
                .into(imageView);
    }

    public static void loadImage(Context context, Bitmap bitmap, ImageView imageView) {
        if (!checkContext(context)) return;
        Glide.with(context).load(bitmap).into(imageView);
    }

    public static void loadRoundRectangleImage(Context context, int resID, ImageView imageView) {
        if (!checkContext(context)) return;
        InputStream is = context.getResources().openRawResource(resID);
        Bitmap resource = BitmapFactory.decodeStream(is);
        int width = resource.getWidth();
        int height = resource.getHeight();
        Bitmap bitmap = ImageUtil.getRoundBitmapByShader(resource, width, height, height / 8, 0);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }

    public static void loadBitmap(Context context, String url, @DrawableRes int resId, CustomTarget<Bitmap> customTarget) {
        if (!checkContext(context)) return;
        Glide.with(context).asBitmap().load(url).placeholder(resId).into(customTarget);
    }

    public static void loadRoundRectangleImage(Context context, String url, ImageView imageView) {
        if (!checkContext(context, url, imageView)) return;
        Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                Bitmap bitmap = ImageUtil.getRoundBitmapByShader(resource, width, height, height / 8, 0);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }

    public static void loadRoundRectangleImage(Context context, String url, @DrawableRes int resId, ImageView imageView) {
        if (!checkContext(context, url, resId, imageView)) return;
        Log.d(GlideUtil.class.getName(), "url:" + url);
        Glide.with(context).asBitmap().placeholder(resId).load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                Bitmap bitmap = ImageUtil.getRoundBitmapByShader(resource, width, height, height / 8, 0);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }

    public static void loadRoundImage(Context context, String url, ImageView imageView) {
        if (!checkContext(context, url, imageView)) return;
        Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                Bitmap bitmap = ImageUtil.getRoundBitmapByShader(resource, width, height, height / 2, 0);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }

    public static void loadRoundImage(Context context, String url, @DrawableRes int resId, ImageView imageView) {
        if (!checkContext(context, url, resId, imageView)) return;
        Glide.with(context).asBitmap().load(url).placeholder(resId).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                Bitmap bitmap = ImageUtil.getRoundBitmapByShader(resource, width, height, height / 2, 0);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
            }
        });
    }

    private static boolean checkContext(Context context) {
        return ImageLoaderUtils.assertValidRequest(context);
    }

    private static boolean checkContext(Context context, String url, ImageView imageView) {
        return checkContext(context, url, R.drawable.app_logo, imageView);
    }

    private static boolean checkContext(Context context, String url, @DrawableRes int resId, ImageView imageView) {
        if (!ImageLoaderUtils.assertValidRequest(context)) {
            return false;
        }
        if (url == null) {
            Glide.with(context).load(resId).into(imageView);
            return false;
        }
        return true;
    }

    /**
     * Glide 将下载到的图片转成灰色图片
     * <p>
     * Created by bayin on 2017/2/16.
     */

    public static class GreyPicTransform extends BitmapTransformation {

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            return ImageUtil.convertGreyImg(toTransform);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }
}
