package com.example.mac.pollcial;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class PollsAdapter extends ArrayAdapter<SinglePoll> {

    // constructor of PollAdapter, single_poll_preview is the format to display a poll's preview on discover page
    public PollsAdapter(Context currContext, ArrayList<SinglePoll> allPolls) {
        super(currContext, R.layout.single_poll_preview, allPolls);
    }

    @NonNull
    @Override
    /**
     * This method will be called automatically to populate a SinglePoll preview.
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SinglePoll currPoll = getItem(position); // get the next SinglePoll object in the ArrayList to display

        // set the display (preview) format
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.single_poll_preview, parent, false);
        }

        // find the location to display info about a SinglePoll object
        TextView currTitle = (TextView) convertView.findViewById(R.id.preview_title);
        TextView currDescription = (TextView) convertView.findViewById(R.id.preview_description);
        TextView currPostTime = (TextView) convertView.findViewById(R.id.preview_time);

        // set the content to be displayed

        // trim poll title a littl, to make sure it fit into one line
        String pollTitle = currPoll.getPollTitle();
        String previewPollTitle = "";

        if (pollTitle.length() > 23) {
            String[] splitedTitle = pollTitle.split(" ");
            for (int i = 0; i < splitedTitle.length; i++) {
                previewPollTitle += splitedTitle[i];
                previewPollTitle += " ";
                if (previewPollTitle.length() > 23) {
                    break;
                }
            }

            previewPollTitle += "...";
        }
        else {
            previewPollTitle = pollTitle;
        }

        // trim poll description a little, to make sure it fit into one line
        String pollDescription = currPoll.getPollDecription();
        String previewPollDescription = "";

        if(pollDescription.length() > 35) {
            String[] splitedDescription = pollDescription.split(" ");
            for (int i = 0; i < splitedDescription.length; i++) {
                previewPollDescription += splitedDescription[i];
                previewPollDescription += " ";
                if (previewPollDescription.length() > 35) {
                    break;
                }
            }

            previewPollDescription += "...";
        }
        else {
            previewPollDescription = pollDescription;
        }
        currTitle.setText(previewPollTitle);
        currDescription.setText(previewPollDescription);
        currPostTime.setText(currPoll.getPollPostTime());

        return convertView;
    }
}
