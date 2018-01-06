package com.zhuanghongji.urlspannabletextview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.zhuanghongji.urlspannabletextview.lib.UrlSpannableTextView;

public class MainActivity extends AppCompatActivity {

	public static final String TAG = "MainActivity";

	private Context mContext;

	public static final String TEST_TEXT = "there are <b>two</b> a label in the right:"
			+ " <a href=\"http://www.baidu.com\">BaiDu</a>"
			+ " and"
			+ " <a href=\"https://www.google.com\">Google</a>"
			+ " you can click to test the click listener.";

	private UrlSpannableTextView tvUrlSpannable;

	private EditText etNormalTextColor;
	private EditText etUrlValueColor;
	private EditText etHighLightColor;

	private Button btnSetNormalTextColor;
	private Button btnSetUrlValueColor;
	private Button btnSetHighLightColor;

	private SwitchCompat scUnderLineOfNormalText;
	private SwitchCompat scUnderLineOfUrlValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initEvent();

		mContext = this;

		tvUrlSpannable.setOnSpannableClickListener(new UrlSpannableTextView.OnSpannableClickListener() {
			@Override
			public void onNormalClick(View view, String text) {
				Toast.makeText(mContext, "onNormalClick text = " + text,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onUrlClick(View view, String value, String url) {
				Toast.makeText(mContext, "onUrlClick value = " + value + ", url = " + url,
						Toast.LENGTH_SHORT).show();
			}
		});
		tvUrlSpannable.setSpannableText(TEST_TEXT);
	}

	private void initEvent() {
		btnSetNormalTextColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvUrlSpannable.setNormalTextColor(getColorByEditText(etNormalTextColor));
				tvUrlSpannable.setSpannableText(TEST_TEXT);
			}
		});

		btnSetUrlValueColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvUrlSpannable.setUrlValueColor(getColorByEditText(etUrlValueColor));
				tvUrlSpannable.setSpannableText(TEST_TEXT);
			}
		});

		btnSetHighLightColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tvUrlSpannable.setHighlightColor(getColorByEditText(etHighLightColor));
				tvUrlSpannable.setSpannableText(TEST_TEXT);
			}
		});

		scUnderLineOfNormalText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				tvUrlSpannable.setEnableUnderLineOfNormalText(isChecked);
				tvUrlSpannable.setSpannableText(TEST_TEXT);
			}
		});

		scUnderLineOfUrlValue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				tvUrlSpannable.setEnableUnderLineOfUrlValue(isChecked);
				tvUrlSpannable.setSpannableText(TEST_TEXT);
			}
		});
	}

	private void initView() {
		tvUrlSpannable = findViewById(R.id.spannableTextView);

		etNormalTextColor = findViewById(R.id.et_normal_text_color);
		etUrlValueColor = findViewById(R.id.et_url_value_color);
		etHighLightColor = findViewById(R.id.et_high_light_color);

		btnSetNormalTextColor = findViewById(R.id.btn_set_normal_text_color);
		btnSetUrlValueColor = findViewById(R.id.btn_set_url_value_color);
		btnSetHighLightColor = findViewById(R.id.btn_set_high_light_color);

		scUnderLineOfNormalText = findViewById(R.id.sc_under_line_of_normal_text);
		scUnderLineOfUrlValue = findViewById(R.id.sc_under_line_of_url_value);
	}

	private int getColorByEditText(EditText et) {
		String text = et.getText().toString();
		return Color.parseColor(text);
	}
}
