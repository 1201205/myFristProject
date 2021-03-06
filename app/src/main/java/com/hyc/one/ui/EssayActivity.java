package com.hyc.one.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.hyc.one.R;
import com.hyc.one.base.BaseActivity;
import com.hyc.one.base.BasePresenter;
import com.hyc.one.base.PresenterFactory;
import com.hyc.one.base.PresenterLoader;
import com.hyc.one.beans.Comment;
import com.hyc.one.beans.Essay;
import com.hyc.one.beans.RealArticle;
import com.hyc.one.beans.RealArticleAuthor;
import com.hyc.one.beans.Song;
import com.hyc.one.event.NetWorkChangeEvent;
import com.hyc.one.event.PlayCallBackEvent;
import com.hyc.one.event.PlayEvent;
import com.hyc.one.player.ManagedMediaPlayer;
import com.hyc.one.player.MyPlayer;
import com.hyc.one.presenter.EssayContentPresenter;
import com.hyc.one.ui.adpter.CommentAdapter;
import com.hyc.one.ui.adpter.EssayAdapter;
import com.hyc.one.utils.AppUtil;
import com.hyc.one.utils.DateUtil;
import com.hyc.one.view.ReadingContentView;
import com.hyc.one.widget.CircleImageView;
import com.hyc.one.widget.ListViewForScrollView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 16/5/18.
 */
