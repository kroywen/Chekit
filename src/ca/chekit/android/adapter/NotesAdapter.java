package ca.chekit.android.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ca.chekit.android.R;
import ca.chekit.android.api.ApiData;
import ca.chekit.android.fragment.TaskNotesFragment;
import ca.chekit.android.model.Note;
import ca.chekit.android.screen.EditNoteScreen;
import ca.chekit.android.util.Utilities;

public class NotesAdapter extends BaseAdapter {
	
	private TaskNotesFragment fragment;
	private List<Note> notes;
	
	public NotesAdapter(TaskNotesFragment fragment, List<Note> notes) {
		this.fragment = fragment;
		this.notes = notes;
	}

	@Override
	public int getCount() {
		return notes.size();
	}

	@Override
	public Note getItem(int position) {
		return notes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return notes.get(position).getId();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.note_list_item, null);
		}
		
		final Note note = getItem(position);
		
		TextView noteView = (TextView) convertView.findViewById(R.id.noteView);
		noteView.setText(note.getNote());
		
		TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
		dateView.setText(Utilities.convertDate(note.getDateChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.yyyy_MM_dd));
		
		TextView timeView = (TextView) convertView.findViewById(R.id.timeView);
		timeView.setText(Utilities.convertDate(note.getDateChanged(), Utilities.yyyy_MM_ddTHH_mm_ss, Utilities.hh_mm_a));
		
		View editNoteBtn = convertView.findViewById(R.id.editNoteBtn);
		editNoteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(fragment.getActivity(), EditNoteScreen.class);
				intent.putExtra(ApiData.PARAM_ID, note.getWorkTaskId());
				intent.putExtra(ApiData.PARAM_ID1, note.getId());				
				intent.putExtra("mode", EditNoteScreen.MODE_EDIT);
				fragment.startActivityForResult(intent, TaskNotesFragment.EDIT_NOTE_REQUEST_CODE);
			}
		});
		
		View deleteNoteBtn = convertView.findViewById(R.id.deleteNoteBtn);
		deleteNoteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.deleteTaskNote(note);
			}
		});
		
		return convertView;
	}

}
