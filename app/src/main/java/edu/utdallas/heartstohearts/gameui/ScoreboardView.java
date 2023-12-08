/**
 * Hearts to Hearts project
 * Senior design project, University of Texas at Dallas CS 4485.0W1
 * Fall 2023
 * <p>
 * File authors:
 * - Ragib Arnab
 */
package edu.utdallas.heartstohearts.gameui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import edu.utdallas.heartstohearts.R;
import edu.utdallas.heartstohearts.game.Card;
import edu.utdallas.heartstohearts.game.PlayerState;

public class ScoreboardView extends TableLayout {
    private TextView playerScoreTextViews[];
    private TextView playerNameTextViews[];


    public ScoreboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.scoreboard_view, this);

        playerNameTextViews = new TextView[4];
        playerNameTextViews[0] = findViewById(R.id.scoreboard_player1_name);
        playerNameTextViews[1] = findViewById(R.id.scoreboard_player2_name);
        playerNameTextViews[2] = findViewById(R.id.scoreboard_player3_name);
        playerNameTextViews[3] = findViewById(R.id.scoreboard_player4_name);

        playerScoreTextViews = new TextView[4];
        playerScoreTextViews[0] = findViewById(R.id.scoreboard_player1_score);
        playerScoreTextViews[1] = findViewById(R.id.scoreboard_player2_score);
        playerScoreTextViews[2] = findViewById(R.id.scoreboard_player3_score);
        playerScoreTextViews[3] = findViewById(R.id.scoreboard_player4_score);

    }

    void updateNames(List<String> nicknames, int thisPlayerId) {
        for (int playerId = 0; playerId < nicknames.size(); playerId++) {
            TextView nameTextView = playerNameTextViews[playerId];
            if (playerId == thisPlayerId) {
                nameTextView.setText("You");
                nameTextView.setTypeface(null, Typeface.BOLD);
            } else {
                nameTextView.setText(nicknames.get(playerId));
                nameTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    void updateScores(List<Integer> scores, int thisPlayerId) {
        for (int playerId = 0; playerId < scores.size(); playerId++) {
            TextView scoreTextView = playerScoreTextViews[playerId];
            scoreTextView.setText(scores.get(playerId).toString());
            if (thisPlayerId == playerId) {
                scoreTextView.setTypeface(null, Typeface.BOLD);
            } else {
                scoreTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}