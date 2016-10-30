package app.popularmovies.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.popularmovies.MovieDetailsFragment.OnMovieDetailsInteractionListener;
import app.popularmovies.R;
import app.popularmovies.data.MoviesDbHelper;
import app.popularmovies.model.Review;

/**

 */
public class ReviewListCursorAdapter extends CursorRecyclerViewAdapter<ReviewListCursorAdapter.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(ReviewListCursorAdapter.class);

    private final OnMovieDetailsInteractionListener mListener;


	public ReviewListCursorAdapter(Context context, Cursor cursor, OnMovieDetailsInteractionListener listener){
		super(context,cursor);
		this.mListener = listener;

	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
	public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {

        Review review   = MoviesDbHelper.cursorToReview(cursor);


        holder.mItem = review;
		holder.mAuthorText.setText(review.getAuthor());
		holder.mcontentText.setText(review.getContent());

		holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onReviewInteraction(holder.mItem);
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorText;
        public final TextView mcontentText;
        public Review mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAuthorText = (TextView) view.findViewById(R.id.reviewAuthor);
            mcontentText = (TextView) view.findViewById(R.id.reviewContent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mcontentText.getText() + "'";
        }
    }
}