public class EssayActivity extends BaseActivity<EssayContentPresenter>
        implements ReadingContentView<Essay, RealArticle>,
        OnLoadMoreListener,
        LoaderManager.LoaderCallbacks<EssayContentPresenter> {
    private SwipeToLoadLayout swipeToLoadLayout;
    private View mHeader;
    private TextView mTitleTV;
    private TextView mAuthorDesTV;
    private TextView mAuthorTV;
    private TextView mDateTV;
    private TextView mContentTV;
    private TextView mEditorTV;
    private TextView mAuthorNameTV;
    private TextView mDesTV;
    private TextView mListenTV;
    private Button mPlayBt;
    private CircleImageView mHeaderIV;
    private CircleImageView mAuthorHeaderIV;
    private ListView listView;
    private ListViewForScrollView mRelateLV;
    private ListViewForScrollView mHotCommentsLV;
    private LinearLayout mRelateLL;
    private LinearLayout mHotLL;
    private LinearLayout mCommentLL;
    private CommentAdapter mCommentAdapter;
    private String mID;
    public static final String ID = "id";
    private boolean mHasMoreComments = true;
    private ManagedMediaPlayer.Status mMusicState = ManagedMediaPlayer.Status.IDLE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initLoader() {
        getSupportLoaderManager().initLoader(AppUtil.getID(), null, this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void handleIntent() {
        mID = getIntent().getStringExtra(ID);
    }


    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mHasMoreComments && view.getLastVisiblePosition() == view.getCount() - 1 &&
                            !ViewCompat.canScrollVertically(view, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mHeader = LayoutInflater.from(this).inflate(R.layout.essay_header, null);
        mTitleTV = (TextView) mHeader.findViewById(R.id.title_tv);
        mPlayBt = (Button) mHeader.findViewById(R.id.play_bt);
        mListenTV = (TextView) mHeader.findViewById(R.id.listen_tv);
        mDesTV = (TextView) mHeader.findViewById(R.id.des_tv);
        mAuthorTV = (TextView) mHeader.findViewById(R.id.name_tv);
        mContentTV = (TextView) mHeader.findViewById(R.id.content_tv);
        mAuthorDesTV = (TextView) mHeader.findViewById(R.id.author_des_tv);
        mDateTV = (TextView) mHeader.findViewById(R.id.date_tv);
        mHotLL = (LinearLayout) mHeader.findViewById(R.id.hot_ll);
        mCommentLL = (LinearLayout) mHeader.findViewById(R.id.comment_ll);
        mAuthorHeaderIV = (CircleImageView) mHeader.findViewById(R.id.author_head_iv);
        mHeaderIV = (CircleImageView) mHeader.findViewById(R.id.head_iv);
        mEditorTV = (TextView) mHeader.findViewById(R.id.editor_tv);
        mAuthorNameTV = (TextView) mHeader.findViewById(R.id.author_name_tv);
        mRelateLV = (ListViewForScrollView) mHeader.findViewById(R.id.relate_lv);
        mRelateLL = (LinearLayout) mHeader.findViewById(R.id.relate_ll);
        mHotCommentsLV = (ListViewForScrollView) mHeader.findViewById(R.id.hot_lv);
        listView.addHeaderView(mHeader);
        mCommentAdapter = new CommentAdapter();
        listView.setAdapter(mCommentAdapter);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        if (!AppUtil.hasConnect(this)) {
            changeVisibility(false);
        }
    }


    @Override
    protected int getLayoutID() {
        return R.layout.activity_question;
    }


    @Override
    public void showContent(final Essay content) {
        final RealArticleAuthor author = content.getAuthor().get(0);
        if (!TextUtils.isEmpty(author.getWeb_url())) {
            Picasso.with(this)
                    .load(author.getWeb_url())
                    .placeholder(R.drawable.head)
                    .into(mHeaderIV);
            Picasso.with(this)
                    .load(author.getWeb_url())
                    .placeholder(R.drawable.head)
                    .into(mAuthorHeaderIV);
        } else {
            mHeaderIV.setImageResource(R.drawable.head);
            mAuthorHeaderIV.setImageResource(R.drawable.head);
        }
        String id = author.getUser_id();
        View.OnClickListener listener = AppUtil.getOtherClickListener(id, this);
        mAuthorTV.setOnClickListener(listener);
        mAuthorNameTV.setOnClickListener(listener);
        mHeaderIV.setOnClickListener(listener);
        mAuthorHeaderIV.setOnClickListener(listener);
        if (TextUtils.isEmpty(content.getSub_title())) {
            mDesTV.setVisibility(View.GONE);
        } else {
            mDesTV.setText(content.getSub_title());
        }
        if (TextUtils.isEmpty(content.getAudio())) {
            mListenTV.setVisibility(View.GONE);
            mPlayBt.setVisibility(View.GONE);
        } else {
            ManagedMediaPlayer.Status status = MyPlayer.getPlayer()
                    .getSourceStatus(content.getAudio());
            mMusicState = status;
            if (status == ManagedMediaPlayer.Status.STARTED) {
                mPlayBt.setBackgroundResource(R.drawable.pause_selector);
            }
            mPlayBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicState == ManagedMediaPlayer.Status.STARTED) {
                        PlayEvent event = new PlayEvent();
                        event.setAction(PlayEvent.Action.PAUSE);
                        EventBus.getDefault().post(event);
                    } else if (mMusicState == ManagedMediaPlayer.Status.PAUSED) {
                        PlayEvent event = new PlayEvent();
                        event.setAction(PlayEvent.Action.RESUME);
                        EventBus.getDefault().post(event);
                    } else {
                        Song song = new Song("", content.getAudio());
                        List<Song> songs = new ArrayList<>();
                        songs.add(song);
                        PlayEvent event = new PlayEvent();
                        event.setQueue(songs);
                        event.setAction(PlayEvent.Action.PLAY);
                        EventBus.getDefault().post(event);
                    }
                }
            });

        }
        mTitleTV.setText(content.getHp_title());
        mAuthorDesTV.setText(author.getDesc());
        mDateTV.setText(DateUtil.getCommentDate(content.getHp_makettime()));
        mContentTV.setText(Html.fromHtml(content.getHp_content()));
        mEditorTV.setText(content.getHp_author_introduce());
        mAuthorTV.setText(content.getHp_author());
        mAuthorNameTV.setText(content.getHp_author());
    }

    @Subscribe
    public void onEvent(PlayCallBackEvent playEvent) {
        mMusicState = playEvent.state;
        switch (playEvent.getState()) {
            case STARTED:
                mPlayBt.setBackgroundResource(R.drawable.pause_selector);
                break;
            case STOPPED:
            case PAUSED:
                mPlayBt.setBackgroundResource(R.drawable.play_selector);
                break;
            default:
                break;

        }
    }


    @Override
    public void showRelate(List<RealArticle> realArticles) {
        if (realArticles == null || realArticles.size() == 0) {
            mRelateLL.setVisibility(View.GONE);
        } else {
            EssayAdapter adapter = new EssayAdapter(this, realArticles);
            mRelateLV.setAdapter(adapter);
            adapter.setItemClickListener(new EssayAdapter.OnReadingItemClickListener() {
                @Override
                public void onItemClicked(RealArticle s) {
                    jumpToNewEssay(s);
                }
            });
        }
    }


    private void jumpToNewEssay(RealArticle s) {
        Intent i = new Intent(this, EssayActivity.class);
        i.putExtra(EssayActivity.ID, s.getContent_id());
        startActivity(i);
    }


    @Override
    public void refreshCommentList(List<Comment> comments) {
        mCommentAdapter.refreshComments(comments);
        swipeToLoadLayout.setLoadingMore(false);

    }


    @Override
    protected String getTitleString() {
        return "短篇";
    }


    @Override
    public void showHotComments(List<Comment> comments) {
        if (comments == null || comments.size() == 0) {
            mHotLL.setVisibility(View.GONE);
        } else {
            CommentAdapter adapter = new CommentAdapter();
            mHotCommentsLV.setAdapter(adapter);
            adapter.refreshComments(comments);
        }

    }


    @Override
    public void showNoComments() {
        AppUtil.showToast(R.string.no_more);
        mHasMoreComments = false;
        swipeToLoadLayout.setLoadingMore(false);
        swipeToLoadLayout.setLoadMoreEnabled(false);
    }


    @Override
    public Loader<EssayContentPresenter> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<EssayContentPresenter>(this, new PresenterFactory() {
            @Override
            public BasePresenter create() {
                return new EssayContentPresenter(EssayActivity.this);
            }
        });
    }


    @Override
    public void onLoadFinished(Loader<EssayContentPresenter> loader, EssayContentPresenter data) {
        mPresenter = data;
        mPresenter.attachView();
        mPresenter.getAndShowContent(mID);
    }


    @Override
    public void onLoaderReset(Loader<EssayContentPresenter> loader) {
        mPresenter = null;
    }


    @Override
    public void onLoadMore() {
        mPresenter.getAndShowCommentList();
    }


    @Subscribe
    public void onEvent(NetWorkChangeEvent event) {
        if (NetWorkChangeEvent.hasNetWork) {
            if (mCommentLL.getVisibility() != View.VISIBLE) {
                changeVisibility(true);
            }
            if (mCommentAdapter.getCount() == 0) {
                mPresenter.getAndShowContent(mID);
            }
            if (mHasMoreComments) {
                swipeToLoadLayout.setLoadMoreEnabled(true);
            }
            swipeToLoadLayout.setLoadingMore(false);
        } else {
            swipeToLoadLayout.setLoadMoreEnabled(false);
        }
    }

    private void changeVisibility(boolean visible) {
        if (visible) {
            mCommentLL.setVisibility(View.VISIBLE);
            mRelateLL.setVisibility(View.VISIBLE);
            mHotLL.setVisibility(View.VISIBLE);
        } else {
            mCommentLL.setVisibility(View.GONE);
            mRelateLL.setVisibility(View.GONE);
            mHotLL.setVisibility(View.GONE);
            swipeToLoadLayout.setLoadMoreEnabled(false);
        }
    }
}
