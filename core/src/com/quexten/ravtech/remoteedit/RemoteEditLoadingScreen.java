
package com.quexten.ravtech.remoteedit;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RemoteEditLoadingScreen implements Screen {

	PolygonSpriteBatch polygonBatch;
	Texture emptyTexture;
	OrthographicCamera camera;
	BitmapFont font;
	BitmapFontCache cache;

	float percentage = 0;

	public RemoteEditLoadingScreen () {
	}

	@Override
	public void show () {
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
		cache = font.getCache();

		if (Gdx.app.getType() == ApplicationType.Android)
			font.getData().setScale(2);
		polygonBatch = new PolygonSpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Pixmap pixMap = new Pixmap(1, 1, Format.RGB565);
		pixMap.setColor(Color.WHITE);
		pixMap.fill();
		emptyTexture = new Texture(pixMap);
		pixMap.dispose();
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		PolygonRegion outerRegion = createCircleRegion(percentage % 2 < 1 ? (percentage % 1) * 360 : 1 * 360);
		PolygonRegion innerRegion = createCircleRegion(percentage % 2 > 1 ? (percentage % 1) * 360 : 0);

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.update();

		final int ringRadius = 200;
		final int innerRingRadius = 180;
		final int innerRingWidth = 20;
		final Color outerColor = new Color(0.97f, 0.44f, 0f, 1f);
		final Color innerColor = Color.WHITE;

		String percentageString = (Math.round((((float)percentage % 1)) * 10)) * 10 + "%";

		polygonBatch.setProjectionMatrix(camera.combined);
		polygonBatch.begin();

		polygonBatch.setColor(outerColor);
		polygonBatch.draw(outerRegion, 0, 0, ringRadius, ringRadius);

		polygonBatch.setColor(innerColor);
		polygonBatch.draw(innerRegion, 0, 0, innerRingRadius, innerRingRadius);

		polygonBatch.setColor(outerColor);
		polygonBatch.draw(innerRegion, 0, 0, innerRingRadius - innerRingWidth, innerRingRadius - innerRingWidth);

		font.setColor(innerColor);
		font.getData().setScale(1f);
		font.setColor(Color.WHITE);
		drawStringCentered(polygonBatch, 0, 0, percentageString);

		font.getData().setScale(1f);
		font.setColor(Color.GRAY);
		drawStringCentered(polygonBatch, 0, -300, percentage % 2 < 1 ? "Fetching Assets..." : "Recieving Game State...");

		polygonBatch.end();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}

	@Override
	public void hide () {

	}

	@Override
	public void dispose () {
	}

	void drawStringCentered (Batch batch, float x, float y, String text) {
		font.draw(polygonBatch, text, x - cache.addText(text, 0, 0).width / 2, y + cache.addText(text, 0, 0).height / 2);
	}

	PolygonRegion createCircleRegion (float degrees) {
		final int steps = Math.abs((int)degrees);
		if (steps < 3) {
			return new PolygonRegion(new TextureRegion(emptyTexture), new float[0], new short[0]);
		}

		float[] vertecies = new float[steps * 2];
		vertecies[0] = 0;
		vertecies[1] = 0;

		for (int i = 2; i < (vertecies.length); i += 2) {
			float subDegrees = degrees * ((float)i - 2) / (vertecies.length - 4);
			vertecies[i] = (float)Math.cos(Math.toRadians(subDegrees));
			vertecies[i + 1] = (float)Math.sin(Math.toRadians(subDegrees));
		}

		short[] triangles = new short[(steps - 2) * 3];

		for (int i = 0; i < (steps - 2) * 3; i += 3) {
			triangles[i] = 0;
			triangles[i + 1] = (short)((i / 3) + 1);
			triangles[i + 2] = (short)((i / 3) + 2);
		}

		return new PolygonRegion(new TextureRegion(emptyTexture), vertecies, triangles);
	}

}
