package com.youmi.android.offerdemo;

import net.youmi.android.AdManager;
import net.youmi.android.offers.EarnPointsOrderInfo;
import net.youmi.android.offers.EarnPointsOrderList;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.OffersWallDialogListener;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsEarnNotify;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.onlineconfig.OnlineConfigCallBack;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class YoumiOffersAdsDemo extends Activity implements OnClickListener, PointsChangeNotify,
		PointsEarnNotify {

	/**
	 * 显示积分余额的控件
	 */
	TextView mTextViewPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offers);

		// 初始化UI
		initUi();

		// (可选)关闭有米log输出，建议开发者在嵌入有米过程中不要关闭，以方便随时捕捉输出信息，出问题时可以快速定位问题
		// AdManager.getInstance(Context context).setEnableDebugLog(false);

		// 初始化接口，应用启动的时候调用，参数：appId, appSecret, 是否开启调试模式
		AdManager.getInstance(this).init("cfdbdd2786ea88ea ", "d8edde7d10dd0073", false);

		// 如果使用积分广告，请务必调用积分广告的初始化接口:
		OffersManager.getInstance(this).onAppLaunch();

		// （可选）注册积分监听-随时随地获得积分的变动情况
		PointsManager.getInstance(this).registerNotify(this);

		// (可选)开启用户数据统计服务,(sdk v4.08之后新增功能)默认不开启，传入false值也不开启，只有传入true才会调用
		AdManager.getInstance(this).setUserDataCollect(true);

		// （可选）注册积分订单赚取监听（sdk v4.10版本新增功能）
		PointsManager.getInstance(this).registerPointsEarnNotify(this);

		// (可选)设置是否在通知栏显示下载相关提示。默认为true，标识开启；设置为false则关闭。（sdk v4.10版本新增功能）
		// AdManager.setIsDownloadTipsDisplayOnNotification(false);

		// (可选)设置安装完成后是否在通知栏显示已安装成功的通知。默认为true，标识开启；设置为false则关闭。（sdk v4.10版本新增功能）
		// AdManager.setIsInstallationSuccessTipsDisplayOnNotification(false);

		// (可选)设置是否在通知栏显示积分赚取提示。默认为true，标识开启；设置为false则关闭。
		// PointsManager.setEnableEarnPointsNotification(false);

		// (可选)设置是否开启积分赚取的Toast提示。默认为true，标识开启；设置为false这关闭。
		// PointsManager.setEnableEarnPointsToastTips(false);
	}

	/**
	 * 退出时回收资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// （可选）注销积分监听-如果在onCreate注册了，那这里必须得注销
		PointsManager.getInstance(this).unRegisterNotify(this);

		// （可选）注销积分订单赚取监听-如果在onCreate注册了，那这里必须得注销（sdk v4.10版本新增功能）
		PointsManager.getInstance(this).unRegisterPointsEarnNotify(this);

		// 回收积分广告占用的资源
		OffersManager.getInstance(this).onAppExit();
	}

	/**
	 * 初始化ui
	 */
	private void initUi() {
		// demo绑定按钮事件
		findViewById(R.id.btn_show_offerswall).setOnClickListener(this);
		findViewById(R.id.btn_show_offerswall_dialog).setOnClickListener(this);
		findViewById(R.id.btn_award_points).setOnClickListener(this);
		findViewById(R.id.btn_spend_points).setOnClickListener(this);
		findViewById(R.id.btn_get_online_config).setOnClickListener(this);
		findViewById(R.id.btn_check_ad_config).setOnClickListener(this);

		// demo显示积分
		mTextViewPoints = (TextView) findViewById(R.id.pointsBalance);
		int pointsBalance = PointsManager.getInstance(this).queryPoints();// 查询积分余额
		mTextViewPoints.setText("积分余额:" + pointsBalance);
	}

	/**
	 * 积分余额发生变动时，就会回调本方法（本回调方法执行在UI线程中）
	 */
	@Override
	public void onPointBalanceChange(int pointsBalance) {
		mTextViewPoints.setText("积分余额:" + pointsBalance);
	}

	/**
	 * 积分订单赚取时会回调本方法（本方法执行在UI线程中）
	 */
	@Override
	public void onPointEarn(Context arg0, EarnPointsOrderList list) {
		// 遍历订单
		for (int i = 0; i < list.size(); ++i) {
			EarnPointsOrderInfo info = list.get(i);
			Toast.makeText(this, info.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		// 展示全屏的积分墙界面
		case R.id.btn_show_offerswall:
			OffersManager.getInstance(this).showOffersWall();
			break;

		// 展示对话框的积分墙界面(本方法支持多种重载格式，开发者可以参考文档或者使用代码提示快捷键来了解)
		case R.id.btn_show_offerswall_dialog:
			OffersManager.getInstance(this).showOffersWallDialog(this,
					new OffersWallDialogListener() {

						@Override
						public void onDialogClose() {
							Toast.makeText(YoumiOffersAdsDemo.this, "积分墙对话框关闭了", Toast.LENGTH_SHORT).show();

						}
					});
			break;

		// demo 奖励10积分, 注：调用本方法后，积分余额马上变更，可留意onPointBalanceChange是不是被调用了
		case R.id.btn_award_points:
			PointsManager.getInstance(this).awardPoints(10);
			break;

		// demo 消费20积分, 注：调用本方法后，积分余额马上变更，可留意onPointBalanceChange是不是被调用了
		case R.id.btn_spend_points:
			PointsManager.getInstance(this).spendPoints(20);
			break;

		// 获取在线参数
		case R.id.btn_get_online_config:
			Toast.makeText(YoumiOffersAdsDemo.this, "获取在线参数中...", Toast.LENGTH_LONG).show();

			// 注意：这里获取的在线参数的key为 ：isOpen，为演示的key ， 开发者需要将key替换为开发者在自己有米后台上面设置的key
			AdManager.getInstance(this).asyncGetOnlineConfig("isOpen",
					new OnlineConfigCallBack() {

						/**
						 * 获取在线参数成功就会回调本方法（本回调方法执行在UI线程中）
						 */
						@Override
						public void onGetOnlineConfigSuccessful(String key, String value) {
							// 获取在线参数成功
							Toast.makeText(YoumiOffersAdsDemo.this,
									String.format("在线参数获取结果：\nkey=%s, value=%s", key, value), Toast.LENGTH_LONG).show();

							// // 开发者在这里可以判断一下获取到的value值，然后设置一个boolean值，每次调用广告之前判断一下是否可以展示广告
							// if (key.equals("isOpen")) {
							// if (value.equals("1")) {
							// // 如果满足开发者自己的定义：如示例中如果key=isOpen value=1 则定义为开启广告
							// // 则将flag（boolean值）设置为true，然后每次调用广告代码之前都判断一下flag，如果flag为true则执行展示广告的代码
							// flag = true;
							// }
							// }

						}

						/**
						 * 获取在线参数失败就会回调本方法（本回调方法执行在UI线程中）
						 */
						@Override
						public void onGetOnlineConfigFailed(String key) {
							// 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
							Toast.makeText(YoumiOffersAdsDemo.this,
									String.format("在线参数获取结果：\n获取在线key=%s失败!\n具体失败原因请查看log，log标签：YoumiSdk", key),
									Toast.LENGTH_LONG).show();
						}
					});
			break;

		// 检查积分墙广告配置
		case R.id.btn_check_ad_config:
			StringBuilder sb = new StringBuilder();

			// 检查广告配置（不使用自定义积分账户）
			boolean isCorrect = OffersManager.getInstance(this).checkOffersAdConfig();

			// 检查广告配置（使用了用自定义积分账户）
			// boolean isCorrect = OffersManager.getInstance(this).checkOffersAdConfig(true);

			String adResult = "广告配置结果：正常";
			if (!isCorrect) {
				adResult = "广告配置结果：异常，具体异常请查看log，log标签：YoumiSdk";
			}
			sb.append(adResult);

			sb.append(String.format("\n\n是否已经开启服务器回调：%b", OffersManager.isUsingServerCallBack()));
			sb.append(String.format("\n\n是否在通知栏显示下载相关通知：%b",
					AdManager.isDownloadTipsDisplayOnNotification()));
			sb.append(String.format("\n\n是否在通知栏显示安装成功的通知：%b",
					AdManager.isInstallationSuccessTipsDisplayOnNotification()));
			sb.append(String.format("\n\n是否在通知栏显示积分赚取提示：%b",
					PointsManager.isEnableEarnPointsNotification()));
			sb.append(String.format("\n\n是否开启积分赚取的Toast提示：%b",
					PointsManager.isEnableEarnPointsToastTips()));

			// Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
			new AlertDialog.Builder(this).setTitle("检查结果").setMessage(sb.toString())
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}

					}).create().show();
			break;

		default:
			break;
		}
	}
}
