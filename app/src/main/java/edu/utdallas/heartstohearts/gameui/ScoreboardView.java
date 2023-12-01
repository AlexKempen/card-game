
package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import edu.utdallas.heartstohearts.R;

public class ScoreboardView extends TableLayout {


    private TextView playerScoreTextViews[];
    private TextView playerNameTextViews[];

    public ScoreboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.scoreboard_view, this);

        playerNameTextViews = new TextView[4];
        playerNameTextViews[0] = (TextView) findViewById(R.id.scoreboard_player1_name);
        playerNameTextViews[1] = (TextView) findViewById(R.id.scoreboard_player2_name);
        playerNameTextViews[2] = (TextView) findViewById(R.id.scoreboard_player3_name);
        playerNameTextViews[3] = (TextView) findViewById(R.id.scoreboard_player4_name);

        playerScoreTextViews = new TextView[4];
        playerScoreTextViews[0] = (TextView) findViewById(R.id.scoreboard_player1_score);
        playerScoreTextViews[1] = (TextView) findViewById(R.id.scoreboard_player2_score);
        playerScoreTextViews[2] = (TextView) findViewById(R.id.scoreboard_player3_score);
        playerScoreTextViews[3] = (TextView) findViewById(R.id.scoreboard_player4_score);

    }


    //TO:DO
    void updateScoreboardNames(){

    }

    //TO:DO
    void updateScoreboardScores(){

    }


}