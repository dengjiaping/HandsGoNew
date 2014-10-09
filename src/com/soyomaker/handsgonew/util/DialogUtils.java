package com.soyomaker.handsgonew.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 对话框公共类，提供对话框类型有：Toast短消息，消息对话框，带开关的消息对话框，确认对话框， 列表项对话框，当选对方框，多选对话框，时间选择对话框，
 * 带开关项的时间选择对话框，等待对话框，用户自定义对话框。.
 * 
 * @author 张代松 2012-4-19
 */
public final class DialogUtils {

	/** 对话框标题默认图标, 如果调用方法没有指定则使用此图片。. */
	public static final int DEFAULT_DIALOG_ICON = android.R.drawable.ic_dialog_alert;

	/** 使用系统默认图片。. */
	public static final int ICON_RES_ID_SYSTEM = -1;

	/** 使用软件默认图片，即{@link DialogUtils#DEFAULT_DIALOG_ICON}。. */
	public static final int ICON_RES_ID_DEFAULT = 0;

	/** 字符串资源ID不指定，如果是标题或者内容将会置空，如果是按钮将会使用系统按钮文字。. */
	public static final int STRING_RES_ID_NULL = -1;

	/** 字符串数组资源ID不指定，即空列表。. */
	public static final int STRING_ARRAY_RES_ID_NULL = -1;

	/** 等待对方框，同时只会存在一个等待对话框，如果显示新的，之前的会自动关闭。. */
	private static ProgressDialog progressDialog;

