package com.just.library.agentweb;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.just.library.LogUtils;
import com.just.library.WebDefaultSettingsManager;
import com.just.library.WebSettings;

/**
 * Created by cenxiaozhong on 2017/5/15.
 */

public class AgentWebFragment extends Fragment implements FragmentKeyDown {


    private ImageView mBackImageView;
    private View mLineView;
    private ImageView mFinishImageView;
    private TextView mTitleTextView;
    protected AgentWeb mAgentWeb;
    public static final String URL_KEY="url_key";

    public static AgentWebFragment getInstance(Bundle bundle) {

        AgentWebFragment mAgentWebFragment = new AgentWebFragment();
        if (bundle != null)
            mAgentWebFragment.setArguments(bundle);

        return mAgentWebFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agentweb, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setIndicatorColorWithHeight(-1,2)//
                .setWebSettings(getSettings())//
                .setWebViewClient(mWebViewClient)
                .setReceivedTitleCallback(mCallback)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());


        initView(view);
    }



    public WebSettings getSettings(){
        return WebDefaultSettingsManager.getInstance();
    }
    public String getUrl(){
        String target="";

        if(TextUtils.isEmpty(target=this.getArguments().getString(URL_KEY))){
            target="http://www.jd.com";
        }
        return target;
    }
    protected ChromeClientCallbackManager.ReceivedTitleCallback mCallback=new ChromeClientCallbackManager.ReceivedTitleCallback() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            if(mTitleTextView!=null&&!TextUtils.isEmpty(title))
                if(title.length()>10)
                    title=title.substring(0,10)+"...";
                mTitleTextView.setText(title);

        }
    };
    protected WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            LogUtils.i("Info","shouldOverrideUrlLoading");
            return false;
        }



        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            Log.i("Info","url:"+url+"   target:"+getUrl());
            if(url.equals(getUrl())){
                pageNavigator(View.GONE);
            }else{
                pageNavigator(View.VISIBLE);
            }

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.i("Info","onActivityResult result");
        mAgentWeb.uploadFileResult(requestCode,resultCode,data);
    }

    protected void initView(View view) {
        mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
        mLineView = view.findViewById(R.id.view_line);

        mFinishImageView = (ImageView) view.findViewById(R.id.iv_finish);
        mTitleTextView = (TextView) view.findViewById(R.id.toolbar_title);

        mBackImageView.setOnClickListener(mOnClickListener);
        mFinishImageView.setOnClickListener(mOnClickListener);

        pageNavigator(View.GONE);
    }

    private void pageNavigator(int tag) {

        Log.i("Info","TAG:"+tag);
        mBackImageView.setVisibility(tag);
        mLineView.setVisibility(tag);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()){

                case R.id.iv_back:

                    if(!mAgentWeb.back())
                        AgentWebFragment.this.getActivity().finish();

                    break;
                case R.id.iv_finish:
                    AgentWebFragment.this.getActivity().finish();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        return mAgentWeb.handleKeyEvent(keyCode,event);
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
        //  mAgentWeb.destroy();
    }
}
