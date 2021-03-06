package com.cyl.musiclake.ui.music.local.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cyl.musiclake.R;
import com.cyl.musiclake.base.BaseFragment;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.service.PlayManager;
import com.cyl.musiclake.ui.music.local.adapter.SongAdapter;
import com.cyl.musiclake.ui.music.local.contract.LoveContract;
import com.cyl.musiclake.ui.music.local.dialog.AddPlaylistDialog;
import com.cyl.musiclake.ui.music.local.dialog.ShowDetailDialog;
import com.cyl.musiclake.ui.music.local.presenter.LovePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Monkey on 2015/6/29.
 */
public class LoveFragment extends BaseFragment implements LoveContract.View {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private SongAdapter mAdapter;
    private List<Music> musicInfos = new ArrayList<>();
    private LovePresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recyclerview;
    }

    public static LoveFragment newInstance() {
        Bundle args = new Bundle();
        LoveFragment fragment = new LoveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("收藏");
        setHasOptionsMenu(true);
        if (getActivity() != null) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(mToolbar);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new SongAdapter(musicInfos);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerView);

        mPresenter = new LovePresenter(getContext());
        mPresenter.attachView(this);
    }

    @Override
    protected void loadData() {
        mPresenter.loadSongs();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showSongs(List<Music> songs) {
        musicInfos = songs;
        mAdapter.setNewData(songs);
    }

    @Override
    protected void listener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (view.getId() != R.id.iv_more) {
                PlayManager.setPlayList(musicInfos);
                PlayManager.play(position);
            }
        });
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        PlayManager.setPlayList(musicInfos);
                        PlayManager.play(position);
                        break;
                    case R.id.popup_song_detail:
                        ShowDetailDialog.newInstance((Music) adapter.getItem(position))
                                .show(getChildFragmentManager(), getTag());
                        break;
                    case R.id.popup_song_addto_queue:
                        AddPlaylistDialog.newInstance(musicInfos.get(position))
                                .show(getChildFragmentManager(), "ADD_PLAYLIST");
                        break;

                }
                return false;
            });
            popupMenu.inflate(R.menu.popup_album);
            popupMenu.show();
        });
    }

    @Override
    public void showEmptyView() {
        mAdapter.setEmptyView(R.layout.view_song_empty);
    }

    private void onBackPress() {
        getActivity().onBackPressed();
    }

}
