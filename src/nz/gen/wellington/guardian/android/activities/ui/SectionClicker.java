package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.section;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SectionClicker implements OnClickListener {

	private Section section;

	public SectionClicker(Section section) {
		this.section = section;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), section.class);
		intent.putExtra("section", section);
		v.getContext().startActivity(intent);
	}

}
