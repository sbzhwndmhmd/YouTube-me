package com.youtubetouch.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

public class MainActivity extends Activity {
    private WebView webView;
    private ProgressBar progress;
    private TextView title;
    private static final String HOME = "https://m.youtube.com/";

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(21,21,21));
        getWindow().setNavigationBarColor(Color.rgb(21,21,21));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(18,18,18));

        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(14), 0, dp(10), 0);
        bar.setBackgroundColor(Color.rgb(21,21,21));
        TextView logo = new TextView(this);
        logo.setText("▶"); logo.setTextColor(Color.rgb(255,0,51)); logo.setTextSize(24);
        bar.addView(logo, new LinearLayout.LayoutParams(dp(32), dp(52)));
        title = new TextView(this);
        title.setText("YouTube Touch"); title.setTextColor(Color.WHITE); title.setTextSize(18);
        title.setGravity(Gravity.CENTER_VERTICAL);
        bar.addView(title, new LinearLayout.LayoutParams(0, dp(52), 1));
        TextView refresh = new TextView(this);
        refresh.setText("↻"); refresh.setTextColor(Color.WHITE); refresh.setTextSize(27);
        refresh.setGravity(Gravity.CENTER); refresh.setContentDescription("Refresh");
        refresh.setOnClickListener(new View.OnClickListener(){ public void onClick(View v){ if(webView!=null) webView.reload(); }});
        bar.addView(refresh, new LinearLayout.LayoutParams(dp(44), dp(52)));
        root.addView(bar, new LinearLayout.LayoutParams(-1, dp(52)));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100); progress.setProgress(0);
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(18,18,18));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        CookieManager.getInstance().setAcceptCookie(true);
        if (android.os.Build.VERSION.SDK_INT >= 21) CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override public void onProgressChanged(WebView view, int p){ progress.setProgress(p); progress.setVisibility(p>=100?View.GONE:View.VISIBLE); }
            @Override public void onReceivedTitle(WebView view, String t){ if(t!=null && t.length()>0) title.setText("YouTube Touch"); }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url){
                if(url==null) return false;
                if(url.startsWith("http://") || url.startsWith("https://")) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch(Exception e) { }
                return true;
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                if(android.os.Build.VERSION.SDK_INT>=21) return shouldOverrideUrlLoading(view, request.getUrl().toString());
                return false;
            }
            @Override public void onPageStarted(WebView view, String url, Bitmap icon){ progress.setVisibility(View.VISIBLE); }
            @Override public void onPageFinished(WebView view, String url){ progress.setProgress(100); }
        });
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
        if(savedInstanceState==null) webView.loadUrl(HOME); else webView.restoreState(savedInstanceState);
    }

    private int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    @Override public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,1,0,"صفحه اصلی"); menu.add(0,2,1,"باز کردن در مرورگر"); menu.add(0,3,2,"درباره برنامه"); return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==1){ webView.loadUrl(HOME); return true; }
        if(item.getItemId()==2){ try{ startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()==null?HOME:webView.getUrl()))); }catch(Exception e){} return true; }
        if(item.getItemId()==3){ Toast.makeText(this,"YouTube Touch 1.0 — رابط لمسی سبک برای وب‌سایت یوتیوب",Toast.LENGTH_LONG).show(); return true; }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onBackPressed(){ if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onSaveInstanceState(Bundle out){ if(webView!=null) webView.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onDestroy(){ if(webView!=null){ webView.stopLoading(); webView.setWebChromeClient(null); webView.setWebViewClient(null); webView.destroy(); } super.onDestroy(); }
}
