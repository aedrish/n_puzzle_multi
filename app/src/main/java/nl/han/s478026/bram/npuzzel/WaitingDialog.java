package nl.han.s478026.bram.npuzzel;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Daniel on 3-6-2015.
 */
public class WaitingDialog extends ProgressDialog {
    public WaitingDialog(Context context, String title, String message) {
        super(context);
        setTitle(title);
        setMessage(message);
        show();
    }
}
