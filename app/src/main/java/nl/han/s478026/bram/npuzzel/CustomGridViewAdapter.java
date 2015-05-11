package nl.han.s478026.bram.npuzzel;

/**
 * Created by Tim on 30-3-2015.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomGridViewAdapter extends ArrayAdapter<CroppedImage> {
    Context context;
    int layoutResourceId;
    ArrayList<CroppedImage> data = new ArrayList<>();
    int width = 0;
    int height = 0;

    public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<CroppedImage> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        setWidthAndHeight(data);
    }

    private void setWidthAndHeight(ArrayList<CroppedImage> data) {
        for(CroppedImage item: data) {
            if(item.getCroppedImage() != null) {
                if (item.getCroppedImage().getWidth() > this.width) {
                    this.width = item.getCroppedImage().getWidth();
                }
                if (item.getCroppedImage().getHeight() > this.height) {
                    this.height = item.getCroppedImage().getHeight();
                }
            }
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        CroppedImage item = data.get(position);
        holder.imageItem.setImageBitmap(item.getCroppedImage());
        holder.imageItem.setMinimumHeight(height);
        holder.imageItem.setMinimumWidth(width);
        return row;
    }
    static class RecordHolder {
        ImageView imageItem;
    }
}