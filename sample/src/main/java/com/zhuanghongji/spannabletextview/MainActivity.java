package com.zhuanghongji.spannabletextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zhuanghongji.spannabletextview.lib.SpannableTextView;

public class MainActivity extends AppCompatActivity {

	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final String test = "there are <b>two</b> a label in the right: " +
				" <a href=\"http://www.baidu.com\">BaiDu</a> " +
				"and" +
				" <a href=\"https://www.google.com\">Google</a> " +
				"you can click to test the click listener.";

		SpannableTextView tv = findViewById(R.id.spannableTextView);
		tv.setOnSpannableClickListener(new SpannableTextView.OnSpannableClickListener() {
			@Override
			public void onNormalClick(View view, String text) {
				Log.i(TAG, "onNormalClick text = " + text);
			}

			@Override
			public void onUrlClick(View view, String text, String url) {
				Log.i(TAG, "onUrlClick text = " + text + ", url = " + url);
			}
		});
		tv.setSpannableText(test);
	}
}
