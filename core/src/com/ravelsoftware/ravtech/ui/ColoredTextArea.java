
package com.ravelsoftware.ravtech.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.kotcrab.vis.ui.widget.VisTextArea;

public class ColoredTextArea extends VisTextArea {

	Array<Color> lineColors = new Array<Color>();

	public void log (Color color, String line) {
		lineColors.add(color);
		setText(getText() + line);
	}

	@Override
	protected void drawText (Batch batch, BitmapFont font, float x,
		float y) {
		IntArray linesBreak = null;
		float alpha = font.getColor().a;
		try {
			Field linesBreakField = ClassReflection
				.getDeclaredField(VisTextArea.class, "linesBreak");
			linesBreakField.setAccessible(true);
			linesBreak = (IntArray)linesBreakField.get(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		float offsetY = 0;
		for (int i = getFirstLineShowing()
			* 2; i < (getFirstLineShowing() + getLinesShowing()) * 2
				&& i < linesBreak.size; i += 2) {
			Color lineColor = lineColors.get(i / 2);
			font.setColor(
				new Color(lineColor.r, lineColor.g, lineColor.b, alpha));
			font.draw(batch, displayText, x, y + offsetY,
				linesBreak.items[i], linesBreak.items[i + 1], 0,
				Align.left, false);
			offsetY -= font.getLineHeight();
		}
		font.setColor(Color.BLACK);
	}

}
