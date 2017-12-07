package com.xinyang.alienware.gaodeiosstyle.utils;

/**
 * Created by Alienware on 2017/12/7.
 */

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/** 
  * 打开或关闭软键盘 
  *  
  * @author 
  *  
  */
public class KeyBoardUtils{


    /***
     * 打开软键盘
     * @param editText
     * @param context
     */
    public static void  openKeyBorad(EditText editText, Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText,InputMethodManager.RESULT_SHOWN);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /***
     * 关闭软键盘
     */
    public static void closeKeyBoard(EditText editText,Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);

    }
}

