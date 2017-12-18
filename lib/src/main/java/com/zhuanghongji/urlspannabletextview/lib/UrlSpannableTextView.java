package com.zhuanghongji.urlspannabletextview.lib;

import android.content.Context;
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
 * 2017/12/17
 * @author zhuanghongji
 * @version 1.0.0
 */

public class UrlSpannableTextView extends android.support.v7.widget.AppCompatTextView {

	public static final String TAG = "UrlSpannableTextView";

	private Context mContext;

	/**
	 * the default color of normal Strings
	 */
	private int mNormalColor = Color.BLACK;

	/**
	 * the default color of url in "a' label
	 */
	private int mUrlColor = Color.BLUE;

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
		mContext = context;
	}


	public String getSpannableText() {
		return mSpannableText;
	}

	public void setSpannableText(String spannableText) {
		mSpannableText = spannableText;
		mSpannableStringBuilder = genSpannableStringBuilder(spannableText);
		setText(mSpannableStringBuilder);
		setHighlightColor(Color.TRANSPARENT);
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
			// append normal string
			String a = "<a";
			int firstIndex = spannableText.indexOf(a);
			String normalText = spannableText.substring(0, firstIndex);
			if (!TextUtils.isEmpty(normalText)) {
				builder.append(genNormalSpannableString(normalText));
			}

			// append url string
			a = "</a>";
			int secondIndex = spannableText.indexOf(a) + a.length();
			String urlText = spannableText.substring(firstIndex, secondIndex);
			if (!TextUtils.isEmpty(urlText)) {
				builder.append(genUrlSpannableString(urlText));
			}

			spannableText = spannableText.substring(secondIndex, spannableText.length());
		}

		// the rest normal String
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
				ds.setColor(mNormalColor);
				ds.setUnderlineText(false);
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
				ds.setColor(mUrlColor);
				ds.setUnderlineText(true);
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
	 * assert the spannableString contain <a> label or not
	 * @param spannableText the target to assert
	 * @return {@code true} contain ；{@code false} not contain
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

	public void setOnSpannableClickListener(OnSpannableClickListener onSpannableClickListener) {
		mOnSpannableClickListener = onSpannableClickListener;
	}

	public interface OnSpannableClickListener {
		void onNormalClick(View view, String text);
		void onUrlClick(View view, String text, String url);
	}
}
