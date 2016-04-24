
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.kotcrab.vis.ui.widget.VisLabel;

public abstract class LabelActorPair<T> {

	public VisLabel label;
	public Actor pairedComponent;
	T dragValue;
	T oldValue;
	Runnable draggedListener = new Runnable() {
		@Override
		public void run () {
		}
	};
	Runnable releasedListener = new Runnable() {
		@Override
		public void run () {
		}
	};

	public LabelActorPair (String labelText, Actor pairedComponent,
		boolean draggable) {

		label = new VisLabel(labelText);
		if (draggable) {
			label.addListener(new InputListener() {
				public void enter (InputEvent event, float x, float y,
					int pointer, Actor fromActor) {
					Gdx.graphics
						.setSystemCursor(SystemCursor.HorizontalResize);
				}

				public void exit (InputEvent event, float x, float y,
					int pointer, Actor toActor) {
					Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
				}
			});

			label.addListener(new DragListener() {
				public void dragStart (InputEvent event, float x, float y,
					int pointer) {
					Gdx.graphics
						.setSystemCursor(SystemCursor.HorizontalResize);
				}

				public void drag (InputEvent event, float x, float y,
					int pointer) {
					Gdx.graphics
						.setSystemCursor(SystemCursor.HorizontalResize);
				}

				public void dragStop (InputEvent event, float x, float y,
					int pointer) {
					Gdx.graphics.setSystemCursor(SystemCursor.Arrow);
				}
			});
		}

		label.addListener(new DragListener() {

			float startX;
			float startY;

			public void dragStart (InputEvent event, float x, float y,
				int pointer) {
				oldValue = getValue();
				startX = x;
				startY = y;
			}

			public void drag (InputEvent event, float x, float y,
				int pointer) {
				dragValue = getValue();
				dragged(x - startX, y - startY);
				LabelActorPair.this.draggedListener.run();
			}

			public void dragStop (InputEvent event, float x, float y,
				int pointer) {
				LabelActorPair.this.releasedListener.run();
			}
		});

		this.pairedComponent = pairedComponent;
	}

	abstract T getValue ();

	void dragged (float deltaX, float deltaY) {
	}

}