	/**
	 * 显示Toast信息.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            消息内容
	 */
	public static void showToast(Context context, CharSequence msg) {
		showToast(context, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示Toast信息.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            消息内容资源ID
	 */
	public static void showToast(Context context, int msgResId) {
		showToast(context, msgResId, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示Toast信息.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            消息内容
	 * @param duration
	 *            the duration {@link Toast#LENGTH_LONG}
	 */
	public static void showToast(Context context, CharSequence msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	/**
	 * 显示Toast信息.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            消息显示时间长短，选择 {@link Toast#LENGTH_SHORT} 或者
	 * @param duration
	 *            the duration {@link Toast#LENGTH_LONG}
	 */
	public static void showToast(Context context, int msgResId, int duration) {
		Toast.makeText(context, msgResId, duration).show();
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            消息内容资源ID
	 */
	public static void showMessageDialog(Context context, int msgResId) {
		showMessageDialog(context, msgResId, null);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            消息内容
	 */
	public static void showMessageDialog(Context context, String msg) {
		showMessageDialog(context, null, msg, null);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param msgResId
	 *            消息内容资源ID
	 */
	public static void showMessageDialog(Context context, int titleResId, int msgResId) {
		showMessageDialog(context, titleResId, msgResId, null);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param msg
	 *            消息内容
	 */
	public static void showMessageDialog(Context context, String title, String msg) {
		showMessageDialog(context, title, msg, null);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            消息内容资源ID
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, int msgResId,
			DialogInterface.OnClickListener listener) {
		showMessageDialog(context, STRING_RES_ID_NULL, msgResId, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, String msg,
			DialogInterface.OnClickListener listener) {
		showMessageDialog(context, null, msg, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param msgResId
	 *            消息内容资源ID
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, int titleResId, int msgResId,
			DialogInterface.OnClickListener listener) {
		showMessageDialog(context, titleResId, msgResId, STRING_RES_ID_NULL, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, String title, String msg,
			DialogInterface.OnClickListener listener) {
		showMessageDialog(context, title, msg, STRING_RES_ID_NULL, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#DialogUtils#STRING_RES_ID_NULL}
	 * @param msgResId
	 *            消息内容资源ID
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, int titleResId, int msgResId,
			int positiveId, DialogInterface.OnClickListener listener) {
		showMessageDialog(context, titleResId, ICON_RES_ID_DEFAULT, msgResId, positiveId, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param msg
	 *            消息内容
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMessageDialog(Context context, String title, String msg, int positiveId,
			DialogInterface.OnClickListener listener) {
		showMessageDialog(context, title, ICON_RES_ID_DEFAULT, msg, positiveId, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msgResId
	 *            消息内容资源ID
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showMessageDialog(Context context, int titleResId, int iconResId,
			int msgResId, int positiveId, DialogInterface.OnClickListener listener) {
		showMessageDialog(context, getStringFromResource(context, titleResId), iconResId,
				getStringFromResource(context, msgResId), positiveId, listener);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showMessageDialog(Context context, String title, int iconResId, String msg,
			int positiveId, DialogInterface.OnClickListener listener) {
		showMessageDialog(context, title, iconResId, msg, positiveId, listener, Color.WHITE, true);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msgResId
	 *            消息内容资源ID
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 * @param cancelable
	 *            能否按返回键关闭对话框 {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showMessageDialog(Context context, int titleResId, int iconResId,
			int msgResId, int positiveId, DialogInterface.OnClickListener listener,
			boolean cancelable) {
		showMessageDialog(context, getStringFromResource(context, titleResId), iconResId,
				getStringFromResource(context, msgResId), positiveId, listener, Color.WHITE,
				cancelable);
	}

	/**
	 * 显示消息对话框.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 * @param cancelable
	 *            能否按返回键关闭对话框 {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showMessageDialog(Context context, String title, int iconResId, String msg,
			int positiveId, DialogInterface.OnClickListener listener, int textColor,
			boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		TextView tv = new TextView(context);
		tv.setGravity(Gravity.CENTER);
		tv.setTextAppearance(context, android.R.style.TextAppearance_Large_Inverse);
		tv.setText(msg);
		tv.setTextColor(textColor);
		builder.setView(tv);

		builder.setCancelable(cancelable);
		builder.setPositiveButton(positiveId == STRING_RES_ID_NULL ? android.R.string.ok
				: positiveId, listener);

		builder.show();
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param msgResId
	 *            消息内容资源ID
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showPromptDialog(Context context, int titleResId, int msgResId,
			DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, titleResId, getStringFromResource(context, msgResId), listener,
				buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showPromptDialog(Context context, int titleResId, String msg,
			DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, getStringFromResource(context, titleResId), msg, listener,
				buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showPromptDialog(Context context, String title, String msg,
			DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, title, ICON_RES_ID_DEFAULT, msg, listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msgResId
	 *            消息内容资源ID
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, int titleResId, int iconResId,
			int msgResId, DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, titleResId, iconResId, getStringFromResource(context, msgResId),
				listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, int titleResId, int iconResId, String msg,
			DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, getStringFromResource(context, titleResId), iconResId, msg,
				listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, String title, int iconResId, String msg,
			DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, title, iconResId, msg, true, listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msgResId
	 *            消息内容资源ID
	 * @param cancelable
	 *            能否按返回键关闭对话框
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, int titleResId, int iconResId,
			int msgResId, boolean cancelable, DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, titleResId, iconResId, getStringFromResource(context, msgResId),
				cancelable, listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param cancelable
	 *            能否按返回键关闭对话框
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个使用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, int titleResId, int iconResId, String msg,
			boolean cancelable, DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, getStringFromResource(context, titleResId), iconResId, msg,
				cancelable, listener, buttonResIds);
	}

	/**
	 * 显示确认对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param cancelable
	 *            能否按返回键关闭对话框
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个这是用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, String title, int iconResId, String msg,
			boolean cancelable, DefaultDialogListener listener, int... buttonResIds) {
		showPromptDialog(context, title, iconResId, msg, cancelable, listener, Color.WHITE,
				buttonResIds);
	}

	/**
	 * 显示确认对方框。（李可 20121022）.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param cancelable
	 *            能否按返回键关闭对话框
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param textColor
	 *            the text color
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个这是用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, String title, int iconResId, String msg,
			boolean cancelable, DefaultDialogListener listener, int textColor, int... buttonResIds) {
		showPromptDialog(context, title, iconResId, msg, cancelable, listener, Color.WHITE,
				Gravity.CENTER, buttonResIds);
	}

	/**
	 * 显示确认对方框。（李可 20121022）.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param msg
	 *            消息内容
	 * @param cancelable
	 *            能否按返回键关闭对话框
	 * @param listener
	 *            对话框相关事件，null则不处理事件s
	 * @param textColor
	 *            the text color
	 * @param textGravity
	 *            the text gravity
	 * @param buttonResIds
	 *            按钮显示字符资源ID，依次代表确认、中立、取消按钮。确认对方框至少有2个按钮，
	 *            如果数组长度为小于2个这是用系统默认文字，超过3个的无效。 {@link DialogUtils#DialogUtils
	 *            #STRING_RES_ID_NULL}则使用系统默认
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showPromptDialog(Context context, String title, int iconResId, String msg,
			boolean cancelable, DefaultDialogListener listener, int textColor, int textGravity,
			int... buttonResIds) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		TextView tv = new TextView(context);
		tv.setGravity(textGravity);
		tv.setTextAppearance(context, android.R.style.TextAppearance_Large_Inverse);
		tv.setText(msg);
		tv.setTextColor(textColor);
		builder.setView(tv);
		builder.setCancelable(cancelable);

		try {
			int len = buttonResIds.length;
			if (len == 0) {
				builder.setPositiveButton(android.R.string.ok, listener);
				builder.setNegativeButton(android.R.string.cancel, listener);
			} else if (len == 1) {
				builder.setPositiveButton(buttonResIds[0], listener);
				builder.setNegativeButton(android.R.string.cancel, listener);
			} else {
				builder.setPositiveButton(buttonResIds[0], listener);
				builder.setNeutralButton(buttonResIds[1], listener);
				builder.setNegativeButton(buttonResIds[2], listener);
			}
		} catch (Exception e) {
			// 使用异常捕获的方式来兼容按钮设置。
		}

		builder.setOnCancelListener(listener);

		builder.show();
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showItemsDialog(Context context, int itemsResId,
			ItemSelectedListener listener) {
		showItemsDialog(context, getStringArrayFromResource(context, itemsResId), listener);
	}

	/**
	 * 显示列表对话框。 S.
	 * 
	 * @param context
	 *            上下文
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showItemsDialog(Context context, String[] items,
			ItemSelectedListener listener) {
		showItemsDialog(context, null, items, listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showItemsDialog(Context context, int titleResId, int itemsResId,
			ItemSelectedListener listener) {
		showItemsDialog(context, titleResId, getStringArrayFromResource(context, itemsResId),
				listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showItemsDialog(Context context, int titleResId, String[] items,
			ItemSelectedListener listener) {
		showItemsDialog(context, getStringFromResource(context, titleResId), items, listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showItemsDialog(Context context, String title, String[] items,
			ItemSelectedListener listener) {
		showItemsDialog(context, title, ICON_RES_ID_DEFAULT, items, listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param listener
	 *            Item点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showItemsDialog(Context context, int titleResId, int iconResId,
			int itemsResId, ItemSelectedListener listener) {
		showItemsDialog(context, titleResId, iconResId,
				getStringArrayFromResource(context, itemsResId), listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showItemsDialog(Context context, int titleResId, int iconResId,
			String[] items, ItemSelectedListener listener) {
		showItemsDialog(context, getStringFromResource(context, titleResId), iconResId, items,
				listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showItemsDialog(Context context, String title, int iconResId,
			final String[] items, final ItemSelectedListener listener) {
		showItemsDialog(context, title, iconResId, items, listener, true);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param cancelable
	 *            能否按返回键关闭对话框 {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showItemsDialog(Context context, int titleResId, int iconResId,
			int itemsResId, ItemSelectedListener listener, boolean cancelable) {
		showItemsDialog(context, titleResId, iconResId,
				getStringArrayFromResource(context, itemsResId), listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param cancelable
	 *            能否按返回键关闭对话框 {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showItemsDialog(Context context, int titleResId, int iconResId,
			String[] items, ItemSelectedListener listener, boolean cancelable) {
		showItemsDialog(context, getStringFromResource(context, titleResId), iconResId, items,
				listener);
	}

	/**
	 * 显示列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param cancelable
	 *            能否按返回键关闭对话框 {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showItemsDialog(Context context, String title, int iconResId,
			final String[] items, final ItemSelectedListener listener, boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		builder.setCancelable(cancelable);

		builder.setItems(items == null ? new String[0] : items,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (listener != null) {
							listener.onItemSelected(dialog, items[which], which);
						}

						dialog.cancel();
					}
				});

		builder.show();
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showSingleChoiceItemsDialog(Context context, int itemsResId,
			int checkedItem, ItemSelectedListener listener) {
		showSingleChoiceItemsDialog(context, getStringArrayFromResource(context, itemsResId),
				checkedItem, listener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showSingleChoiceItemsDialog(Context context, String[] items,
			int checkedItem, ItemSelectedListener listener) {
		showSingleChoiceItemsDialog(context, null, items, checkedItem, listener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showSingleChoiceItemsDialog(Context context, int titleResId, int itemsResId,
			int checkedItem, ItemSelectedListener listener) {
		showSingleChoiceItemsDialog(context, getStringFromResource(context, titleResId),
				getStringArrayFromResource(context, itemsResId), checkedItem, listener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showSingleChoiceItemsDialog(Context context, int titleResId, String[] items,
			int checkedItem, ItemSelectedListener listener) {
		showSingleChoiceItemsDialog(context, getStringFromResource(context, titleResId), items,
				checkedItem, listener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 */
	public static void showSingleChoiceItemsDialog(Context context, String title, String[] items,
			int checkedItem, ItemSelectedListener listener) {
		showSingleChoiceItemsDialog(context, title, ICON_RES_ID_DEFAULT, items, checkedItem,
				listener, STRING_RES_ID_NULL, null);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showSingleChoiceItemsDialog(Context context, int titleResId, int iconResId,
			int itemsResId, int checkedItem, ItemSelectedListener listener, int negativeId,
			DialogInterface.OnClickListener negativeListener) {
		showSingleChoiceItemsDialog(context, getStringFromResource(context, titleResId), iconResId,
				getStringArrayFromResource(context, itemsResId), checkedItem, listener, negativeId,
				negativeListener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showSingleChoiceItemsDialog(Context context, int titleResId, int iconResId,
			String[] items, int checkedItem, ItemSelectedListener listener, int negativeId,
			DialogInterface.OnClickListener negativeListener) {
		showSingleChoiceItemsDialog(context, getStringFromResource(context, titleResId), iconResId,
				items, checkedItem, listener, negativeId, negativeListener);
	}

	/**
	 * 显示单选列表对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的Item, -1表示无默认选中项
	 * @param listener
	 *            Item点击事件，null则不处理事件
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showSingleChoiceItemsDialog(Context context, String title, int iconResId,
			final String[] items, int checkedItem, final ItemSelectedListener listener,
			int negativeId, DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		builder.setSingleChoiceItems(items == null ? new String[0] : items, checkedItem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (listener != null) {
							listener.onItemSelected(dialog, items[which], which);
						}

						dialog.cancel();
					}
				});
		builder.setNegativeButton(negativeId == STRING_RES_ID_NULL ? android.R.string.cancel
				: negativeId, negativeListener);
		builder.show();
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, int titleResId, int itemsResId,
			boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, titleResId, getStringArrayFromResource(context, itemsResId),
				checkedItem, listener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, int titleResId, String[] items,
			boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, getStringFromResource(context, titleResId), items,
				checkedItem, listener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, String title, String[] items,
			boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, null, title, items, checkedItem, listener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, View triggerView, int titleResId,
			int itemsResId, boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, triggerView, titleResId,
				getStringArrayFromResource(context, itemsResId), checkedItem, listener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, View triggerView, int titleResId,
			String[] items, boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, triggerView, getStringFromResource(context, titleResId),
				items, checkedItem, listener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param title
	 *            标题，若无则null
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 */
	public static void showMultiChoiceDialog(Context context, View triggerView, String title,
			String[] items, boolean[] checkedItem, ItemMultiChoiceChangedListener listener) {
		showMultiChoiceDialog(context, triggerView, title, ICON_RES_ID_DEFAULT, items, checkedItem,
				listener, STRING_RES_ID_NULL, STRING_RES_ID_NULL, null);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param itemsResId
	 *            列表数据数组资源ID
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showMultiChoiceDialog(Context context, View triggerView, int titleResId,
			int iconResId, int itemsResId, boolean[] checkedItem,
			ItemMultiChoiceChangedListener listener, int positiveId, int negativeId,
			DialogInterface.OnClickListener negativeListener) {
		showMultiChoiceDialog(context, triggerView, titleResId, iconResId,
				getStringArrayFromResource(context, itemsResId), checkedItem, listener, positiveId,
				negativeId, negativeListener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showMultiChoiceDialog(Context context, View triggerView, int titleResId,
			int iconResId, String[] items, boolean[] checkedItem,
			ItemMultiChoiceChangedListener listener, int positiveId, int negativeId,
			DialogInterface.OnClickListener negativeListener) {
		showMultiChoiceDialog(context, triggerView, getStringFromResource(context, titleResId),
				iconResId, items, checkedItem, listener, positiveId, negativeId, negativeListener);
	}

	/**
	 * 显示多选列表对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            触发对话框显示的控件，可以任意指定，可为null
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param items
	 *            列表数据数组
	 * @param checkedItem
	 *            默认选中的项, 数组值true表示选中，false未选中
	 * @param listener
	 *            确认按钮点击事件，null则不处理事件
	 * @param positiveId
	 *            确认按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeId
	 *            取消按钮文字，{@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 * @param negativeListener
	 *            取消按钮点击事件，null则不处理事件 {@link DialogUtils#ICON_RES_ID_DEFAULT}
	 *            表示使用程序图标
	 */
	public static void showMultiChoiceDialog(Context context, final View triggerView, String title,
			int iconResId, final String[] items, final boolean[] checkedItem,
			final ItemMultiChoiceChangedListener listener, int positiveId, int negativeId,
			DialogInterface.OnClickListener negativeListener) {
		final boolean[] newCheckedItem = checkedItem.clone();

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		builder.setMultiChoiceItems(items == null ? new String[0] : items, checkedItem,
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						newCheckedItem[which] = isChecked;
					}
				});
		builder.setPositiveButton(positiveId == STRING_RES_ID_NULL ? android.R.string.ok
				: positiveId, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int i) {
				if (listener != null) {
					listener.onMutiItemChoiceChanged(dialog, triggerView, newCheckedItem);
				}
			}
		});
		builder.setNegativeButton(positiveId == STRING_RES_ID_NULL ? android.R.string.cancel
				: positiveId, negativeListener);
		builder.show();
	}

	/**
	 * Show time choose dialog.
	 * 
	 * @param context
	 *            the context
	 * @param triggerView
	 *            the trigger view
	 * @param titleId
	 *            the title id
	 * @param hourOfDay
	 *            the hour of day
	 * @param minute
	 *            the minute
	 * @param timeChangedListener
	 *            the time changed listener
	 */
	public static void showTimeChooseDialog(Context context, View triggerView, int titleId,
			int hourOfDay, int minute, TimeChangedListener timeChangedListener) {
		showTimeChooseDialog(context, triggerView, titleId, timeChangedListener, hourOfDay, minute,
				true);
	}

	/**
	 * Show time choose dialog.
	 * 
	 * @param context
	 *            the context
	 * @param triggerView
	 *            the trigger view
	 * @param titleResId
	 *            the title res id
	 * @param timeChangedListener
	 *            the time changed listener
	 * @param hourOfDay
	 *            the hour of day
	 * @param minute
	 *            the minute
	 * @param is24HourView
	 *            the is24 hour view
	 */
	public static void showTimeChooseDialog(Context context, View triggerView, int titleResId,
			TimeChangedListener timeChangedListener, int hourOfDay, int minute, boolean is24HourView) {
		showTimeChooseDialog(context, triggerView, getStringFromResource(context, titleResId),
				ICON_RES_ID_SYSTEM, timeChangedListener, hourOfDay, minute, is24HourView);
	}

	/**
	 * 显示时间选择对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param triggerView
	 *            the trigger view
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param timeChangedListener
	 *            时间改变监听事件，null则不处理事件
	 * @param hourOfDay
	 *            默认显示的小时值
	 * @param minute
	 *            默认显示的分钟值
	 * @param is24HourView
	 *            true显示成24小时格式, false显示上午下午格式
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 */
	public static void showTimeChooseDialog(Context context, final View triggerView, String title,
			int iconResId, final TimeChangedListener timeChangedListener, final int hourOfDay,
			final int minute, boolean is24HourView) {
		TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int newHourOfDay, int newMinute) {
				if (timeChangedListener != null
						&& (hourOfDay != newHourOfDay || minute != newMinute)) {
					timeChangedListener.onTimeChanged(triggerView, newHourOfDay, newMinute);
				}
			}
		};

		TimePickerDialog dialog = new TimePickerDialog(context, listener, hourOfDay, minute,
				is24HourView);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			dialog.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			dialog.setIcon(iconResId);
		}

		dialog.setButton(context.getText(android.R.string.ok), dialog);

		if (title != null) {
			dialog.setTitle(title);
		}

		dialog.show();
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            等待提示内容资源ID
	 */
	public static void showProgressDialog(Context context, int msgResId) {
		showProgressDialog(context, getStringFromResource(context, msgResId));
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            等待提示内容
	 */
	public static void showProgressDialog(Context context, String msg) {
		showProgressDialog(context, msg, false);
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            等待提示内容资源ID
	 * @param cancelable
	 *            能否按返回键关闭
	 */
	public static void showProgressDialog(Context context, int msgResId, boolean cancelable) {
		showProgressDialog(context, getStringFromResource(context, msgResId), cancelable);
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            等待提示内容
	 * @param cancelable
	 *            能否按返回键关闭
	 */
	public static void showProgressDialog(Context context, String msg, boolean cancelable) {
		showProgressDialog(context, msg, cancelable, null, null);
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msgResId
	 *            等待提示内容资源ID
	 * @param cancelable
	 *            能否按返回键关闭
	 * @param cancelListener
	 *            按返回键关闭对话框事件监听
	 * @param dismissListener
	 *            对话框关闭事件监听
	 */
	public static void showProgressDialog(Context context, int msgResId, boolean cancelable,
			OnCancelListener cancelListener, OnDismissListener dismissListener) {
		showProgressDialog(context, getStringFromResource(context, msgResId), cancelable,
				cancelListener, dismissListener);
	}

	/**
	 * 显示进度对话框。.
	 * 
	 * @param context
	 *            上下文
	 * @param msg
	 *            等待提示内容
	 * @param cancelable
	 *            能否按返回键关闭
	 * @param cancelListener
	 *            按返回键关闭对话框事件监听
	 * @param dismissListener
	 *            对话框关闭事件监听
	 */
	public static void showProgressDialog(Context context, String msg, boolean cancelable,
			OnCancelListener cancelListener, OnDismissListener dismissListener) {
		dismissProgressDialog();

		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnCancelListener(cancelListener);
		progressDialog.setOnDismissListener(dismissListener);
		progressDialog.setMessage(msg);

		progressDialog.show();
	}

	/**
	 * 更新显示进度对话框。.
	 * 
	 * @param msg
	 *            the msg
	 */
	public static void updateProgressDialog(String msg) {
		if (progressDialog != null) {
			progressDialog.setMessage(msg);
		}
	}

	/**
	 * 取消显示进度对话框。.
	 */
	public static void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param viewLayoutResId
	 *            用户指定控件布局文件资源ID
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, int viewLayoutResId,
			CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, STRING_RES_ID_NULL, viewLayoutResId, listener, buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param view
	 *            用户指定控件
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, View view, CustomListener listener,
			int... buttonResIds) {
		showCustomDialog(context, null, view, listener, buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param viewLayoutResId
	 *            用户指定控件布局文件资源ID
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, int titleResId, int viewLayoutResId,
			CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, titleResId, ICON_RES_ID_DEFAULT, viewLayoutResId, listener,
				buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param viewLayoutResId
	 *            用户指定控件布局文件资源ID
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, String title, int viewLayoutResId,
			CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, title, ICON_RES_ID_DEFAULT, viewLayoutResId, listener,
				buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param view
	 *            用户指定控件
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, String title, View view,
			CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, title, ICON_RES_ID_DEFAULT, view, listener, buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param titleResId
	 *            标题资源ID，若无则{@link DialogUtils#STRING_RES_ID_NULL}
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param viewLayoutResId
	 *            用户指定控件布局文件资源ID
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, int titleResId, int iconResId,
			int viewLayoutResId, CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, getStringFromResource(context, titleResId), iconResId,
				viewLayoutResId, listener, buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param viewLayoutResId
	 *            用户指定控件布局文件资源ID
	 * @param listener
	 *            确认按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, String title, int iconResId,
			int viewLayoutResId, CustomListener listener, int... buttonResIds) {
		showCustomDialog(context, title, iconResId, getViewFromResource(context, viewLayoutResId),
				listener, buttonResIds);
	}

	/**
	 * 显示用户自定义对方框。.
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            标题，若无则null
	 * @param iconResId
	 *            标题图标，{@link DialogUtils#ICON_RES_ID_SYSTEM}表示使用系统默认，
	 * @param view
	 *            用户指定控件
	 * @param listener
	 *            按钮点击和界面初始化事件，null则不处理事件
	 * @param buttonResIds
	 *            按钮显示字符资源ID，如果数组长度为0则只有一个确认按钮，如果数组长度为1则只有一个用户指定字符的确认按钮，
	 *            如果数组长度为2则是确认按钮和取消按钮，如果数组长度为3则包括确认取消中立按钮，超过3个的无效。
	 *            {@link DialogUtils#ICON_RES_ID_DEFAULT}表示使用程序图标
	 *            {@link DialogUtils#STRING_RES_ID_NULL}则使用系统默认
	 */
	public static void showCustomDialog(Context context, String title, int iconResId,
			final View view, final CustomListener listener, int... buttonResIds) {
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) {
					listener.onClicked(dialog, view, which);
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);

		if (iconResId == ICON_RES_ID_DEFAULT) {
			builder.setIcon(DEFAULT_DIALOG_ICON);
		} else if (iconResId != ICON_RES_ID_SYSTEM) {
			builder.setIcon(iconResId);
		}

		builder.setView(view);
		builder.setCancelable(true);

		try {
			if (buttonResIds.length == 0) {
				builder.setPositiveButton(android.R.string.ok, onClickListener);
			} else {
				builder.setPositiveButton(buttonResIds[0], onClickListener);
				builder.setNegativeButton(buttonResIds[1], onClickListener);
				builder.setNeutralButton(buttonResIds[2], onClickListener);
			}
		} catch (Exception e) {
			// 使用异常捕获的方式来兼容按钮设置。
		}

		if (listener != null) {
			listener.init(view);
		}

		builder.show();
	}

	/**
	 * 用于获取标题或者消息的字符串，如果资源ID无效，返回null.
	 * 
	 * @param context
	 *            上下文
	 * @param resId
	 *            字符串资源ID
	 * @return the string from resource
	 */
	public static String getStringFromResource(Context context, int resId) {
		String str = null;

		if (resId != STRING_RES_ID_NULL) {
			try {
				Resources res = context.getResources();
				str = res.getString(resId);
			} catch (Resources.NotFoundException e) {
				// 屏蔽资源ID无效的异常情况
			}
		}

		return str;
	}

	/**
	 * 用于列表对话框（列表、单选、多选）字符串数组，如果资源ID无效，返回null.
	 * 
	 * @param context
	 *            上下文
	 * @param itemsResId
	 *            字符串数组资源ID
	 * @return the string array from resource
	 */
	public static String[] getStringArrayFromResource(Context context, int itemsResId) {
		String[] items = null;

		if (itemsResId != STRING_ARRAY_RES_ID_NULL) {
			try {
				Resources res = context.getResources();
				items = res.getStringArray(itemsResId);
			} catch (Resources.NotFoundException e) {
				// 屏蔽资源ID无效的异常情况
			}
		}

		return items;
	}

	/**
	 * 用于用户自定义对话框，如果资源ID无效，返回null.
	 * 
	 * @param context
	 *            上下文
	 * @param layoutResId
	 *            布局文件资源ID
	 * @return the view from resource
	 */
	public static View getViewFromResource(Context context, int layoutResId) {
		View view = null;

		try {
			view = LayoutInflater.from(context).inflate(layoutResId, null);
		} catch (Resources.NotFoundException e) {
			// 屏蔽资源ID无效的异常情况
		}

		return view;
	}

	/**
	 * 该接口定义”带开关的消息对话框“的监听事件。.
	 * 
	 * @author 张代松 2012-5-21
	 */
	public interface CheckBoxMessageListener {

		/**
		 * 该方法在用户点击对话框按钮时调用。.
		 * 
		 * @param dialog
		 *            消息对话框对象
		 * @param which
		 *            点击的按钮，共3种：确认按钮{@link DialogUtils#DialogInterface.BUTTON_POSITIVE}，取消按钮
		 * @param checkBoxIsOn
		 *            开关选项是否打开 {@link DialogUtils#DialogInterface.BUTTON_NEGATIVE}， 中立按钮
		 *            {@link DialogUtils#DialogInterface.BUTTON_NEUTRAL}
		 */
		public void onClicked(DialogInterface dialog, int which, boolean checkBoxIsOn);
	}

	/**
	 * 该接口定义“列表对方框”和“单选对话框”的监听事件。.
	 * 
	 * @author 张代松 2011-9-4
	 */
	public interface ItemSelectedListener {

		/**
		 * 该方法在用户点击对话框列表项时调用。.
		 * 
		 * @param dialog
		 *            对话框实例对象
		 * @param text
		 *            所点击的列表项字符串
		 * @param which
		 *            所点击的列表项位置
		 */
		public void onItemSelected(DialogInterface dialog, String text, int which);
	}

	/**
	 * 该接口定义“多选对话框”的监听事件。.
	 * 
	 * @author 张代松 2011-9-4
	 */
	public interface ItemMultiChoiceChangedListener {

		/**
		 * 该方法在用户点击对话框确定按钮时调用。.
		 * 
		 * @param dialog
		 *            对话框实例对象
		 * @param triggerView
		 *            触发对话框显示的控件，显示对话框方法中指定的控件，不指定为null
		 * @param checkedItem
		 *            选中的项, 数组值true表示选中，false未选中
		 */
		public void onMutiItemChoiceChanged(DialogInterface dialog, View triggerView,
				boolean[] checkedItem);
	}

	/**
	 * 该接口定义“时间选择对话框”的监听事件。.
	 * 
	 * @author 张代松 2011-9-4
	 */
	public interface TimeChangedListener {

		/**
		 * 该方法在用户点击对话框确定按钮并且设置时间改变时调用。.
		 * 
		 * @param triggerView
		 *            触发对话框显示的控件，显示对话框方法中指定的控件，不指定为null
		 * @param hourOfDay
		 *            设置的小时值
		 * @param minute
		 *            设置的分钟值
		 */
		public void onTimeChanged(View triggerView, int hourOfDay, int minute);
	}

	/**
	 * 该接口定义“带开关的时间选择对话框”的监听事件。.
	 * 
	 * @author 张代松 2011-9-4
	 */
	public interface TimeCheckBoxChangedListener {

		/**
		 * 该方法用在用户点击对话框确定按钮，如果设置时间改变或者开关状态改变时，才会调用。.
		 * 
		 * @param triggerView
		 *            触发对话框显示的控件，显示对话框方法中指定的控件，不指定为null
		 * @param hourOfDay
		 *            设置的小时值
		 * @param minute
		 *            设置的分钟值
		 * @param isOn
		 *            开关状态
		 */
		public void onTimeSetChanged(View triggerView, int hourOfDay, int minute, boolean isOn);
	}

	/**
	 * 该接口定义”用户自定义对话框“的监听事件。.
	 * 
	 * @author 张代松 2012-5-21
	 */
	public interface CustomListener {

		/**
		 * 该方法用于用户初始化界面数据，该方法会在对话框显示前调用。.
		 * 
		 * @param view
		 *            用户指定对话框显示的控件
		 */
		public void init(View view);

		/**
		 * 该方法在用户点击对话框按钮时调用。.
		 * 
		 * @param dialog
		 *            对话框对象
		 * @param view
		 *            用户指定对话框显示的控件
		 * @param which
		 *            点击的按钮，共3种：确认按钮{@link DialogUtils#DialogInterface.BUTTON_POSITIVE}，取消按钮
		 *            {@link DialogUtils#DialogInterface.BUTTON_NEGATIVE}， 中立按钮
		 *            {@link DialogUtils#DialogInterface.BUTTON_NEUTRAL}
		 */
		public void onClicked(DialogInterface dialog, View view, int which);
	}

	/**
	 * 该类定义对话框按钮默认点击事件，对话框按返回键取消事件，对话框关闭事件。 使用时可重载相应方法实现确认按钮点击监听、取消按钮监听、中立按钮监听、
	 * 按返回键监听、对话框关闭监听。.
	 * 
	 * @author 张代松 2012-6-1
	 */
	public static class DefaultDialogListener implements DialogInterface.OnClickListener,
			DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.DialogInterface.OnClickListener#onClick(android.content
		 * .DialogInterface, int)
		 */
		@Override
		public final void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				onPositiveClick(dialog);

				break;
			case DialogInterface.BUTTON_NEGATIVE:
				onNegativeClick(dialog);

				break;
			case DialogInterface.BUTTON_NEUTRAL:
				onNeutralClick(dialog);

				break;
			default:
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.DialogInterface.OnCancelListener#onCancel(android
		 * .content.DialogInterface)
		 */
		@Override
		public void onCancel(DialogInterface dialog) {
			// 重载后实现
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.content.DialogInterface.OnDismissListener#onDismiss(android
		 * .content.DialogInterface)
		 */
		@Override
		public void onDismiss(DialogInterface dialog) {
			// 重载后实现
		}

		/**
		 * 点击确认按钮执行此方法。.
		 * 
		 * @param dialog
		 *            the dialog
		 */
		public void onPositiveClick(DialogInterface dialog) {
			// 重载后实现
		}

		/**
		 * 点击取消按钮执行此方法。.
		 * 
		 * @param dialog
		 *            the dialog
		 */
		public void onNegativeClick(DialogInterface dialog) {
			// 重载后实现
		}

		/**
		 * 点击中立按钮执行此方法。.
		 * 
		 * @param dialog
		 *            the dialog
		 */
		public void onNeutralClick(DialogInterface dialog) {
			// 重载后实现
		}
	}
}
