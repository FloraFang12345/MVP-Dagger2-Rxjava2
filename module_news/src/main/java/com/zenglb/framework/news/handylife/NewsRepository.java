package com.zenglb.framework.news.handylife;

import android.util.Log;

import com.zenglb.framework.news.http.NewsApiService;
import com.zenglb.framework.news.http.result.ArticlesResult;
import com.zlb.httplib.BaseObserver;
import com.zlb.httplib.rxUtils.SwitchSchedulers;

import javax.inject.Inject;

/**
 * 这里就不做什么先加载本地的ORM DB 然后再加载 HTTP 了
 * <p>
 * Created by zlb on 2018/3/23.
 */
public class NewsRepository implements INewsDataSource {

    static int i=0;

    // Get the ApiService from dagger。 用dagger 来注入 ApiService
    @Inject
    NewsApiService apiService;

    @Inject
    public NewsRepository() {

    }

    public NewsRepository(NewsApiService apiService) {
        this.apiService = apiService;
    }


    /**
     * getHandyLifeData from http server
     *
     * @param type                      数据类型，{city guide,shop,eat}
     * @param page                      page index
     * @param loadNewsCallback the callBack
     */
    @Override
    public void getHandyLifeData(String type, int page, LoadNewsDataCallback loadNewsCallback) {
        //
        apiService.getArticles(type)
                .compose(SwitchSchedulers.applySchedulers())
                //BaseObserver 参数问题优化，如果不传参数context 的话，依赖context 的功能就要改
                .subscribe(new BaseObserver<ArticlesResult>(null) {
                    @Override
                    public void onSuccess(ArticlesResult lifeResultBeans) {

                        Log.e("Thread-main",++i+"   ---  "+Thread.currentThread().getName()+Thread.currentThread().getId());


                        if (null != loadNewsCallback) {
                            loadNewsCallback.onHandyLifeDataSuccess(lifeResultBeans);
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        Log.e("Thread-main",++i+"   ---  "+Thread.currentThread().getName()+Thread.currentThread().getId());

                        if (null != loadNewsCallback) {
                            loadNewsCallback.onHandyLifeDataFailed(code, message);
                        }

                    }
                });

    }

}
