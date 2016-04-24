
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.ui.utils.UpdateManager;
import com.quexten.ravtech.dk.ui.utils.Updater;

public class UpdaterWidget extends VisWindow {

	public UpdaterWidget () {
		super("Updater");

		align(Align.top);

		VisTable items = new VisTable();

		Entries<String, Updater> entries = UpdateManager.getUpdaters()
			.iterator();
		while (entries.hasNext) {
			Entry<String, Updater> entry = entries.next();
			UpdaterEntry updaterEntry = new UpdaterEntry(entry.key,
				entry.value);
			entry.value.setUpdaterEntry(updaterEntry);
			items.add(updaterEntry).growX();
		}
		this.add(new VisScrollPane(items)).growX().align(Align.top);

		setResizable(true);
		setSize(450, 600);
		setVisible(false);
	}

	public class UpdaterEntry extends VisTable {

		VisLabel versionLabel;
		VisTextButton actionButton;
		Updater updater;

		public UpdaterEntry (String title, final Updater updater) {
			this.updater = updater;
			align(Align.left);
			VisLabel titleLabel = new VisLabel(title,
				new VisLabel.LabelStyle(
					(BitmapFont)RavTechDK.editorAssetManager
						.get("fonts/OpenSansBold.fnt"),
					Color.BLACK));
			add(titleLabel).padLeft(10).align(Align.left);
			row();

			add(new VisLabel(updater.getDescription())).align(Align.left)
				.padLeft(15);
			row();

			VisTable subLineTable = new VisTable();
			versionLabel = new VisLabel(
				"Version: " + updater.currentVersion() + "/" + "");
			versionLabel
				.setStyle(new LabelStyle(versionLabel.getStyle()));
			versionLabel.getStyle().fontColor = Color.GRAY;
			subLineTable.add(versionLabel).align(Align.left).padLeft(15);

			subLineTable
				.add(new LinkLabel(title, updater.getProjectPage()))
				.padLeft(15);
			add(subLineTable).align(Align.left);
			add(new Actor()).growX();
			actionButton = new VisTextButton(
				updater.isNewVersionAvalible() ? "Update"
					: updater.currentVersion().equals("") ? "Install"
						: "Uninstall");
			actionButton.setDisabled(true);

			actionButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (updater.isNewVersionAvalible()) {
						actionButton.setText("Updating...");
						actionButton.setDisabled(true);

						Thread thread = new Thread() {
							@Override
							public void run () {
								updater.update(updater.getRemoteVersion());
							}
						};
						thread.start();

					}
				}
			});
			add(actionButton).align(Align.right);
			row();
		}

		public void finishedUpdating () {
			versionLabel.setText("Version: " + updater.currentVersion()
				+ "/" + updater.getRemoteVersion());
			actionButton.setText("Uninstall");
		}

		public void gotRemoteVersion () {
			versionLabel.setText("Version: " + updater.currentVersion()
				+ "/" + updater.getRemoteVersion());
			actionButton
				.setText(updater.isNewVersionAvalible() ? "Update"
					: updater.currentVersion().equals("") ? "Install"
						: "Uninstall");
			actionButton.setDisabled(false);
		}

	}

}
