package gaozhi.online.peoplety.ui.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.SystemUtil;
import gaozhi.online.peoplety.util.ToastUtil;

public class WebActivity extends BaseActivity implements DownloadListener {
    private static final String INTENT_TAG_URL = "URL";
    private static final String INTENT_TAG_TITLE = "title";
    private WebView webView;
    private String url;
    private String title;
    private ImageView image_finish;
    private TextView text_title;
    private ImageView image_open_browser;

    @Override
    protected void initParams(Intent intent) {
        url =intent.getStringExtra(INTENT_TAG_URL);
        title=intent.getStringExtra(INTENT_TAG_TITLE);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_web;
    }

    @Override
    protected void initView(View view) {
        webView = $(R.id.web_view);
        image_finish=$(R.id.title_image_left);
        image_finish.setImageResource(R.drawable.close);
        image_finish.setOnClickListener(this);
        image_open_browser=$(R.id.title_image_right);
        image_open_browser.setImageResource(R.drawable.more);
        image_open_browser.setOnClickListener(this);
        text_title=$(R.id.title_text);
        if(title!=null){
            text_title.setText(title);
        }
        text_title.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        //解决一些图片加载问题
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setSupportZoom(true);
        settings.setDefaultTextEncodingName("UTF -8");//设置默认为utf-8
        //页面返回链接直接显示
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view,request);
            }
        });
        webView.setDownloadListener(this);

        if(PatternUtil.matchUrl(url)) {
            webView.loadUrl(url);
        }else {
            image_open_browser.setVisibility(View.INVISIBLE);
            webView.loadData(url, "text/html; charset=UTF-8", null);//这种写法可以正确解码
        }
    }

    //监听BACK按键，有可以返回的页面时返回页面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.setTag(null);
            webView.clearHistory();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_image_left:
                finish();
                break;
            case R.id.title_image_right:
                startBrowser();
                break;
            case R.id.title_text:
                if(PatternUtil.matchUrl(url)) {
                    SystemUtil.copyStr2Clipboard(this, "", url);
                    ToastUtil.showToastShort(getString(R.string.tip_copyed) + url);
                }
                break;
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        startBrowser();
    }
    private void startBrowser(){
        if(StringUtil.isEmpty(url)){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.tip_please_choose_browser)));
    }
    /**
     *
     * @param context 上下文环境
     * @param url   目标链接
     * @param title 标题
     */
    public static void startActivity(Context context, String url,String title) {
        Intent intent = new Intent(context,WebActivity.class);
        intent.putExtra(INTENT_TAG_URL, url);
        intent.putExtra(INTENT_TAG_TITLE, title);
        context.startActivity(intent);
    }
}