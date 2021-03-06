package com.hyc.one.beans.music;

import android.support.v7.widget.RecyclerView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.hyc.one.beans.Comment;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class MusicRelateListBean {
    private boolean hasRequested;
    private RecyclerView mRecyclerView;
    private SwipeToLoadLayout layout;
    private String id;
    private List<MusicRelate> musics;
    private List<Comment> hotComment;
    private boolean hasMoreComment = true;
    private String lastIndex;


    public MusicRelateListBean(String id, List<MusicRelate> musics, List<Comment> hotComment, String lastIndex) {
        this.id = id;
        this.musics = musics;
        this.hotComment = hotComment;
        this.lastIndex = lastIndex;
    }


    public boolean hasRequested() {
        return hasRequested;
    }


    public void setHasRequested(boolean hasRequested) {
        this.hasRequested = hasRequested;
    }


    public SwipeToLoadLayout getLayout() {
        return layout;
    }


    public void setLayout(SwipeToLoadLayout layout) {
        this.layout = layout;
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }


    public boolean hasMoreComment() {
        return hasMoreComment;
    }


    public void setHasMoreComment(boolean hasMoreComment) {
        this.hasMoreComment = hasMoreComment;
    }


    public String getLastIndex() {
        return lastIndex;
    }


    public void setLastIndex(String lastIndex) {
        this.lastIndex = lastIndex;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public List<Comment> getHotComment() {
        return hotComment;
    }


    public void setHotComment(List<Comment> hotComment) {
        this.hotComment = hotComment;
    }


    public List<MusicRelate> getMusics() {
        return musics;
    }


    public void setMusics(List<MusicRelate> musics) {
        this.musics = musics;
    }

}
