/**
 * taskwarrior for android – a task list manager
 *
 * Copyright (c) 2012 Sujeevan Vijayakumaran
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * allcopies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * http://www.opensource.org/licenses/mit-license.php
 *
 */

package org.svij.taskwarriorapp.db;

import java.text.DateFormat;
import java.util.ArrayList;

import org.svij.taskwarriorapp.R;
import org.svij.taskwarriorapp.data.Task;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaskBaseAdapter extends BaseAdapter {
	private ArrayList<Task> entries;
	private Activity activity;
	private static final int TYPE_ROW = 0;
	private static final int TYPE_ROW_CLICKED = 1;
	private static final int TYPE_MAX_COUNT = TYPE_ROW_CLICKED + 1;
	private ArrayList<Integer> RowClickedList = new ArrayList<Integer>();
	private Context context;

	public TaskBaseAdapter(Activity a, int layoutID, ArrayList<Task> entries,
			Context context) {
		super();
		this.entries = entries;
		this.activity = a;
		this.context = context;
	}

	public static class ViewHolder {
		public TextView taskDescription;
		public TextView taskProject;
		public TextView taskDueDate;
		public TextView taskPriority;
		public TextView taskStatus;
		public TextView taskUrgency;
		public RelativeLayout taskRelLayout;
		public FrameLayout taskFramelayout;
		public View taskPriorityView;
	}

	public void changeTaskRow(final int position) {
		if (RowClickedList.contains(position)) {
			RowClickedList.remove(Integer.valueOf(position));
		} else {
			RowClickedList.add(position);
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			int type = getItemViewType(position);

			switch (type) {
			case TYPE_ROW:
				v = vi.inflate(R.layout.task_row, null);
				break;
			case TYPE_ROW_CLICKED:
				v = vi.inflate(R.layout.task_row_clicked, null);
			}
			holder = new ViewHolder();
			holder.taskDescription = (TextView) v
					.findViewById(R.id.tvRowTaskDescription);
			holder.taskProject = (TextView) v
					.findViewById(R.id.tvRowTaskProject);
			holder.taskDueDate = (TextView) v
					.findViewById(R.id.tvRowTaskDueDate);
			holder.taskRelLayout = (RelativeLayout) v
					.findViewById(R.id.taskRelLayout);
			holder.taskPriorityView = (View) v
					.findViewById(R.id.horizontal_line_priority);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		final Task task = entries.get(position);
		if (task != null) {
			holder.taskDescription.setText(task.getDescription());
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			String textAppearance = prefs
					.getString(
							"pref_appearance_descriptionTextSize",
							context.getResources()
									.getString(
											R.string.pref_appearance_descriptionTextSize_small));

			if (textAppearance.equals(context.getResources().getString(
					R.string.pref_appearance_descriptionTextSize_small))) {
				holder.taskDescription.setTextSize(context.getResources()
						.getDimension(R.dimen.taskDescription_small));
			} else if (textAppearance.equals(context.getResources().getString(
					R.string.pref_appearance_descriptionTextSize_medium))) {
				holder.taskDescription.setTextSize(context.getResources()
						.getDimension(R.dimen.taskDescription_medium));
			} else {
				holder.taskDescription.setTextSize(context.getResources()
						.getDimension(R.dimen.taskDescription_large));
			}

			if (task.getProject() != null) {
				holder.taskProject.setVisibility(View.VISIBLE);
				holder.taskProject.setText(task.getProject());
			} else {
				holder.taskProject.setVisibility(View.GONE);
			}

			if (task.getDue() != null && !(task.getDue().getTime() == 0)) {
				holder.taskDueDate.setVisibility(View.VISIBLE);
				if (!DateFormat.getTimeInstance().format(task.getDue())
						.equals("00:00:00")) {
					holder.taskDueDate.setText(DateFormat.getDateTimeInstance(
							DateFormat.MEDIUM, DateFormat.SHORT).format(
							task.getDue()));
				} else {
					holder.taskDueDate.setText(DateFormat.getDateInstance()
							.format(task.getDue()));
				}
			} else {
				holder.taskDueDate.setVisibility(View.GONE);
			}

			if (!TextUtils.isEmpty(task.getPriority())) {
				if (task.getPriority().equals("H")) {
					holder.taskPriorityView.setBackgroundColor(context
							.getResources().getColor(R.color.task_red));
				} else if (task.getPriority().equals("M")) {
					holder.taskPriorityView.setBackgroundColor(context
							.getResources().getColor(R.color.task_yellow));
				} else if (task.getPriority().equals("L")) {
					holder.taskPriorityView.setBackgroundColor(context
							.getResources().getColor(R.color.task_green));
				}
			} else {
				holder.taskPriorityView.setBackgroundColor(context
						.getResources().getColor(android.R.color.transparent));
			}

			if (task.getStatus().equals("completed")
					|| task.getStatus().equals("deleted")) {
				if (getItemViewType(position) == TYPE_ROW_CLICKED) {
					LinearLayout llButtonLayout = (LinearLayout) v
							.findViewById(R.id.taskLinLayout);
					llButtonLayout.setVisibility(View.GONE);

					View horizBar = v.findViewById(R.id.horizontal_line);
					horizBar.setVisibility(View.GONE);
				}
			}
		}

		return v;
	}

	@Override
	public int getItemViewType(int position) {
		return RowClickedList.contains(position) ? TYPE_ROW_CLICKED : TYPE_ROW;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return entries.size();
	}

	@Override
	public Object getItem(int position) {
		return entries.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
