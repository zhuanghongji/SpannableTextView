package com.zhuanghongji.urlspannabletextview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a simple custom TextView on Android for dealing the click event of url <br>
 * ( such as {@code <a href='https://www.google.com'>Google<a/>} )
 * @author zhuanghongji
 * @version 1.0.0
 */

public class UrlSpannableTextView extends android.support.v7.widget.AppCompatTextView {

	/**
	 * the color of normal text
	 */
	private int mNormalTextColor;

	/**
	 * the color of value in {@code <a/>} label
	 */
	private int mUrlValueColor;

	/**
	 * the background color of the normal text or the value in {@code <a/>} label after click
	 */
	private int mHighLightColor;

	private boolean mEnableUnderLineOfNormalText;

	private boolean mEnableUnderLineOfUrlValue;

	/**
	 * the text you set to {@link UrlSpannableTextView} by {@link #setSpannableText(String)}
	 */
	private String mSpannableText;

	private SpannableStringBuilder mSpannableStringBuilder;

	private OnSpannableClickListener mOnSpannableClickListener;

	public UrlSpannableTextView(Context context) {
		this(context, null);
	}

	public UrlSpannableTextView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UrlSpannableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initTypedArray(context, attrs);

		setSpannableText(mSpannableText);
		setHighlightColor(mHighLightColor);
	}

	private void initTypedArray(Context context, @Nullable AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UrlSpannableTextView);

		mSpannableText = typedArray.getString(R.styleable.UrlSpannableTextView_spannableText);

		mNormalTextColor = typedArray.getColor(
				R.styleable.UrlSpannableTextView_normalTextColor, Color.BLACK);
		mUrlValueColor = typedArray.getColor(
				R.styleable.UrlSpannableTextView_urlValueColor, Color.BLUE);
		mHighLightColor = typedArray.getColor(
				R.styleable.UrlSpannableTextView_highLightColor, Color.TRANSPARENT);

		mEnableUnderLineOfNormalText = typedArray.getBoolean(
				R.styleable.UrlSpannableTextView_enableUnderLineOfNormalText, false);
		mEnableUnderLineOfUrlValue= typedArray.getBoolean(
				R.styleable.UrlSpannableTextView_enableUnderLineOfUrlValue, false);
		typedArray.recycle();
	}


	public String getSpannableText() {
		return mSpannableText;
	}

	public void setSpannableText(String spannableText) {
		mSpannableText = spannableText;
		mSpannableStringBuilder = genSpannableStringBuilder(spannableText);
		setText(mSpannableStringBuilder);
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 *
	 * @return {@code null} if you never {@link #setSpannableText(String)}
	 */
	@Nullable
	public SpannableStringBuilder getSpannableStringBuilder() {
		return mSpannableStringBuilder;
	}

	private SpannableStringBuilder genSpannableStringBuilder(@Nullable String spannableText) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		if (TextUtils.isEmpty(spannableText)) {
			return builder;
		}

		while (isUrlText(spannableText)) {
			// append normal text
			String a = "<a";
			int firstIndex = spannableText.indexOf(a);
			String normalText = spannableText.substring(0, firstIndex);
			if (!TextUtils.isEmpty(normalText)) {
				builder.append(genNormalSpannableString(normalText));
			}

			// append url value
			a = "</a>";
			int secondIndex = spannableText.indexOf(a) + a.length();
			String urlText = spannableText.substring(firstIndex, secondIndex);
			if (!TextUtils.isEmpty(urlText)) {
				builder.append(genUrlSpannableString(urlText));
			}

			spannableText = spannableText.substring(secondIndex, spannableText.length());
		}

		// the rest normal text
		if (!TextUtils.isEmpty(spannableText)) {
			builder.append(genNormalSpannableString(spannableText));
		}
		return builder;
	}

	private SpannableString genNormalSpannableString(final String text) {
		final Spanned spanned = Html.fromHtml(text);
		SpannableString result = new SpannableString(spanned);
		result.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(mNormalTextColor);
				ds.setUnderlineText(mEnableUnderLineOfNormalText);
			}

			@Override
			public void onClick(View widget) {
				if (mOnSpannableClickListener != null) {
					mOnSpannableClickListener.onNormalClick(widget, text);
				}
			}
		}, 0, spanned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return result;
	}

	private SpannableString genUrlSpannableString(String urlText) {
		final String href = getHrefFromALabel(urlText);
		final String value = getValueFromALabel(urlText);
		final Spanned spanned = Html.fromHtml(urlText);
		SpannableString result = new SpannableString(spanned);
		result.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(mUrlValueColor);
				ds.setUnderlineText(mEnableUnderLineOfUrlValue);
			}

			@Override
			public void onClick(View widget) {
				if (mOnSpannableClickListener != null) {
					mOnSpannableClickListener.onUrlClick(widget, value, href);
				}
			}
		}, 0, spanned.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return result;
	}

	@NonNull
	private String getValueFromALabel(String urlText) {
		String regex = "<a[^>]*>([^<]*)</a>";
		return getStringByRegex(urlText, regex);
	}

	@NonNull
	private String getHrefFromALabel(String urlText) {
		String regex = "href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'>\\s]+))";
		return getStringByRegex(urlText, regex);
	}

	@NonNull
	private String getStringByRegex(String text, String regex) {
		if (TextUtils.isEmpty(text)) {
			return "";
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		String result = "";
		while (matcher.find()) {
			result = matcher.group(1);
		}
		return result;
	}

	/**
	 * assert the spannableString contain {@code <a>} label or not
	 * @param spannableText the target to assert
	 * @return {@code true} contain ；{@code false} or not
	 */
	private boolean isUrlText (String spannableText){
		if (TextUtils.isEmpty(spannableText)) {
			return false;
		}
		String regex = ".*<a.*?/a>.*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(spannableText);
		return matcher.matches();
	}

	public int getNormalTextColor() {
		return mNormalTextColor;
	}

	public void setNormalTextColor(int normalTextColor) {
		mNormalTextColor = normalTextColor;
	}

	public int getUrlValueColor() {
		return mUrlValueColor;
	}

	public void setUrlValueColor(int urlValueColor) {
		mUrlValueColor = urlValueColor;
	}

	public int getHighLightColor() {
		return mHighLightColor;
	}

	public void setHighLightColor(int highLightColor) {
		mHighLightColor = highLightColor;
	}

	public boolean isEnableUnderLineOfNormalText() {
		return mEnableUnderLineOfNormalText;
	}

	public void setEnableUnderLineOfNormalText(boolean enableUnderLineOfNormalText) {
		mEnableUnderLineOfNormalText = enableUnderLineOfNormalText;
	}

	public boolean isEnableUnderLineOfUrlValue() {
		return mEnableUnderLineOfUrlValue;
	}

	public void setEnableUnderLineOfUrlValue(boolean enableUnderLineOfUrlValue) {
		mEnableUnderLineOfUrlValue = enableUnderLineOfUrlValue;
	}

	public void setOnSpannableClickListener(OnSpannableClickListener onSpannableClickListener) {
		mOnSpannableClickListener = onSpannableClickListener;
	}

	/**
	 * Listener used to handle click events.
	 */
	public interface OnSpannableClickListener {

		/**
		 * will be called if user click normal text
		 * @param widget
		 * @param text the normal text
		 */
		void onNormalClick(View widget, String text);

		/**
		 * will be called if user click the value of {@code <a/>} label
		 * @param widget
		 * @param value the value of {@code <a/>} label
		 * @param url the value of href attr
		 */
		void onUrlClick(View widget, String value, String url);
	}
}
