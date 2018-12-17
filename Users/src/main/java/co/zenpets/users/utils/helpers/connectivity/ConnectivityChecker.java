package co.zenpets.users.utils.helpers.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectivityChecker extends AsyncTask<Void, Void, Boolean> {

    /** THE INTERFACE INSTANCE **/
    private final ConnectivityInterface delegate;

    public ConnectivityChecker(ConnectivityInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("EXCEPTION", e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        delegate.checkConnectivity(aBoolean);
    }
}