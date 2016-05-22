package com.hyc.zhihu.ui;

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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.hyc.zhihu.R;
import com.hyc.zhihu.base.BaseActivity;
import com.hyc.zhihu.base.BasePresenter;
import com.hyc.zhihu.base.PresenterFactory;
import com.hyc.zhihu.base.PresenterLoader;
import com.hyc.zhihu.beans.Comment;
import com.hyc.zhihu.beans.Essay;
import com.hyc.zhihu.beans.Question;
import com.hyc.zhihu.beans.RealArticle;
import com.hyc.zhihu.beans.RealArticleAuthor;
import com.hyc.zhihu.beans.Song;
import com.hyc.zhihu.event.PlayCallBackEvent;
import com.hyc.zhihu.event.PlayEvent;
import com.hyc.zhihu.player.MyPlayer;
import com.hyc.zhihu.player.PlayerService;
import com.hyc.zhihu.presenter.EssayContentPresenter;
import com.hyc.zhihu.ui.adpter.CommentAdapter;
import com.hyc.zhihu.ui.adpter.EssayAdapter;
import com.hyc.zhihu.utils.AppUtil;
import com.hyc.zhihu.view.ReadingContentView;
import com.hyc.zhihu.widget.CircleImageView;
import com.hyc.zhihu.widget.CircleTransform;
import com.hyc.zhihu.widget.ListViewForScrollView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ray on 16/5/18.
 */
public class EssayActivity extends BaseActivity implements ReadingContentView<Essay, RealArticle>, OnLoadMoreListener, LoaderManager.LoaderCallbacks<EssayContentPresenter> {
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
    private EssayContentPresenter mPresenter;
    private CircleImageView mHeaderIV;
    private CircleImageView mAuthorHeaderIV;
    private ListView listView;
    private ListViewForScrollView mRelateLV;
    private ListViewForScrollView mHotCommentsLV;
    private LinearLayout mRelateLL;
    private CommentAdapter mCommentAdapter;
    private String mID;
    public static final String ID = "id";
    private boolean mHasMoreComments = true;
    private CircleTransform mTransform = new CircleTransform();
    private int mMusicState=PlayCallBackEvent.IDLE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(125, null, this);
        EventBus.getDefault().register(this);
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
                    if (mHasMoreComments && view.getLastVisiblePosition() == view.getCount() - 1 && !ViewCompat.canScrollVertically(view, 1)) {
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
        mAuthorHeaderIV = (CircleImageView) mHeader.findViewById(R.id.author_head_iv);
        mHeaderIV = (CircleImageView) mHeader.findViewById(R.id.head_iv);
        mEditorTV = (TextView) mHeader.findViewById(R.id.editor_tv);
        mAuthorNameTV = (TextView) mHeader.findViewById(R.id.author_name_tv);
        mRelateLV = (ListViewForScrollView) mHeader.findViewById(R.id.relate_lv);
        mRelateLL = (LinearLayout) mHeader.findViewById(R.id.relate_ll);
        mHotCommentsLV = (ListViewForScrollView) mHeader.findViewById(R.id.hot_lv);
        listView.addHeaderView(mHeader);
        mCommentAdapter = new CommentAdapter(this);
        listView.setAdapter(mCommentAdapter);
        swipeToLoadLayout.setOnLoadMoreListener(this);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_question;
    }


    @Override
    public void showContent(final Essay content) {
        RealArticleAuthor author = content.getAuthor().get(0);
        if (!TextUtils.isEmpty(author.getWeb_url())) {
            Picasso.with(this).load(author.getWeb_url()).placeholder(R.drawable.head).into(mHeaderIV);
            Picasso.with(this).load(author.getWeb_url()).placeholder(R.drawable.head).into(mAuthorHeaderIV);
        }else {
            mHeaderIV.setImageResource(R.drawable.head);
            mAuthorHeaderIV.setImageResource(R.drawable.head);
        }
        if (TextUtils.isEmpty(content.getSub_title())) {
            mDesTV.setVisibility(View.GONE);
        } else {
            mDesTV.setText(content.getSub_title());
        }
        if (TextUtils.isEmpty(content.getAudio())) {
            mListenTV.setVisibility(View.GONE);
            mPlayBt.setVisibility(View.GONE);
        }else {
            mPlayBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicState == PlayCallBackEvent.PLAY) {
                        PlayEvent event=new PlayEvent();
                        event.setAction(PlayEvent.Action.STOP);
                        EventBus.getDefault().post(event);
                    } else {
                        Song song=new Song("",content.getAudio());
                        List<Song> songs=new ArrayList<>();
                        songs.add(song);
                        PlayEvent event=new PlayEvent();
                        event.setQueue(songs);
                        event.setAction(PlayEvent.Action.PLAY);
                        EventBus.getDefault().post(event);
                    }

                }
            });

        }
        mTitleTV.setText(content.getHp_title());
        mAuthorDesTV.setText(author.getDesc());
        mDateTV.setText(content.getHp_makettime());
        mContentTV.setText(Html.fromHtml(content.getHp_content()));
        mEditorTV.setText(content.getHp_author_introduce());
        mAuthorTV.setText(content.getHp_author());
        mAuthorNameTV.setText(content.getHp_author());
    }
    @Subscribe
    public void onEvent(PlayCallBackEvent playEvent) {
        mMusicState=playEvent.state;
        switch (playEvent.state){
            case PlayCallBackEvent.PLAY:
                mPlayBt.setBackgroundResource(R.drawable.pause_selector);
                break;
            case PlayCallBackEvent.PAUSE:
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
        Intent i=new Intent(this,EssayActivity.class);
        i.putExtra(EssayActivity.ID,s.getContent_id());
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
        CommentAdapter adapter = new CommentAdapter(this);
        mHotCommentsLV.setAdapter(adapter);
        adapter.refreshComments(comments);
    }

    @Override
    public void showNoComments() {
        AppUtil.showToast("没有更多评论啦~~~");
        mHasMoreComments = false;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

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

    static class ViewHolder {
        TextView tv;
    }

    class TestAdapter extends BaseAdapter {
        TestAdapter() {

        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Integer getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder h = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(EssayActivity.this).inflate(R.layout.layout_title, null);
                h = new ViewHolder();
                h.tv = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            h.tv.setText(position + "");
            return convertView;
        }
    }
}