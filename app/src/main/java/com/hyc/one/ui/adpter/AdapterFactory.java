package com.hyc.one.ui.adpter;

import android.content.Context;

import com.hyc.one.base.BaseRecyclerAdapter;
import com.hyc.one.utils.S;

import java.util.List;

/**
 * Created by hyc on 2016/7/12.
 */
public class AdapterFactory {
    public static BaseRecyclerAdapter getAdapter(Context ctx, List list, int tag) {
        switch (tag) {
            case S.DAIRY:
                return new OtherDairyAdapter(ctx, list);
            case S.MOVIE:
                return new OtherMovieAdapter(ctx, list);
            case S.WORK:
            case S.ESSAY:
                return new OtherEssayAdapter(ctx, list);
            case S.SERIAL:
                return new OtherSerialAdapter(ctx, list);
            case S.QUESTION:
                return new OtherQuestionAdapter(ctx, list);
            case S.MUSIC:
                return new MonthMusicAdapter2(ctx, list);
            case S.MOVIE_LIST:
                return new MovieListAdapter2(ctx, list);

        }
        return null;
    }
}
