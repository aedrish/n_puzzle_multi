package nl.han.s478026.bram.npuzzel;

/**
 * Created by Tim on 30-3-2015.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Bram Arts
 * email: bramiejo@hotmail.com
 * Student nummer: 478026
 */
public class CustomPlayerGridViewAdapter extends ArrayAdapter<CroppedImage> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<CroppedImage> data = new ArrayList<>();
    private int width = 0;
    private int height = 0;

    public CustomPlayerGridViewAdapter(Context context, int layoutResourceId, ArrayList<CroppedImage> data) {
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
        if(data.get(position).getCroppedImage() == null) {
            holder.imageItem.setBackgroundColor(Color.rgb(0,0,0));
        }
        return row;
    }
    static class RecordHolder {
        ImageView imageItem;
    }

    public void setData(ArrayList<CroppedImage> data) {
        this.data = data;
    }

    public ArrayList<CroppedImage> getData() {
        return data;
    }
}