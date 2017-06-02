package com.d.dmusic.mvp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.d.dmusic.MainActivity;
import com.d.dmusic.R;
import com.d.dmusic.model.AlbumModel;
import com.d.dmusic.model.FolderModel;
import com.d.dmusic.model.SingerModel;
import com.d.dmusic.module.events.MusicModelEvent;
import com.d.dmusic.module.events.RefreshEvent;
import com.d.dmusic.module.greendao.db.MusicDB;
import com.d.dmusic.module.greendao.music.base.MusicModel;
import com.d.dmusic.module.service.MusicControl;
import com.d.dmusic.module.service.MusicService;
import com.d.dmusic.mvp.activity.HandleActivity;
import com.d.dmusic.mvp.adapter.SongAdapter;
import com.d.dmusic.view.DSLayout;
import com.d.dmusic.view.SongHeaderView;
import com.d.dmusic.view.sort.SideBar;
import com.d.dmusic.view.sort.SortUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-本地歌曲-歌曲
 * Created by D on 2017/4/29.
 */
public class LMSongFragment extends AbstractLMFragment implements SongHeaderView.OnHeaderListener, SideBar.OnLetterChangedListener {
    private SongHeaderView header;
    private SongAdapter adapter;
    private SortUtil sortUtil;
    private boolean isNeedReLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();
        sortUtil = new SortUtil();
        adapter = new SongAdapter(context, new ArrayList<MusicModel>(), R.layout.adapter_song, MusicDB.LOCAL_ALL_MUSIC, this);
        header = new SongHeaderView(context);
        header.setVisibility(R.id.iv_header_song_handler, View.GONE);
        header.setVisibility(View.GONE);
        header.setOnHeaderListener(this);
    }

    @Override
    protected void onVisible() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        super.onVisible();
    }

    @Override
    protected void onInvisible() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onInvisible();
    }

    @Override
    protected void lazyLoad() {
        xrvList.showAsList();
        xrvList.setCanRefresh(false);
        xrvList.setCanLoadMore(false);
        xrvList.addHeaderView(header);
        xrvList.setAdapter(adapter);
        sbSideBar.setOnLetterChangedListener(this);
        mPresenter.getSong(MusicDB.LOCAL_ALL_MUSIC, sortUtil);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedReLoad) {
            isNeedReLoad = false;
            mPresenter.getSong(MusicDB.LOCAL_ALL_MUSIC, sortUtil);
        }
    }

    @Override
    public void setSong(List<MusicModel> models) {
        if (models.size() <= 0) {
            setDSState(DSLayout.STATE_EMPTY);
            sbSideBar.setVisibility(View.GONE);
        } else {
            setDSState(View.GONE);
            sbSideBar.setVisibility(View.VISIBLE);
        }
        notifyDataCountChanged(models.size());
        adapter.setDatas(models);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setSinger(List<SingerModel> models) {

    }

    @Override
    public void setAlbum(List<AlbumModel> models) {

    }

    @Override
    public void setFolder(List<FolderModel> models) {

    }

    @Override
    public void setDSState(int state) {
        dslDS.setState(state);
    }

    @Override
    public void notifyDataCountChanged(int count) {
        if (count <= 0) {
            header.setVisibility(View.GONE);
        } else {
            header.setSongCount(count);
            header.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayAll() {
        List<MusicModel> datas = adapter.getDatas();
        if (datas != null && datas.size() > 0) {
            MusicControl control = MusicService.getControl();
            control.init(datas, 0);
        }
    }

    @Override
    public void onHandle() {
        getActivity().startActivity(new Intent(getActivity(), HandleActivity.class));
    }

    @Override
    public void onChange(int index, String c) {
        sortUtil.onChange(index, c, xrvList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicModelEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()
                || event.type != MusicDB.LOCAL_ALL_MUSIC || mPresenter == null || !isLazyLoaded) {
            return;
        }
        setSong(event.list);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onRefreshEvent(RefreshEvent event) {
        if (event == null || getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        isNeedReLoad = true;
    }

    @Override
    public void onDestroy() {
        MainActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        super.onDestroy();
    }
}