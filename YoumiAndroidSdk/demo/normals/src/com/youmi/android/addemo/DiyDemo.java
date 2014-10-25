package com.youmi.android.addemo;

import java.util.List;

import net.youmi.android.diy.AdObject;
import net.youmi.android.diy.DiyManager;
import net.youmi.android.diy.banner.DiyAdSize;
import net.youmi.android.diy.banner.DiyBanner;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class DiyDemo extends Activity {

    private Button showRecommendWallBtn, showRecommendAppWallBtn, showRecommendGameWallBtn;
    private List<AdObject> adList;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_diydemo);


        //普通布局，适用于应用
        //获取要嵌入迷你广告条的布局
        RelativeLayout adLayout=(RelativeLayout)findViewById(R.id.adLayout);
        //demo 1 迷你Banner : 宽满屏，高32dp
        DiyBanner banner = new DiyBanner(this, DiyAdSize.SIZE_MATCH_SCREENx32);//传入高度为32dp的AdSize来定义迷你Banner    
        //demo 2 迷你Banner : 宽320dp，高32dp
        //DiyBanner banner = new DiyBanner(this, DiyAdSize.SIZE_320x32);//传入高度为32dp的AdSize来定义迷你Banner 
        //将积分Banner加入到布局中
        adLayout.addView(banner);

        
        //悬浮布局，适用于游戏
        /*
	        //实例化LayoutParams(重要)
	        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
	            FrameLayout.LayoutParams.FILL_PARENT,
	            FrameLayout.LayoutParams.WRAP_CONTENT);     
	        //设置迷你Banner的悬浮位置
	        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角  
	        //实例化迷你Banner
	        DiyBanner banner = new DiyBanner(this, DiyAdSize.SIZE_MATCH_SCREENx32);//传入高度为32dp的DiyAdSize来定义迷你Banner
	        //调用Activity的addContentView函数
	        this.addContentView(banner, layoutParams);
        */

        showRecommendWallBtn = (Button) findViewById(R.id.showRecommendWall);
        showRecommendWallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	// 展示所有应用推荐墙
            	DiyManager.showRecommendWall(DiyDemo.this);
            }
        });
        
        showRecommendAppWallBtn = (Button) findViewById(R.id.showRecommendAppWall);
        showRecommendAppWallBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		// 展示应用推荐墙
        		DiyManager.showRecommendAppWall(DiyDemo.this);
        	}
        });
        
        showRecommendGameWallBtn = (Button) findViewById(R.id.showRecommendGameWall);
        showRecommendGameWallBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		// 展示游戏推荐墙
        		DiyManager.showRecommendGameWall(DiyDemo.this);
        	}
        });


    }
}
