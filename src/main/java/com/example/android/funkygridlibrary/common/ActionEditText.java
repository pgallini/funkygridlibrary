package com.example.android.funkygridlibrary.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by Paul Gallini on 1/29/17.
 *
 * By extending EditText, I can combine a multiline input type with a DONE button
 *   (versus the CR button).  THis is used in candidates_entry.xml.  See the second answer on this post:
 * http://stackoverflow.com/questions/2986387/multi-line-edittext-with-done-action-button
 *
 */
//   *** changing this due to error when trying to apply the proper material design type
    // error formating to Question Entry
//public class ActionEditText extends android.support.v7.widget.AppCompatEditText
public class ActionEditText extends     android.support.design.widget.TextInputEditText

{
    public ActionEditText(Context context)
    {
        super(context);
    }

    public ActionEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ActionEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}