package jp.mediba.seeksemicolon;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class MyActivity extends AppCompatActivity {

    private int score = 0;
    private int maxPatternNum;
    private Bitmap buttonImage1 , buttonImage2;

    private TableLayout table;
    private int colon_x , colon_y;
    private int tableSize;
    private int ChoiceCharTarget;

    private String[][] ChoiceCharList;

    private Handler TimeRefresh = new Handler();
    private Handler TimeTimeOver = new Handler();

    private long TimeupCount;
    private double TimeRest , TimeStart;
    private TextView TimeView;

    private Boolean isTimeOver , isFailedTouch;
    private int failed_x , failed_y;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());        Fabric.with(this, new Crashlytics());

        maxPatternNum = getPatternCSV( "patterns.csv" );
        titleScene();

        checkForUpdates();
    }


    public void titleScene() {
        setContentView(R.layout.activity_my);

        LinearLayout parentLayout;
        parentLayout = (LinearLayout)(findViewById(R.id.parentLinearLayout));

        TextView titleView = new TextView(this);
        titleView.setText(getString(R.string.game_title));
        parentLayout.addView(titleView);

        Button startButton = new Button(this);
        startButton.setId(R.id.startButton);
        startButton.setText(getString(R.string.title_start));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeupCount = 10 * 1000;
                TimeStart = System.currentTimeMillis();
                TimeRest = TimeupCount - (System.currentTimeMillis() - TimeStart);
                isTimeOver = false;
                isFailedTouch = false;
                score = 0;
                gameScene(2);
            }
        });
        parentLayout.addView(startButton);

    }


    public void gameScene(int tableSizeRef){
        setContentView(R.layout.activity_my);

        tableSize = tableSizeRef;

        LinearLayout parentLayout;
        parentLayout = (LinearLayout)(findViewById(R.id.parentLinearLayout));

        ChoiceCharTarget = (int) ( Math.random() * maxPatternNum );

        table = makeColonTable( tableSize , ChoiceCharList[ChoiceCharTarget][0] , ChoiceCharList[ChoiceCharTarget][1] );

        TextView scoreView = new TextView(this);
        scoreView.setText(getString(R.string.gamescore) + score);

        TextView titleView = new TextView(this);
        titleView.setText( String.format( getString(R.string.gameseekcolon) , ChoiceCharList[ChoiceCharTarget][1] , ChoiceCharList[ChoiceCharTarget][0] ) );
        titleView.setTextSize(18f);


        TimeView = new TextView(this);

        parentLayout.addView(table);
        parentLayout.addView(scoreView);
        parentLayout.addView(titleView);
        parentLayout.addView(TimeView);

        StartCount();

    }


    //    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public TableLayout makeColonTable(final int columns, final String ChoiceCharCorrect, final String ChoiceCharIncorrect) {

        TableLayout table = new TableLayout(this);

        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        TableRow tableRowArray[];
        tableRowArray = new TableRow[columns];

        if ( ! isFailedTouch && ! isTimeOver ) {
            Random rnd = new Random();
            colon_x = rnd.nextInt(columns);
            colon_y = rnd.nextInt(columns);
        }

        int ButtonSize = (int)(size.x / columns * 0.9);
        boolean setMissedButtonIdFlug = false;

        for (int y = 0; y < columns; y++) {
            tableRowArray[y] = new TableRow(this);
            for(int x = 0 ; x < columns ; x++) {

                Button button = new Button(this);

                button.setPadding(0, 0, 0, 0);
                button.setTextSize(ButtonSize / 4);

                if ( x == colon_x && y == colon_y ) {
                    button.setText(ChoiceCharCorrect);
                    if ( ! isFailedTouch && ! isTimeOver ) {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StopCount();
                                TimeupCount += 10 * 1000;
                                score += columns * (int) (TimeRest / 100);
                                if (columns < 10) {
                                    gameScene(columns + 1);
                                } else {
                                    gameScene(columns);
                                }
                            }
                        });
                    }else {
                        button.setBackgroundColor(Color.CYAN);
                    }
                    button.setId(R.id.correctButton);
                }else{
                    button.setText(ChoiceCharIncorrect);
                    if ( ! isFailedTouch && ! isTimeOver ) {
                        final int finalX = x;
                        final int finalY = y;
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StopCount();
                                failed_x = finalX;
                                failed_y = finalY;
                                gameOverScene();
                            }
                        });
                    }else{
                        if ( x == failed_x && y == failed_y && !isTimeOver ){
                            button.setBackgroundColor(Color.RED);
                        }
                    }
                    if ( ! setMissedButtonIdFlug ){
                        button.setId(R.id.missedButton);
                        setMissedButtonIdFlug = true;
                    }

                }

                tableRowArray[y].addView(button, new TableRow.LayoutParams(ButtonSize, ButtonSize));

            }
            table.addView(tableRowArray[y], new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        return table;
    }


    public void gameOverScene() {
        setContentView(R.layout.activity_my);

        StopCount();

        LinearLayout parentLayout;
        parentLayout = (LinearLayout)(findViewById(R.id.parentLinearLayout));

        TextView titleView = new TextView(this);
        if ( isTimeOver ) {
            titleView.setText(getString(R.string.resulttimeover));
        }else{
            isFailedTouch = true;
            titleView.setText(getString(R.string.resultgameover));
        }

        table = makeColonTable( tableSize , ChoiceCharList[ChoiceCharTarget][0] , ChoiceCharList[ChoiceCharTarget][1] );

        TextView scoreView = new TextView(this);
        scoreView.setText(getString(R.string.resultscore) + score);

        Button startButton = new Button(this);
        startButton.setId(R.id.backTitleButton);
        startButton.setText(getString(R.string.resultback2title));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleScene();
            }
        });

        parentLayout.addView(table);
        parentLayout.addView(titleView);
        parentLayout.addView(scoreView);
        parentLayout.addView(startButton);

    }


    public int getPatternCSV(String CSVFilename) {

        int i = 0;
        InputStreamReader isr;

        try {

            Log.d("INFO","CSV Read : " + CSVFilename);
            isr = new InputStreamReader( getAssets().open(CSVFilename) );

            // 行数数えて配列確保。
            LineNumberReader lr = new LineNumberReader( isr );
            while ( lr.readLine() != null ) ;
            ChoiceCharList = new String[lr.getLineNumber()][2];
            Log.d("INFO","CSV Line : " + lr.getLineNumber());

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

        try {
            isr = new InputStreamReader( getAssets().open(CSVFilename) );

            // CSVとして読んで配列に投入。
            BufferedReader br = new BufferedReader( isr );

            String line;
            while ( (line = br.readLine()) != null ) {
                String[] result = line.split(",");
                ChoiceCharList[i] = new String[]{result[0], result[1]};

                Log.d( "INFO" , "add choice no " + i + " : " + ChoiceCharList[i][0] + " / " + ChoiceCharList[i][1] );

                i++;
            }

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            e.printStackTrace();
        }

        Log.d("INFO","return : " + i );
        return i;

    }

    public void StartCount() {
        TimeRefresh.postDelayed( RefreshTimeViewRunnable , 1 );
        TimeRest = TimeupCount - (System.currentTimeMillis() - TimeStart);
        TimeTimeOver.postDelayed( TimeOverRunnable , (long)(TimeRest)  );
        Log.d("INFO","Count Start : " + (long)(TimeRest) );
    }

    public void StopCount(){
        TimeRefresh.removeCallbacks( RefreshTimeViewRunnable );
        TimeTimeOver.removeCallbacks( TimeOverRunnable );
    }


    private final Runnable TimeOverRunnable = new Runnable(){
        @Override
        public void run(){
            StopCount();
            isTimeOver = true;
            gameOverScene();
        }
    };

    private final Runnable RefreshTimeViewRunnable = new Runnable(){
        @Override
        public void run() {
            TimeRest = TimeupCount - (System.currentTimeMillis() - TimeStart);
            TimeView.setText(String.format(getString(R.string.gameTimeString), (TimeRest / 1000)));
            TimeRefresh.postDelayed( RefreshTimeViewRunnable, 1 );
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

}

