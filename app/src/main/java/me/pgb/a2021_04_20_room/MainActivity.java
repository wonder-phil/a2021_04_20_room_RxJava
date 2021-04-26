package me.pgb.a2021_04_20_room;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "_MAINACT_";

    private int stock_ID;
    private Observable<Stock> observable;

    private PortfolioViewModel portfolioViewModel;

    private Button insertStock;
    private Button deleteButton;
    private Button findButton;
    private EditText stockName;
    private EditText stockPrice;

    private DataOperation dataOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        portfolioViewModel = new ViewModelProvider(this).get(PortfolioViewModel.class);

        insertStock = findViewById(R.id.insert_button);
        deleteButton = findViewById(R.id.delete_button);
        findButton = findViewById(R.id.find_button);

        stockName = findViewById(R.id.name_textView);
        stockPrice = findViewById(R.id.price_textView);

        insertStock.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               dataOperation = DataOperation.INSERT;
               Stock stock;
               if (0 == stockName.getText().toString().length()) {
                   stock = new Stock("CSE 3200", 99.9);
               } else {
                   double price = Double.valueOf(stockPrice.getText().toString()).doubleValue();
                   stock = new Stock(stockName.getText().toString(), price);
               }
               observable = io.reactivex.Observable.just(stock);
               Observer<Stock> observer = getStockObserver(stock);

               observable
                       .observeOn(Schedulers.io())
                       .subscribe(observer);
           }
       });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOperation = DataOperation.DELETE;

                Stock stock;

                String name = stockName.getText().toString();
                stock = new Stock(name, 0.0);
                stock.setId(stock_ID);

                observable = io.reactivex.Observable.just(stock);
                Observer<Stock> observer = getStockObserver(stock);

                observable
                        .observeOn(Schedulers.io())
                        .subscribe(observer);
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataOperation = DataOperation.GET_STOCK;

                findStock(stockName.getText().toString());
                Log.i(TAG, "Stock " + stock_ID);
            }
        });

    }

    // sets stock_ID
    public void findStock(String name) {
        dataOperation = DataOperation.GET_STOCK;
        Stock stock = new Stock(name, 0.0);
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
                switch(dataOperation) {
                    case INSERT:
                        portfolioViewModel.getPortfolioDatabase().portfolioDao().insert(stock);
                        break;
                    case DELETE:
                        if (-1 == stock_ID) {
                            Log.e(TAG, "Dont' delete a non-existant stock");
                        } else {
                            portfolioViewModel.getPortfolioDatabase().portfolioDao().delete(stock);
                        }
                        break;
                    case GET_STOCK:
                        Stock actualStock = portfolioViewModel.getPortfolioDatabase().portfolioDao().getStock(stock.name);
                        if (null == actualStock){
                            Log.e(TAG, "No stock found! " );
                            // handle error!
                            stock_ID = -1;
                        } else {
                            Log.i(TAG, "Get stock ID: " + String.valueOf(actualStock.id).toString());
                            stock_ID = actualStock.id;
                        }
                        break;
                    case UPDATE:
                        Log.i(TAG, "Delete");
                        break;
                    default:
                        Log.i(TAG, "Default");
                }
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