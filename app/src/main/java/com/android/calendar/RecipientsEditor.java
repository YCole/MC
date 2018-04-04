/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar;

import android.content.Context;
import android.text.Annotation;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.util.Rfc822Tokenizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.android.ex.chips.RecipientEditTextView;

/**
 * Provide UI for editing the recipients of multi-media messages.
 */
public class RecipientsEditor extends RecipientEditTextView {
    private static final String TAG = "RecipientsEditor";
    private int mLongPressedPosition = -1;
    private final Rfc822Tokenizer mTokenizer;
    private char mLastSeparator = ',';
    private Runnable mOnSelectChipRunnable;

    // private final AddressValidator mInternalValidator;

    public RecipientsEditor(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTokenizer = new Rfc822Tokenizer();
        setTokenizer(mTokenizer);

        // mInternalValidator = new AddressValidator();
        // super.setValidator(mInternalValidator);

        // For the focus to move to the message body when soft Next is pressed
        setImeOptions(EditorInfo.IME_ACTION_NEXT);

        setThreshold(1); // pop-up the list after a single char is typed

        /*
         * The point of this TextWatcher is that when the user chooses an
         * address completion from the AutoCompleteTextView menu, it is marked
         * up with Annotation objects to tie it back to the address book entry
         * that it came from. If the user then goes back and edits that part of
         * the text, it no longer corresponds to that address book entry and
         * needs to have the Annotations claiming that it does removed.
         */
        addTextChangedListener(new TextWatcher() {
            private Annotation[] mAffected;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                Log.i(TAG, "beforeTextChanged, CharSequence = " + s
                        + ", start = " + start + ", count = " + count
                        + ", after = " + after);
                mAffected = ((Spanned) s).getSpans(start, start + count,
                        Annotation.class);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int after) {
                Log.i(TAG, "beforeTextChanged, CharSequence = " + s
                        + ", start = " + start + ", before = " + before
                        + ", after = " + after);
                if (before == 0 && after == 1) { // inserting a character
                    char c = s.charAt(start);
                    if (c == ',' || c == ';') {
                        // Remember the delimiter the user typed to end this
                        // recipient. We'll
                        // need it shortly in terminateToken().
                        mLastSeparator = c;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged, Editable s = " + s.toString());
                if (mAffected != null) {
                    for (Annotation a : mAffected) {
                        s.removeSpan(a);
                    }
                }
                mAffected = null;
            }
        });
    }
}
