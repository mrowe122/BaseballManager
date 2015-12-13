package com.msrproduction.baseballmanager.plugins;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.msrproduction.baseballmanager.R;

import java.lang.ref.WeakReference;

public class UsPhoneNumberFormatter implements TextWatcher {
	//This TextWatcher sub-class formats entered numbers as 1 (123) 456-7890
	private boolean mFormatting; // this is a flag which prevents the
	// stack(onTextChanged)
	private boolean clearFlag;
	private boolean with1 = false;
	private boolean formatted = false;
	private boolean anyError = false;
	private int mLastStartLocation;
	private String mLastBeforeText;
	private WeakReference<EditText> mWeakEditText;
	private TextInputLayout til;
	private Activity activity;

	public UsPhoneNumberFormatter(WeakReference<EditText> weakEditText, TextInputLayout til, Activity act) {
		this.mWeakEditText = weakEditText;
		this.til = til;
		this.activity = act;
		this.til.setErrorEnabled(true);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		if (after == 0 && s.toString().equals("1 ")) {
			clearFlag = true;
			with1 = false;
		}
		mLastStartLocation = start;
		mLastBeforeText = s.toString();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Do nothing
	}

	@Override
	public void afterTextChanged(Editable s) {
		// Make sure to ignore calls to afterTextChanged caused by the work
		// done below
		if (!mFormatting) {
			mFormatting = true;
			formatted = true;
			int curPos = mLastStartLocation;
			String beforeValue = mLastBeforeText;
			String currentValue = s.toString();
			String formattedValue = formatUsNumber(s);
			if (currentValue.length() > beforeValue.length()) {
				int setCusorPos = formattedValue.length()
						- (beforeValue.length() - curPos);
				mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
			} else {
				int setCusorPos = formattedValue.length()
						- (currentValue.length() - curPos);
				if (setCusorPos > 0 && !Character.isDigit(formattedValue.charAt(setCusorPos - 1))) {
					setCusorPos--;
				}
				mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
			}
			if (formattedValue.length() == 0 ||
					(formattedValue.length() == 16 && formatted) ||
					(!with1 && formattedValue.length() == 14 && formatted)) {
				til.setError(null);
				anyError = false;
			} else {
				til.setError(activity.getString(R.string.error_invalid_phone));
				anyError = true;
			}
			mFormatting = false;
		}
	}

	public boolean getError() {
		return anyError;
	}

	private String formatUsNumber(Editable text) {
		StringBuilder formattedString = new StringBuilder();
		// Remove everything except digits
		int p = 0;
		while (p < text.length()) {
			char ch = text.charAt(p);
			if (!Character.isDigit(ch)) {
				text.delete(p, p + 1);
			} else {
				p++;
			}
		}
		// Now only digits are remaining
		String allDigitString = text.toString();

		int totalDigitCount = allDigitString.length();

		if (totalDigitCount == 0
				|| (totalDigitCount > 10 && !allDigitString.startsWith("1"))
				|| totalDigitCount > 11) {
			// May be the total length of input length is greater than the
			// expected value so we'll remove all formatting
			text.clear();
			text.append(allDigitString);
			formatted = false;
			return allDigitString;
		}
		int alreadyPlacedDigitCount = 0;
		// Only '1' is remaining and user pressed backspace and so we clear
		// the edit text.
		if (allDigitString.equals("1") && clearFlag) {
			text.clear();
			clearFlag = false;
			return "";
		}
		if (allDigitString.startsWith("1")) {
			formattedString.append("1 ");
			with1 = true;
			alreadyPlacedDigitCount++;
		}
		// The first 3 numbers beyond '1' must be enclosed in brackets "()"
		if (totalDigitCount - alreadyPlacedDigitCount > 3) {
			formattedString.append("(").append(allDigitString.substring(alreadyPlacedDigitCount,
					alreadyPlacedDigitCount + 3)).append(") ");
			alreadyPlacedDigitCount += 3;
		}
		// There must be a '-' inserted after the next 3 numbers
		if (totalDigitCount - alreadyPlacedDigitCount > 3) {
			formattedString.append(allDigitString.substring(
					alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3)).append("-");
			alreadyPlacedDigitCount += 3;
		}
		// All the required formatting is done so we'll just copy the
		// remaining digits.
		if (totalDigitCount > alreadyPlacedDigitCount) {
			formattedString.append(allDigitString.substring(alreadyPlacedDigitCount));
		}

		text.clear();
		text.append(formattedString.toString());
		return formattedString.toString();
	}

}