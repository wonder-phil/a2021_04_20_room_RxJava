package me.pgb.a2021_04_20_room;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.pgb.a2021_04_20_room.db.PortfolioDao;
import me.pgb.a2021_04_20_room.db.PortfolioDatabase;
import me.pgb.a2021_04_20_room.db.Stock;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "_MAINACT_";
    private PortfolioDao portfolioDao;
    private LiveData<List<Stock>> allStocks;
    private PortfolioDatabase portfolioDatabase;
    private Observable<Stock> observable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        portfolioDatabase = PortfolioDatabase.getInstance(getApplicationContext());
        portfolioDao = portfolioDatabase.portfolioDao();
        allStocks = portfolioDao.getAll();
        Stock stock = new Stock("CSE 3200", 99.9);
        observable = io.reactivex.Observable.just(stock);
        Observer<Stock> observer = getStockObserver(stock);

        observable
                .observeOn(Schedulers.io())
                .subscribe(observer);


    }

    private Observer<Stock> getStockObserver(Stock stock) { // OBSERVER
        return new Observer<Stock>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull Stock stock) {
                portfolioDatabase.portfolioDao().insert(stock);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}