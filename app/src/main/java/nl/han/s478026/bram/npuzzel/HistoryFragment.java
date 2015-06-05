package nl.han.s478026.bram.npuzzel;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bram on 4-6-2015.
 */
public class HistoryFragment extends Fragment {


    private ArrayList<HistoryFragmentRowItem> data;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        String url;
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            url = bundle.getString("url");

            //        String key = getArguments().getString("key");
            Firebase.setAndroidContext(container.getContext());
            Firebase myFirebaseRef = new Firebase(url);
            final ArrayList<HistoryFragmentRowItem> data = new ArrayList<>();
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Map<String, Object> details = (Map<String, Object>) child.getValue();
                            HistoryFragmentRowItem detailItem = new HistoryFragmentRowItem(
                                    child.getKey(),
                                    (boolean) details.get("didWon"),
                                    (long) details.get("yourScore"),
                                    (long) details.get("opponentScore"),
                                    (long) details.get("resourceID"));
                            data.add(detailItem);

                    }
                    ListView detail = (ListView) getView().findViewById(R.id.history_detail);
                    detail.setAdapter(new HistoryFragmentListAdapter(
                            getView().getContext(),
                            R.layout.fragment_row_list,
                            data));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setData(ArrayList<HistoryFragmentRowItem> data) {
        this.data = data;
    }
}
