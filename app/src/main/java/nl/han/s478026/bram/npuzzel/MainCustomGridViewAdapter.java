package nl.han.s478026.bram.npuzzel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by bram on 6-4-2015.
 */
public class MainCustomGridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<>();
    private int rows;
    private int screenWidth;
    private ArrayList<ImageItem> rowHeight = new ArrayList<>();

    public MainCustomGridViewAdapter(Context context, int resource, ArrayList<ImageItem> data, int rows, int screenWidth) {
        super(context, resource, data);

        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
        this.rows = rows;
        this.screenWidth = screenWidth;
        //setMaxImageHeight();
    }

    private void setMaxImageHeight() {
        int i = 0;
        ImageItem rowItem1 = null;
        ImageItem rowItem2;
        for(ImageItem item: data) {
            if(i % rows == 0) {
                rowItem1 = item;
                if(data.size() == i) {
                    rowHeight.add(rowItem1);
                }
            } else {
                rowItem2 = item;
                if(rowItem1.getBitmap().getHeight() > rowItem2.getBitmap().getHeight()) {
                    rowHeight.add(rowItem1);
                } else {
                    rowHeight.add(rowItem2);
                }
            }
            i++;
        }
    }

    public int getRowHeight(int position) {
        int row = Math.round(position / rows);
//        return 10;
        return rowHeight.get(row).getBitmap().getHeight();
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
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image_main);

            row.setTag(holder);
        }

        ImageItem item = data.get(position);
        if(item != null) {
            holder = (RecordHolder) row.getTag();
            holder.imageItem.setImageBitmap(item.getBitmap());
            holder.imageItem.setMinimumWidth(screenWidth);
        }
        return row;
    }
    static class RecordHolder {
        ImageView imageItem;
    }


    public void setData(ArrayList<ImageItem> data) {
        this.data = data;
    }

    public ArrayList<ImageItem> getData() {
        return data;
    }
}
