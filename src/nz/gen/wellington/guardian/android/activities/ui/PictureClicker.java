/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.picture;
import nz.gen.wellington.guardian.android.model.Picture;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class PictureClicker implements OnClickListener {

	private Picture picture;

	public PictureClicker(Picture picture) {
		this.picture = picture;
	}
	
	public void onClick(View view) {
		Intent intent = new Intent(view.getContext(), picture.class);
		intent.putExtra("picture", picture);
		view.getContext().startActivity(intent);	
	}

}
