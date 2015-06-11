package nl.han.s478026.bram.npuzzel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bram on 4-6-2015.
 */
public class HistoryFragmentListAdapter extends ArrayAdapter<HistoryFragmentRowItem> {

    private final int layoutResourceId;
    private final Context context;
    private final ArrayList<HistoryFragmentRowItem> data;

    public HistoryFragmentListAdapter(Context context, int layoutResourceId, ArrayList<HistoryFragmentRowItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        RecordHolder holder;
        if(convertView != null){
            return convertView;
        }else {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.history_used_image);
            holder.historyUserScore = (TextView) row.findViewById(R.id.history_user_score);
            holder.historyOpponentScore = (TextView) row.findViewById(R.id.history_opponent_score);
            holder.didYouWon = (TextView) row.findViewById(R.id.did_you_won);

            row.setTag(holder);
        }
        HistoryFragmentRowItem item = data.get(position);
        holder.imageItem.setImageResource((int) item.getResourceId());
        holder.historyUserScore.setText((int) item.getYourScore()+ "");
        holder.historyOpponentScore.setText((int) item.getOpponentScore()+ "");
        holder.didYouWon.setText(item.isDidWon() ? context.getString(R.string.fragment_won) :
                getContext().getString(R.string.fragment_lost) + "");
        return row;
    }
    static class RecordHolder {
        ImageView imageItem;
        TextView historyUserScore;
        TextView historyOpponentScore;
        TextView didYouWon;
    }
}
